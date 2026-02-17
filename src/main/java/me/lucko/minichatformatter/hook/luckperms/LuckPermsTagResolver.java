package me.lucko.minichatformatter.hook.luckperms;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.lucko.minichatformatter.context.ChatContext;
import me.lucko.minichatformatter.format.FormatUtil;
import me.lucko.minichatformatter.hook.luckperms.LuckPermsHook.ChatData;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record LuckPermsTagResolver(LuckPermsHook luckPerms) implements TagResolver {
    private static final String PREFIX = "prefix";
    private static final String SUFFIX = "suffix";
    private static final String META = "meta";

    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        if (!has(name)) {
            return null;
        }

        PlayerRef sender = ctx.targetAsType(ChatContext.class).sender();
        ChatData chatData = this.luckPerms.getChatData(sender);
        if (chatData == null) {
            return FormatUtil.EMPTY;
        }

        return switch (name) {
            case PREFIX -> {
                FormatUtil.ensureNoArguments(name, arguments, ctx);
                String value = chatData.getPrefix();
                yield FormatUtil.parseFormattedText(value);
            }
            case SUFFIX -> {
                FormatUtil.ensureNoArguments(name, arguments, ctx);
                String value = chatData.getSuffix();
                yield FormatUtil.parseFormattedText(value);
            }
            case META -> {
                String metaKey = arguments.popOr("Meta tag requires a 'meta key' argument").value();
                FormatUtil.ensureNoArguments(name, arguments, ctx);

                String value = chatData.getMeta(metaKey);
                yield FormatUtil.parseFormattedText(value);
            }
            default -> null;
        };
    }

    @Override
    public boolean has(@NotNull String name) {
        return PREFIX.equalsIgnoreCase(name) || SUFFIX.equalsIgnoreCase(name) || META.equalsIgnoreCase(name);
    }
}
