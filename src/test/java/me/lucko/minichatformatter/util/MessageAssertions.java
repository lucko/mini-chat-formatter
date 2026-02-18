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

package me.lucko.minichatformatter.util;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.server.core.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Useful JUnit assertions for {@link Message}s.
 */
public enum MessageAssertions {
    ;

    public static void assertMessageEquals(Message expected, Message actual) {
        // Compare the encoded JSON representation first
        // easier to debug as Message/FormattedMessage do not implement toString()
        assertEquals(
                Message.CODEC.encode(expected, new ExtraInfo()),
                Message.CODEC.encode(actual, new ExtraInfo())
        );

        // Message does not implement equals() but FormattedMessage does
        assertEquals(
                expected.getFormattedMessage(),
                actual.getFormattedMessage()
        );
    }

}
