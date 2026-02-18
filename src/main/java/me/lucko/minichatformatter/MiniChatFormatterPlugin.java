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

package me.lucko.minichatformatter;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.console.ConsoleModule;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.util.MessageUtil;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class MiniChatFormatterPlugin extends JavaPlugin {

    /** The plugin config. */
    private final Config<MiniChatFormatterConfig> config = withConfig(MiniChatFormatterConfig.CODEC);

    /** If we should check that we are handling the PlayerChatEvent, only logs for the first observed event */
    private final AtomicBoolean shouldCheck = new AtomicBoolean(true);

    /** Our chat formatter */
    private MiniChatFormatter formatter;

    public MiniChatFormatterPlugin(@NotNull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        // Save default values
        this.config.save();
    }

    @Override
    protected void start() {
        this.formatter = new MiniChatFormatter(this.config.get().getFormat(), getLogger());

        // Register our listener at priority 1, just after NORMAL. If a server has this plugin installed,
        // it's quite likely the admin actually does want to use it for formatting
        getEventRegistry().registerGlobal((short) 1, PlayerChatEvent.class, this::onPlayerChat);
        getEventRegistry().registerGlobal(EventPriority.LAST, PlayerChatEvent.class, this::onPlayerChatLast);

        getCommandRegistry().registerCommand(new MiniChatFormatterCommand(this));
    }

    public MiniChatFormatter getFormatter() {
        return this.formatter;
    }

    public void reloadConfig() {
        this.config.load().join();
        this.formatter = new MiniChatFormatter(this.config.get().getFormat(), getLogger());
        this.shouldCheck.set(true);
    }

    private void onPlayerChat(PlayerChatEvent e) {
        e.setFormatter(this.formatter);
    }

    private void onPlayerChatLast(PlayerChatEvent e) {
        PlayerChatEvent.Formatter formatter = e.getFormatter();

        // perform a one-time check to see if another plugin is cancelling the event or setting
        // a different formatter. If so, log a warning message
        if (this.shouldCheck.compareAndSet(true, false)) {
            if (e.isCancelled()) {
                getLogger().atWarning().log("Another plugin has cancelled (the first seen) PlayerChatEvent. " +
                        "mini-chat-formatter will not apply formatting.");
            } else if (!(formatter instanceof MiniChatFormatter)) {
                getLogger().atWarning().log("Another plugin is setting the PlayerChatEvent formatter. " +
                        "mini-chat-formatter will not apply formatting. Formatter = %s", formatter.getClass().getName());
            }
        }

        // if our formatter is chosen but the format is relational, we need to cancel the event and
        // apply formatting per recipient (target) instead of globally.
        if (!e.isCancelled() && formatter instanceof MiniChatFormatter miniFormatter && miniFormatter.isFormatRelational()) {
            e.setCancelled(true);

            boolean sentToConsole = false;
            for (PlayerRef recipient : e.getTargets()) {
                Message message = miniFormatter.format(e.getSender(), recipient, e.getContent());
                recipient.sendMessage(message);

                // send to the console once - use the formatting of the first recipient
                if (!sentToConsole) {
                    HytaleLogger.getLogger().atInfo().log(MessageUtil.toAnsiString(message).toAnsi(ConsoleModule.get().getTerminal()));
                    sentToConsole = true;
                }
            }
        }
    }

}
