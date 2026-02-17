package me.lucko.minichatformatter.hook.placeholderapi;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.lucko.minichatformatter.context.ChatContext;
import me.lucko.minichatformatter.format.FormatUtil;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * A tag resolver for PlaceholderAPI placeholders.
 */
public record PlaceholderApiTagResolver(PlaceholderApiHook placeholderHook, HytaleLogger logger) implements TagResolver {
    private static final String TAG = "papi";

    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context parseCtx) throws ParsingException {
        if (!has(name)) {
            return null;
        }

        ChatContext ctx = parseCtx.targetAsType(ChatContext.class);
        String placeholder = arguments.popOr("Placeholder tag requires a 'placeholder' argument").value();
        FormatUtil.ensureNoArguments(name, arguments, parseCtx);

        String value;
        try {
            value = resolvePlaceholderValue(placeholder, ctx);
        } catch (Exception e) {
            value = "";
            this.logger.atWarning().withCause(e).log("Error while resolving placeholder for player = %s, placeholder = %s", ctx.sender(), placeholder);
        }

        return FormatUtil.parseFormattedText(value);
    }

    private String resolvePlaceholderValue(String placeholder, ChatContext ctx) {
        try {
            return resolvePlaceholderValue0(placeholder, ctx);
        } catch (IllegalStateException e) {
            if (e.getMessage().startsWith("Assert not in thread!")) {
                // Most likely, the placeholder provider tried to access ECS data :(
                // We can retry on the player's world thread & that might be more likely to succeed.
                //
                // This is a bit hacky, but for now I think it's the best way to deal with it.
                // An alternative would be to process the whole formatting operation on the world thread,
                // but then we would need to group players by world. It gets messy.
                //
                // TODO: performance may be a problem here, either if there are a lot of
                //       players (recipients) or a lot of placeholders - can we optimise?

                PlayerRef player = ctx.sender();
                Ref<EntityStore> ref = player.getReference();
                if (ref == null || !ref.isValid()) {
                    throw new IllegalStateException("Player ref is not valid", e);
                }

                World world = ref.getStore().getExternalData().getWorld();
                return CompletableFuture.supplyAsync(
                        () -> resolvePlaceholderValue0(placeholder, ctx),
                        world
                ).join();
            } else {
                throw e;
            }
        }
    }

    private String resolvePlaceholderValue0(String placeholder, ChatContext ctx) {
        if (PlaceholderApiHook.isRelationalPlaceholder(placeholder) && ctx instanceof ChatContext.Relational rCtx) {
            return this.placeholderHook.resolveRelationalPlaceholder(ctx.sender(), rCtx.recipient(), placeholder);
        } else {
            return this.placeholderHook.resolvePlaceholder(ctx.sender(), placeholder);
        }
    }

    @Override
    public boolean has(@NotNull String name) {
        return TAG.equalsIgnoreCase(name);
    }
}
