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
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.lucko.minichatformatter.hook.luckperms.LuckPermsHook;
import me.lucko.minichatformatter.hook.luckperms.LuckPermsTagResolver;
import me.lucko.minichatformatter.hook.placeholderapi.PlaceholderApiHook;
import me.lucko.minichatformatter.hook.placeholderapi.PlaceholderApiTagResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static me.lucko.minichatformatter.util.MessageAssertions.assertMessageEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class MiniChatFormatterTest {

    private static final HytaleLogger LOGGER = HytaleLogger.getLogger();

    @Mock private PlayerRef sender;
    @Mock private PlayerRef recipient;

    @BeforeEach
    void init() {
        lenient().when(this.sender.getUsername()).thenReturn("sender");
        lenient().when(this.recipient.getUsername()).thenReturn("recipient");
    }

    // =========================================================================================
    // Basic
    // =========================================================================================

    @Test
    void testBasic() {
        MiniChatFormatter formatter = createFormatter("<username>: <message>");
        assertEquals("<username>: <message>", formatter.getFormat());
        assertFalse(formatter.isFormatRelational());

        Message actual = formatter.format(this.sender, "hello!");
        Message expected = Message.raw("sender: hello!");
        assertMessageEquals(expected, actual);
    }

    // =========================================================================================
    // Formatting (MiniMessage built-in tags)
    // =========================================================================================

    @Test
    void testColorFormatting() {
        MiniChatFormatter formatter = createFormatter("<red><username>: <message>");
        Message actual = formatter.format(this.sender, "hello!");
        Message expected = Message.raw("sender: hello!").color("#FF5555");
        assertMessageEquals(expected, actual);
    }

    @Test
    void testColorAndStyleFormatting() {
        MiniChatFormatter formatter = createFormatter("<red><bold><username>: <message>");
        Message actual = formatter.format(this.sender, "hello!");
        Message expected = Message.raw("sender: hello!").color("#FF5555").bold(true);
        assertMessageEquals(expected, actual);
    }

    @Test
    void testPartialFormatting() {
        MiniChatFormatter formatter = createFormatter("<bold><username></bold>: <message>");
        Message actual = formatter.format(this.sender, "hello!");
        Message expected = Message.raw("").insertAll(
                Message.raw("sender").bold(true),
                Message.raw(": hello!")
        );
        assertMessageEquals(expected, actual);
    }

    // =========================================================================================
    // PlaceholderAPI tags
    // =========================================================================================

    @ParameterizedTest
    @ValueSource(strings = {
            "[%player_world%] <username>: <message>",
            "[{player_world}] <username>: <message>"
    })
    void testPlaceholderApi(String format) {
        MiniChatFormatter formatter = createFormatter(format);
        assertEquals("[<papi:'player_world'>] <username>: <message>", formatter.getFormat());
        assertFalse(formatter.isFormatRelational());

        Message actual = formatter.format(this.sender, "hello!");
        Message expected = Message.raw("[*PLACEHOLDER 'player_world' FOR 'sender'*] sender: hello!");
        assertMessageEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "<username> %test_color% <message>",
            "<username> %test_color_legacy% <message>"
    })
    void testPlaceholderApiRecursiveFormatting(String format) {
        MiniChatFormatter formatter = createFormatter(format);
        Message actual = formatter.format(this.sender, "hello!");
        Message expected = Message.raw("sender ").insertAll(
                Message.raw("abc hello!").color("#FF5555")
        );
        assertMessageEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "%test% %rel_test% <username>: <message>",
            "{test} {rel_test} <username>: <message>"
    })
    void testPlaceholderApiRelational(String format) {
        MiniChatFormatter formatter = createFormatter(format);
        assertEquals("<papi:'test'> <papi:'rel_test'> <username>: <message>", formatter.getFormat());
        assertTrue(formatter.isFormatRelational());

        Message actual = formatter.format(this.sender, this.recipient, "hello!");
        Message expected = Message.raw("*PLACEHOLDER 'test' FOR 'sender'* *PLACEHOLDER 'rel_test' FOR 'sender' -> 'recipient'* sender: hello!");
        assertMessageEquals(expected, actual);
    }

    // =========================================================================================
    // LuckPerms tags
    // =========================================================================================

    @Test
    void testLuckPerms() {
        MiniChatFormatter formatter = createFormatter("<prefix><username><suffix>: <message>");
        Message actual = formatter.format(this.sender, "hello!");
        Message expected = Message.raw("*PREFIX FOR 'sender'*sender*SUFFIX FOR 'sender'*: hello!");
        assertMessageEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "<username> <meta:test_color> <message>",
            "<username> <meta:test_color_legacy> <message>"
    })
    void testLuckPermsRecursiveFormatting(String format) {
        MiniChatFormatter formatter = createFormatter(format);
        Message actual = formatter.format(this.sender, "hello!");
        Message expected = Message.raw("sender ").insertAll(
                Message.raw("abc hello!").color("#FF5555")
        );
        assertMessageEquals(expected, actual);
    }

    // =========================================================================================
    // Utilities
    // =========================================================================================

    private MiniChatFormatter createFormatter(String format) {
        return new MiniChatFormatter(format, LOGGER, (_, tagResolverBuilder) -> {
            tagResolverBuilder.resolver(new PlaceholderApiTagResolver(TestPlaceholderApiHook.INSTANCE, LOGGER));
            tagResolverBuilder.resolver(new LuckPermsTagResolver(TestLuckPermsHook.INSTANCE));
        });
    }

    enum TestPlaceholderApiHook implements PlaceholderApiHook {
        INSTANCE;

        @Override
        public String resolvePlaceholder(PlayerRef sender, String placeholder) {
            if (placeholder.equals("test_color")) {
                return "<red>abc";
            }
            if (placeholder.equals("test_color_legacy")) {
                return "&cabc";
            }
            return "*PLACEHOLDER '%s' FOR '%s'*".formatted(placeholder, sender.getUsername());
        }

        @Override
        public String resolveRelationalPlaceholder(PlayerRef sender, PlayerRef recipient, String placeholder) {
            return "*PLACEHOLDER '%s' FOR '%s' -> '%s'*".formatted(placeholder, sender.getUsername(), recipient.getUsername());
        }
    }

    enum TestLuckPermsHook implements LuckPermsHook {
        INSTANCE;

        @Override
        public ChatData getChatData(PlayerRef playerRef) {
            return new ChatData() {
                @Override
                public String getPrefix() {
                    return "*PREFIX FOR '%s'*".formatted(playerRef.getUsername());
                }

                @Override
                public String getSuffix() {
                    return "*SUFFIX FOR '%s'*".formatted(playerRef.getUsername());
                }

                @Override
                public String getMeta(String key) {
                    if (key.equals("test_color")) {
                        return "<red>abc";
                    }
                    if (key.equals("test_color_legacy")) {
                        return "&cabc";
                    }
                    return "*META '%s' FOR '%s'*".formatted(key, playerRef.getUsername());
                }
            };
        }
    }

}
