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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * Utilities for formatting MiniMessage text.
 */
public enum FormatUtil {
    ;

    /** Empty tag **/
    public static final Tag EMPTY = Tag.selfClosingInserting(Component.empty());

    public static void ensureNoArguments(String name, ArgumentQueue arguments, Context ctx) {
        if (arguments.hasNext()) {
            throw ctx.newException("Tag '<" + name + ">' was given more arguments than required");
        }
    }

    public static Tag parseFormattedText(String value) {
        if (value == null || value.isEmpty()) {
            return EMPTY;
        }

        char legacyChar = findLegacyColorCodes(value);
        if (legacyChar != 0) {
            TextComponent component = LegacyComponentSerializer.legacy(legacyChar).deserialize(value);
            return Tag.inserting(component);
        } else {
            return Tag.preProcessParsed(value);
        }
    }

    /**
     * Checks if the input string contains any legacy color codes (either '&x' or '§x').
     * If it does, it returns the character used for the color codes. If not, it returns 0.
     */
    @VisibleForTesting
    static char findLegacyColorCodes(String string) {
        final char[] charArray = string.toCharArray();
        for (int i = 0; i < charArray.length - 1; i++) {
            if ((charArray[i] == LegacyComponentSerializer.AMPERSAND_CHAR
                    || charArray[i] == LegacyComponentSerializer.SECTION_CHAR)
                    && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(charArray[i + 1]) > -1) {
                return charArray[i];
            }
        }
        return 0;
    }
}
