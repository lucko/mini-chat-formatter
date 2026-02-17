package me.lucko.minichatformatter.context;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import net.kyori.adventure.pointer.Pointered;

/**
 * Context object passed down to MiniMessage TagResolvers.
 */
public interface ChatContext extends Pointered {

    /**
     * The player who sent the chat message
     */
    PlayerRef sender();

    /** Simple chat context, the message is formatted based on who sent the message only */
    record Simple(PlayerRef sender) implements ChatContext { }

    /** Relational chat context, the message can be formatted based on the relationship between the sender and recipient */
    record Relational(PlayerRef sender, PlayerRef recipient) implements ChatContext { }

}
