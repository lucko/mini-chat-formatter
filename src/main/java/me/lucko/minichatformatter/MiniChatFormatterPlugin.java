package me.lucko.minichatformatter;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import me.lucko.minichatformatter.event.RelationalPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class MiniChatFormatterPlugin extends JavaPlugin {
    private final Config<MiniChatFormatterConfig> config = withConfig(MiniChatFormatterConfig.CODEC);
    private MiniChatFormatter formatter;

    public MiniChatFormatterPlugin(@NotNull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.config.save();
    }

    @Override
    protected void start() {
        this.formatter = new MiniChatFormatter(this.config.get().getFormat(), getLogger());

        // register our listener at priority 1, just after NORMAL. If a server has this plugin installed,
        // it's quite likely the admin actually does want to use it for formatting
        getEventRegistry().registerGlobal((short) 1, PlayerChatEvent.class, this::onPlayerChat);
        getEventRegistry().registerGlobal(EventPriority.LAST, PlayerChatEvent.class, this::onPlayerChatLast);

        getCommandRegistry().registerCommand(new MiniChatFormatterCommand(this));
    }

    public void reloadConfig() {
        this.config.load().join();
        this.formatter = new MiniChatFormatter(this.config.get().getFormat(), getLogger());
    }

    private void onPlayerChat(PlayerChatEvent e) {
        e.setFormatter(this.formatter);
    }

    private void onPlayerChatLast(PlayerChatEvent e) {
        PlayerChatEvent.Formatter formatter = e.getFormatter();
        if (e.isCancelled() || !(formatter instanceof MiniChatFormatter miniFormatter)) {
            return;
        }

        // if our formatter is chosen but the format is relational, we need to convert & dispatch as a relational event
        if (miniFormatter.isFormatRelational()) {
            e.setCancelled(true);

            RelationalPlayerChatEvent relationalEvent = new RelationalPlayerChatEvent(e.getSender(), e.getTargets(), e.getContent(), miniFormatter);
            relationalEvent.dispatch();
        }
    }

}
