package me.lucko.minichatformatter;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.lucko.minichatformatter.context.ChatContext;
import me.lucko.minichatformatter.event.RelationalPlayerChatEvent;
import me.lucko.minichatformatter.format.MessageUtil;
import me.lucko.minichatformatter.hook.luckperms.LuckPermsHook;
import me.lucko.minichatformatter.hook.luckperms.LuckPermsTagResolver;
import me.lucko.minichatformatter.hook.placeholderapi.PlaceholderApiHook;
import me.lucko.minichatformatter.hook.placeholderapi.PlaceholderApiTagResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

/**
 * A chat formatter that uses MiniMessage for formatting.
 */
public class MiniChatFormatter implements PlayerChatEvent.Formatter, RelationalPlayerChatEvent.Formatter {
    private final HytaleLogger logger;
    private final String format;
    private final boolean isFormatRelational;
    private final MiniMessage miniMessage;

    public MiniChatFormatter(String format, HytaleLogger logger) {
        this.logger = logger;

        // PlaceholderAPI
        PlaceholderApiHook placeholderHook;
        if (PlaceholderApiHook.containsPlaceholders(format)) {
            this.format = PlaceholderApiHook.transformFormat(format);
            placeholderHook = PlaceholderApiHook.init();
            if (placeholderHook == null) {
                this.logger.atSevere().log("Chat format contains PlaceholderAPI placeholders, but PlaceholderAPI is not loaded!");
            }
        } else {
            this.format = format;
            placeholderHook = null;
        }

        this.isFormatRelational = this.format.contains("<papi:rel_");

        // LuckPerms
        LuckPermsHook luckPermsHook;
        if (LuckPermsHook.containsPlaceholders(format)) {
            luckPermsHook = LuckPermsHook.init();
            if (luckPermsHook == null) {
                this.logger.atSevere().log("Chat format contains LuckPerms placeholders, but LuckPerms is not loaded!");
            }
        } else {
            luckPermsHook = null;
        }

        this.miniMessage = MiniMessage.builder()
                .editTags(tags -> {
                    if (luckPermsHook != null) {
                        tags.resolver(new LuckPermsTagResolver(luckPermsHook));
                    }
                    if (placeholderHook != null) {
                        tags.resolver(new PlaceholderApiTagResolver(placeholderHook, logger));
                    }
                })
                .build();
    }

    public boolean isFormatRelational() {
        return this.isFormatRelational;
    }

    private Message format(ChatContext ctx, String message) {
        try {
            Component component = this.miniMessage.deserialize(
                    this.format,
                    ctx,
                    Placeholder.unparsed("username", ctx.sender().getUsername()),
                    Placeholder.unparsed("message", message)
            );
            return MessageUtil.toHytaleMessage(component);
        } catch (RuntimeException e) {
            this.logger.atWarning().withCause(e).log("Failed to format chat message");
            throw e;
        }
    }

    @Override
    public @NotNull Message format(@NotNull PlayerRef sender, @NotNull String message) {
        return format(new ChatContext.Simple(sender), message);
    }

    @Override
    public @NotNull Message format(@NotNull PlayerRef sender, @NotNull PlayerRef recipient, @NotNull String message) {
        return format(new ChatContext.Relational(sender, recipient), message);
    }

}
