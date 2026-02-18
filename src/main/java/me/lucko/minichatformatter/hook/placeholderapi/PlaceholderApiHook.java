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

package me.lucko.minichatformatter.hook.placeholderapi;

import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.regex.Pattern;

/**
 * Integration with PlaceholderAPI.
 */
public interface PlaceholderApiHook {

    Pattern TAG_PATTERN = Pattern.compile("<papi:'([^']+)'>");
    Pattern RELATIONAL_TAG_PATTERN = Pattern.compile("<papi:'rel_([^']+)'>");

    Pattern PERCENT_PLACEHOLDER_PATTERN = Pattern.compile("[%]([^%]+)[%]");
    Pattern BRACKET_PLACEHOLDER_PATTERN = Pattern.compile("[{]([^{}]+)[}]");

    /**
     * Checks if the input string contains any PlaceholderAPI tags.
     *
     * @param input the string to check
     * @return true if the string contains PlaceholderAPI tags, false otherwise
     */
    static boolean containsTags(String input) {
        return TAG_PATTERN.matcher(input).find();
    }

    /**
     * Checks if the input string contains any relational PlaceholderAPI tags
     * (i.e. tags starting with "rel_").
     *
     * @param input the string to check
     * @return true if the string contains relational PlaceholderAPI tags, false otherwise
     */
    static boolean containsRelationalTags(String input) {
        return RELATIONAL_TAG_PATTERN.matcher(input).find();
    }

    /**
     * Checks if the input string contains any placeholders.
     *
     * @param input the string to check
     * @return true if the string contains placeholders, false otherwise
     */
    static boolean containsPlaceholders(String input) {
        return PERCENT_PLACEHOLDER_PATTERN.matcher(input).find() || BRACKET_PLACEHOLDER_PATTERN.matcher(input).find();
    }

    /**
     * Transforms the format string, replacing placeholders in {@code %placeholder%}
     * or {@code {placeholder}} format with {@code <papi:'placeholder'>}.
     *
     * <p>This allows the placeholders to be resolved by the MiniMessage parser.</p>
     *
     * @param input the string to transform
     * @return the transformed string
     */
    static String transformFormat(String input) {
        input = PERCENT_PLACEHOLDER_PATTERN.matcher(input).replaceAll("<papi:'$1'>");
        input = BRACKET_PLACEHOLDER_PATTERN.matcher(input).replaceAll("<papi:'$1'>");
        return input;
    }

    /**
     * Checks if the placeholder is a relational placeholder (i.e. starts with "rel_").
     *
     * @param placeholder the placeholder to check
     * @return true if the placeholder is a relational placeholder, false otherwise
     */
    static boolean isRelationalPlaceholder(String placeholder) {
        return placeholder.startsWith("rel_");
    }

    String resolvePlaceholder(PlayerRef sender, String placeholder);

    String resolveRelationalPlaceholder(PlayerRef sender, PlayerRef recipient, String placeholder);

}
