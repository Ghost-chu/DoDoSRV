package com.ghostchu.plugins.dodosrv.command.bukkit.subcommand;

import com.ghostchu.plugins.dodosrv.DoDoSRV;
import com.ghostchu.plugins.dodosrv.command.bukkit.CommandHandler;
import com.ghostchu.plugins.dodosrv.util.RandomCode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.deechael.dodo.event.EventHandler;
import net.deechael.dodo.event.personal.PersonalMessageEvent;
import net.deechael.dodo.types.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SubCommand_Link implements CommandHandler<Player>, Listener, net.deechael.dodo.event.Listener {
    private final Cache<String, UUID> CODE_POOL = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();
    private final DoDoSRV plugin;

    public SubCommand_Link(DoDoSRV plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.bot().addEventListener(this);
    }

    @Override
    public void onCommand(Player sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        UUID player = sender.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Optional<String> dodo = plugin.userBind().queryBind(player).join();
            if (dodo.isPresent()) {
                plugin.text().of(sender, "this-minecraft-account-already-bind").send();
                return;
            }
            String code = RandomCode.generateCode(6).toLowerCase();
            CODE_POOL.put(code, player);
            plugin.text().of(sender, "enter-code-to-bind-dodo", 30, code).send();
        });
    }

    @EventHandler
    public void dodoDirectMessageEvent(PersonalMessageEvent event) {
        if (event.getMessageType() != MessageType.TEXT) return;
        String code = event.getBody().get().getAsJsonObject().get("content").getAsString();
        UUID player = CODE_POOL.getIfPresent(code.trim().toLowerCase());
        if (player != null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
            plugin.userBind().bind(player, event.getMember().getId())
                    .thenAccept(errorMessage -> {
                        if (errorMessage != null) {
                            event.getMember().send(plugin.text().of("bind-failure", errorMessage).dodoText());
                            return;
                        }
                        event.getMember().send(plugin.text().of("bind-success", player, offlinePlayer.getName()).dodoText());
                    })
                    .exceptionally(err -> {
                        event.getMember().send(plugin.text().of("internal-error").dodoText());
                        return null;
                    });
        }

    }
}
