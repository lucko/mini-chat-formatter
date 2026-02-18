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

import com.hypixel.hytale.logger.HytaleLogger;
import me.lucko.minichatformatter.hook.luckperms.LuckPermsHook;
import me.lucko.minichatformatter.hook.luckperms.LuckPermsHookImpl;
import me.lucko.minichatformatter.hook.placeholderapi.PlaceholderApiHook;
import me.lucko.minichatformatter.hook.placeholderapi.PlaceholderApiHookImpl;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents a hook into an external plugin.
 *
 * <p>This is used to conditionally initialize hooks based on whether
 * the chat format contains tags for that plugin.</p>
 *
 * @param name the plugin name
 * @param initFunction a function to initialize the hook instance
 * @param requiredClass a class that must be present for the hook to be initialized
 * @param containsTagsPredicate a predicate to check if the chat format contains tags for this plugin
 * @param <T> the hook type
 */
public record Hook<T>(
        String name,
        Supplier<? extends T> initFunction,
        String requiredClass,
        Predicate<String> containsTagsPredicate
) {

    /** Hook with LuckPerms - see {@link LuckPermsHook} */
    public static final Hook<LuckPermsHook> LUCKPERMS = new Hook<>(
            "LuckPerms",
            LuckPermsHookImpl::new,
            "net.luckperms.api.LuckPerms",
            LuckPermsHook::containsTags
    );

    /** Hook with PlaceholderAPI - see {@link PlaceholderApiHook} */
    public static final Hook<PlaceholderApiHook> PLACEHOLDER_API = new Hook<>(
            "PlaceholderAPI",
            PlaceholderApiHookImpl::new,
            "at.helpch.placeholderapi.PlaceholderAPI",
            PlaceholderApiHook::containsTags
    );

    /**
     * Initialize the hook.
     *
     * @return the hook, or null if the plugin is not present
     */
    public T init() {
        try {
            Class.forName(this.requiredClass);
            return this.initFunction.get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Initialize the hook if the chat format contains tags for this plugin.
     *
     * @param format the chat format to check for tags
     * @param logger logger for errors
     * @return the hook, or null if the plugin is not present or the format does not contain tags
     */
    public T initIfRequired(String format, HytaleLogger logger) {
        if (!this.containsTagsPredicate.test(format)) {
            return null;
        }

        T hook = init();
        if (hook == null) {
            logger.atSevere().log("Chat format contains %s placeholders, but %s is not loaded!", this.name, this.name);
        }
        return hook;
    }

}
