package me.lucko.minichatformatter.event;

import com.hypixel.hytale.event.IAsyncEvent;
import com.hypixel.hytale.event.ICancellable;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.console.ConsoleModule;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.MessageUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Similar to {@link PlayerChatEvent}, but supporting per-target (per-recipient) formatting.
 */
public class RelationalPlayerChatEvent implements IAsyncEvent<String>, ICancellable {
    private static final IEventDispatcher<RelationalPlayerChatEvent, CompletableFuture<RelationalPlayerChatEvent>> DISPATCHER = HytaleServer.get().getEventBus().dispatchForAsync(RelationalPlayerChatEvent.class);

    private PlayerRef sender;
    private List<PlayerRef> targets;
    private String content;
    private Formatter formatter;
    private boolean cancelled;

    public RelationalPlayerChatEvent(PlayerRef sender, List<PlayerRef> targets, String content, Formatter formatter) {
        this.sender = sender;
        this.targets = targets;
        this.content = content;
        this.formatter = formatter;
        this.cancelled = false;
    }

    public void dispatch() {
        DISPATCHER.dispatch(this).whenComplete((e, ex) -> {
            if (ex != null) {
                HytaleLogger.getLogger().atSevere().withCause(ex).log("An error occurred while dispatching RelationalPlayerChatEvent for player %s", this.sender.getUsername());
                return;
            }

            if (e.isCancelled()) {
                return;
            }

            boolean sentToConsole = false;
            for (PlayerRef target : e.getTargets()) {
                Message message = e.getFormatter().format(e.getSender(), target, e.getContent());
                target.sendMessage(message);

                if (!sentToConsole) {
                    HytaleLogger.getLogger().atInfo().log(MessageUtil.toAnsiString(message).toAnsi(ConsoleModule.get().getTerminal()));
                    sentToConsole = true;
                }
            }
        });
    }

    public PlayerRef getSender() {
        return this.sender;
    }

    public void setSender(PlayerRef sender) {
        this.sender = sender;
    }

    public List<PlayerRef> getTargets() {
        return this.targets;
    }

    public void setTargets(List<PlayerRef> targets) {
        this.targets = targets;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Formatter getFormatter() {
        return this.formatter;
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public String toString() {
        return "RelationalPlayerChatEvent{" +
                "sender=" + this.sender +
                ", targets=" + this.targets +
                ", content='" + this.content + '\'' +
                ", formatter=" + this.formatter +
                ", cancelled=" + this.cancelled +
                '}';
    }

    public interface Formatter {
        Message format(PlayerRef sender, PlayerRef recipient, String message);
    }

}
