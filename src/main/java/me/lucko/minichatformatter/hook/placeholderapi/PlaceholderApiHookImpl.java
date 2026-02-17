package me.lucko.minichatformatter.hook.placeholderapi;

import at.helpch.placeholderapi.PlaceholderAPI;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class PlaceholderApiHookImpl implements PlaceholderApiHook {

    @Override
    public String resolvePlaceholder(PlayerRef sender, String placeholder) {
        return PlaceholderAPI.setPlaceholders(sender, "%" + placeholder + "%");
    }

    @Override
    public String resolveRelationalPlaceholder(PlayerRef sender, PlayerRef recipient, String placeholder) {
        return PlaceholderAPI.setRelationalPlaceholders(sender, recipient, "%" + placeholder + "%");
    }
}
