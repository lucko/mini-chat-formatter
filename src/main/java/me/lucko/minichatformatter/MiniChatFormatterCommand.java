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

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class MiniChatFormatterCommand extends AbstractCommandCollection {

    public MiniChatFormatterCommand(MiniChatFormatterPlugin plugin) {
        super("minichatformatter", "mini-chat-formatter commands");
        requirePermission("minichatformatter.reload");
        addSubCommand(new ReloadCommand(plugin));
    }

    private static final class ReloadCommand extends AbstractAsyncCommand {
        private final MiniChatFormatterPlugin plugin;

        ReloadCommand(MiniChatFormatterPlugin plugin) {
            super("reload", "Reload the plugin configuration");
            requirePermission("minichatformatter.reload");
            this.plugin = plugin;
        }

        @Override
        protected @NotNull CompletableFuture<Void> executeAsync(@NotNull CommandContext ctx) {
            this.plugin.reloadConfig();
            MiniChatFormatter formatter = this.plugin.getFormatter();

            ctx.sendMessage(Message.raw("[mini-chat-formatter] Reloaded! :)").color(Color.YELLOW));
            ctx.sendMessage(Message.raw("[mini-chat-formatter] The new format is:").color(Color.YELLOW));
            ctx.sendMessage(Message.raw(formatter.getFormat()));

            // If the sender is a player, show them an example of how the chat should now look with the new format
            if (ctx.isPlayer()) {
                Ref<EntityStore> ref = ctx.senderAsPlayerRef();
                if (ref != null && ref.isValid()) {
                    Store<EntityStore> store = ref.getStore();
                    World world = store.getExternalData().getWorld();

                    return this.runAsync(ctx, () -> {
                        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                        if (playerRef == null) {
                            return;
                        }

                        Message message = formatter.format(playerRef, playerRef, "Hello!");
                        ctx.sendMessage(Message.raw("[mini-chat-formatter] If you sent 'Hello!' in chat, it should now look like this:").color(Color.YELLOW));
                        ctx.sendMessage(message);
                    }, world);
                }
            }

            return CompletableFuture.completedFuture(null);
        }
    }
}
