package me.lucko.minichatformatter;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
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
            ctx.sendMessage(Message.raw("[mini-chat-formatter] Reloaded :)").color(Color.YELLOW));
            return CompletableFuture.completedFuture(null);
        }
    }
}
