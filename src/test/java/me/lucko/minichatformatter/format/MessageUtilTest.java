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
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static me.lucko.minichatformatter.util.MessageAssertions.assertMessageEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageUtilTest {

    @Test
    void testConversion() {
        Component component = Component.text("hello")
                .color(NamedTextColor.AQUA)
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.OBFUSCATED, true) // should be ignored
                .append(
                        Component.text("child1").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, false),
                        Component.text("child2").decoration(TextDecoration.ITALIC, true),
                        Component.text("child3")
                                .clickEvent(ClickEvent.openUrl("https://example.com"))
                                .hoverEvent(HoverEvent.showText(Component.text("hello"))), // should be ignored
                        Component.translatable("child4").color(NamedTextColor.BLUE)
                );

        Message expected = Message.raw("hello")
                .color("#55FFFF")
                .bold(true)
                .insertAll(List.of(
                        Message.raw("child1").color("#FF5555").bold(false),
                        Message.raw("child2").italic(true),
                        Message.raw("child3").link("https://example.com"),
                        Message.translation("child4").color("#5555FF")
                ));

        Message actual = MessageUtil.toHytaleMessage(component);
        assertMessageEquals(expected, actual);
    }

    @Test
    void testUnsupportedComponent() {
        Component component = Component.keybind("test");
        assertThrows(UnsupportedOperationException.class, () -> MessageUtil.toHytaleMessage(component));
    }
}
