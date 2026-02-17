package me.lucko.minichatformatter.hook.placeholderapi;

import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.regex.Pattern;

/**
 * Integration with PlaceholderAPI.
 */
public interface PlaceholderApiHook {

    static PlaceholderApiHook init() {
        try {
            Class.forName("at.helpch.placeholderapi.PlaceholderAPI");
            return new PlaceholderApiHookImpl();
        } catch (Exception e) {
            return null;
        }
    }

    Pattern PERCENT_PLACEHOLDER_PATTERN = Pattern.compile("[%]([^%]+)[%]");
    Pattern BRACKET_PLACEHOLDER_PATTERN = Pattern.compile("[{]([^{}]+)[}]");

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
