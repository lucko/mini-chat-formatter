package me.lucko.minichatformatter.format;

import com.hypixel.hytale.server.core.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Utility for converting between Adventure Components and Hytale Messages.
 */
public enum MessageUtil {
    ;

    public static Message toHytaleMessage(Component component) {
        if (!(component instanceof TextComponent text)) {
            throw new UnsupportedOperationException("Unsupported component type: " + component.getClass());
        }

        Message message = Message.raw(text.content());

        TextColor color = text.color();
        if (color != null) {
            message.color(color.asHexString());
        }

        TextDecoration.State bold = text.decoration(TextDecoration.BOLD);
        if (bold != TextDecoration.State.NOT_SET) {
            message.bold(bold == TextDecoration.State.TRUE);
        }

        TextDecoration.State italic = text.decoration(TextDecoration.ITALIC);
        if (italic != TextDecoration.State.NOT_SET) {
            message.italic(italic == TextDecoration.State.TRUE);
        }

        ClickEvent clickEvent = text.clickEvent();
        if (clickEvent != null && clickEvent.action() == ClickEvent.Action.OPEN_URL) {
            message.link(clickEvent.value());
        }

        message.insertAll(text.children().stream()
                .map(MessageUtil::toHytaleMessage)
                .toList()
        );
        return message;
    }
}
