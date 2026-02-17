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
