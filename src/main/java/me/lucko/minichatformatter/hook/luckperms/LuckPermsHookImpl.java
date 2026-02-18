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
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.platform.PlayerAdapter;

public class LuckPermsHookImpl implements LuckPermsHook {
    private final PlayerAdapter<PlayerRef> playerAdapter;

    public LuckPermsHookImpl() {
        LuckPerms luckPerms = LuckPermsProvider.get();
        this.playerAdapter = luckPerms.getPlayerAdapter(PlayerRef.class);
    }

    @Override
    public ChatData getChatData(PlayerRef playerRef) {
        CachedMetaData metaData;
        try {
            metaData = this.playerAdapter.getMetaData(playerRef);
        } catch (IllegalStateException e) { // player not loaded
            return null;
        }
        return new ChatDataImpl(metaData);
    }

    private record ChatDataImpl(CachedMetaData data) implements ChatData {
        @Override
        public String getPrefix() {
            return this.data.getPrefix();
        }

        @Override
        public String getSuffix() {
            return this.data.getSuffix();
        }

        @Override
        public String getMeta(String key) {
            return this.data.getMetaValue(key);
        }
    }
}
