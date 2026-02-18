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

package me.lucko.minichatformatter.hook.luckperms;

import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.regex.Pattern;

/**
 * Integration with LuckPerms.
 */
public interface LuckPermsHook {

    ChatData getChatData(PlayerRef playerRef);

    interface ChatData {
        String getPrefix();
        String getSuffix();
        String getMeta(String key);
    }

    Pattern TAG_PATTERN = Pattern.compile("<(prefix|suffix|meta:.+)>");

    /**
     * Checks if the input string contains any LuckPerms tags.
     *
     * @param input the string to check
     * @return true if the string contains LP tags, false otherwise
     */
    static boolean containsTags(String input) {
        return TAG_PATTERN.matcher(input).find();
    }

}
