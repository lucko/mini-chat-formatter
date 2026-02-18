/*
 * This file is part of mini-chat-formatter, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.minichatformatter;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.lucko.minichatformatter.context.ChatContext;
import me.lucko.minichatformatter.format.MessageUtil;
import me.lucko.minichatformatter.hook.Hook;
import me.lucko.minichatformatter.hook.luckperms.LuckPermsHook;
import me.lucko.minichatformatter.hook.luckperms.LuckPermsTagResolver;
import me.lucko.minichatformatter.hook.placeholderapi.PlaceholderApiHook;
import me.lucko.minichatformatter.hook.placeholderapi.PlaceholderApiTagResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

/**
 * A chat formatter that uses MiniMessage for formatting.
 */
public class MiniChatFormatter implements PlayerChatEvent.Formatter {
    private final HytaleLogger logger;
    private final String format;
    private final boolean isFormatRelational;
    private final MiniMessage miniMessage;

    public MiniChatFormatter(String format, HytaleLogger logger) {
        this(format, logger, (fmt, builder) -> {
            PlaceholderApiHook placeholderHook = Hook.PLACEHOLDER_API.initIfRequired(fmt, logger);
            LuckPermsHook luckPermsHook = Hook.LUCKPERMS.initIfRequired(fmt, logger);

            if (luckPermsHook != null) {
                builder.resolver(new LuckPermsTagResolver(luckPermsHook));
            }
            if (placeholderHook != null) {
                builder.resolver(new PlaceholderApiTagResolver(placeholderHook, logger));
            }
        });
    }

    public MiniChatFormatter(String format, HytaleLogger logger, TagConfigFunction configFunction) {
        this.logger = logger;
        this.format = PlaceholderApiHook.transformFormat(format);
        this.isFormatRelational = PlaceholderApiHook.containsRelationalTags(this.format);
        this.miniMessage = MiniMessage.builder()
                .editTags(builder -> configFunction.apply(this.format, builder))
                .build();
    }

    public String getFormat() {
        return this.format;
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

    public @NotNull Message format(@NotNull PlayerRef sender, @NotNull PlayerRef recipient, @NotNull String message) {
        return format(new ChatContext.Relational(sender, recipient), message);
    }

    public interface TagConfigFunction {
        void apply(String format, TagResolver.Builder tagResolverBuilder);
    }

}
