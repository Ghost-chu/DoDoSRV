package com.ghostchu.plugins.dodosrv.listener.bukkit;

import com.ghostchu.plugins.dodosrv.DoDoSRV;
import com.ghostchu.plugins.dodosrv.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BukkitListener implements Listener {
    private final DoDoSRV plugin;
    private String requriedPrefix;

    public BukkitListener(DoDoSRV plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        requriedPrefix = plugin.getConfig().getString("feature.minecraft-to-dodo.enable.require-prefix");
        if (StringUtils.isBlank(requriedPrefix)) requriedPrefix = null;
        plugin.getLogger().info("Bukkit Listener Registered");
    }

    private String getAvatarLink(UUID uuid) {
        return Util.fillArgs(plugin.getConfig().getString("avatar-url"), uuid.toString(), "32");
    }

    private boolean allowForward(@NotNull String key) {
        return plugin.getConfig().getBoolean("feature.minecraft-to-dodo.forward." + key, false);

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        if (!allowForward("join")) {
            return;
        }
        String message = plugin.text().of("player-join-message",
                event.getPlayer().getDisplayName(), getAvatarLink(event.getPlayer().getUniqueId())).plain();
        plugin.sendMessageToDefChannel(message);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        if (!allowForward("quit")) {
            return;
        }
        String message = plugin.text().of("player-quit-message", event.getPlayer().getDisplayName(),
                getAvatarLink(event.getPlayer().getUniqueId())).plain();
        plugin.sendMessageToDefChannel(message);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        if (!allowForward("death")) {
            return;
        }
        Location loc = event.getEntity().getLocation();
        String msg = plugin.text().of("player-death-message",
                event.getEntity().getDisplayName(),
                event.getDeathMessage(),
                loc.getWorld().getName() + " " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ(),
                getAvatarLink(event.getEntity().getUniqueId())).plain();
        plugin.sendMessageToDefChannel(msg);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAdvancementEvent(PlayerAdvancementDoneEvent event) {
        if (!allowForward("advancement")) {
            return;
        }
        AdvancementDisplay display = event.getAdvancement().getDisplay();
        if (display == null) return;
        if (!display.shouldShowToast()) return;
        if (!display.shouldAnnounceChat()) return;

//        String title = switch (display.getType()) {
//            case TASK, GOAL -> "(font)" + display.getTitle() + "(font)" + "[success]";
//            case CHALLENGE -> "(font)" + display.getTitle() + "(font)" + "[purple]";
//        };
        String title = "`"+display.getTitle()+"`";
        String description = ">"+display.getDescription();

        String type = plugin.text().of("advancement-message." + display.getType().name()).plain();

        String msg = plugin.text().of("player-unlock-advancement-message",
                event.getPlayer().getDisplayName(),
                type,
                title,
                description,
                getAvatarLink(event.getPlayer().getUniqueId())).plain();
        plugin.sendMessageToDefChannel(msg);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!allowForward("chat")) {
            return;
        }
        if (requriedPrefix != null) {
            if (!event.getMessage().startsWith(requriedPrefix)) {
                return;
            }
        }
        plugin.sendMessageToDefChannel(plain(plugin.text().of("minecraft-to-dodo-format", event.getPlayer().getDisplayName(), event.getMessage()).component()));
    }

    private String plain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
