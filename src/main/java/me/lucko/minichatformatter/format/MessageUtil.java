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

package me.lucko.minichatformatter.format;

import com.hypixel.hytale.server.core.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

/**
 * Utility for converting between Adventure Components and Hytale Messages.
 */
public enum MessageUtil {
    ;

    public static Message toHytaleMessage(Component component) {
        Message message;
        if (component instanceof TextComponent text) {
            message = Message.raw(text.content());
        } else if (component instanceof TranslatableComponent translatable) {
            message = Message.translation(translatable.key());
        } else {
            throw new UnsupportedOperationException("Unsupported component type: " + component.getClass());
        }

        TextColor color = component.color();
        if (color != null) {
            message.color(color.asHexString());
        }

        TextDecoration.State bold = component.decoration(TextDecoration.BOLD);
        if (bold != TextDecoration.State.NOT_SET) {
            message.bold(bold == TextDecoration.State.TRUE);
        }

        TextDecoration.State italic = component.decoration(TextDecoration.ITALIC);
        if (italic != TextDecoration.State.NOT_SET) {
            message.italic(italic == TextDecoration.State.TRUE);
        }

        ClickEvent clickEvent = component.clickEvent();
        if (clickEvent != null && clickEvent.action() == ClickEvent.Action.OPEN_URL) {
            message.link(clickEvent.value());
        }

        List<Message> children = component.children().stream()
                .map(MessageUtil::toHytaleMessage)
                .toList();

        if (!children.isEmpty()) {
            message.insertAll(children);
        }

        return message;
    }
}
