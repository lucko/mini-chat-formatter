package me.lucko.minichatformatter.hook.luckperms;

import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.regex.Pattern;

/**
 * Integration with LuckPerms.
 */
public interface LuckPermsHook {

    static LuckPermsHook init() {
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            return new LuckPermsHookImpl();
        } catch (Exception e) {
            return null;
        }
    }

    ChatData getChatData(PlayerRef playerRef);

    interface ChatData {
        String getPrefix();
        String getSuffix();
        String getMeta(String key);
    }

    Pattern PLACEHOLDER_PATTERN = Pattern.compile("<(prefix|suffix|meta:.+)>");

    /**
     * Checks if the input string contains any LuckPerms placeholders.
     *
     * @param input the string to check
     * @return true if the string contains placeholders, false otherwise
     */
    static boolean containsPlaceholders(String input) {
        return PLACEHOLDER_PATTERN.matcher(input).find();
    }

}
