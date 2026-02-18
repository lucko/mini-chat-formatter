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

package me.lucko.minichatformatter.hook;

import me.lucko.minichatformatter.hook.placeholderapi.PlaceholderApiHook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaceholderApiHookTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "hello <papi:'player_world'> world",
            "<papi:'player_world'>"
    })
    void testContainsTagsTrue(String input) {
        Assertions.assertTrue(PlaceholderApiHook.containsTags(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "hello",
            "",
            "papi",
            "<papi"
    })
    void testContainsTagsFalse(String input) {
        assertFalse(PlaceholderApiHook.containsTags(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "hello <papi:'rel_something'> world",
            "<papi:'rel_something'>"
    })
    void testContainersRelationalTagsTrue(String input) {
        assertTrue(PlaceholderApiHook.containsRelationalTags(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "hello",
            "<papi:'player_world'>",
            "<papi:'rel_unclosed"
    })
    void testContainsRelationalTagsFalse(String input) {
        assertFalse(PlaceholderApiHook.containsRelationalTags(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "hello %test% world",
            "hello {test} world",
            "%test%",
            "%test%%hello%"
    })
    void testContainsPlaceholdersTrue(String input) {
        assertTrue(PlaceholderApiHook.containsPlaceholders(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "hello",
            "",
            "$test",
            "{test",
            "%%",
            "{}"
    })
    void testContainsPlaceholdersFalse(String input) {
        assertFalse(PlaceholderApiHook.containsPlaceholders(input));
    }

    @Test
    void testIsRelationalPlaceholder() {
        assertTrue(PlaceholderApiHook.isRelationalPlaceholder("rel_example"));
        assertFalse(PlaceholderApiHook.isRelationalPlaceholder("example"));
    }

    @ParameterizedTest
    @CsvSource({
            "hello %abc% world {xyz}, hello <papi:'abc'> world <papi:'xyz'>",
            "%test 123%, <papi:'test 123'>",
            "%rel_example%, <papi:'rel_example'>",
    })
    void testTransformFormat(String input, String expectedOutput) {
        assertEquals(expectedOutput, PlaceholderApiHook.transformFormat(input));
    }

}
