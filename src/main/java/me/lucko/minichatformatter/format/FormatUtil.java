package me.lucko.minichatformatter.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

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
    private static char findLegacyColorCodes(String string) {
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
