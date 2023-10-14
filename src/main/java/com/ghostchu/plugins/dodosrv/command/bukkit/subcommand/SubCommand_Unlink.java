package com.ghostchu.plugins.dodosrv.command.bukkit.subcommand;

import com.ghostchu.plugins.dodosrv.DoDoSRV;
import com.ghostchu.plugins.dodosrv.command.bukkit.CommandHandler;
import net.deechael.dodo.api.Member;
import net.deechael.dodo.event.EventHandler;
import net.deechael.dodo.event.personal.PersonalMessageEvent;
import net.deechael.dodo.types.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class SubCommand_Unlink implements CommandHandler<Player>, Listener, net.deechael.dodo.event.Listener {
    private final DoDoSRV plugin;

    public SubCommand_Unlink(DoDoSRV plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.bot().addEventListener(this);
    }

    @Override
    public void onCommand(Player sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        UUID player = sender.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Optional<String> dodo = plugin.userBind().queryBind(player).join();
            if (dodo.isEmpty()) {
                plugin.text().of(sender, "this-minecraft-account-had-no-bind").send();
                return;
            }
            plugin.userBind().unbind(player)
                    .thenAccept(error -> {
                        if (error != null) {
                            plugin.text().of(sender, "unbind-failure", error).send();
                            return;
                        }
                        plugin.text().of(sender, "unbind-success").send();
                    })
                    .exceptionally(error -> {
                        plugin.text().of(sender, "internal-error").send();
                        error.printStackTrace();
                        return null;
                    });
        });
    }

    @EventHandler
    public void dodoDirectMessageEvent(PersonalMessageEvent event) {
        if (event.getMessageType() != MessageType.TEXT) return;
        String command = event.getBody().get().getAsJsonObject().get("content").getAsString();
        if (!command.equalsIgnoreCase("unlink")) return;
        Member member;
        try {
            member = plugin.bot().getClient().fetchMember(event.getIslandId(), event.getDodoId());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return;
        }
        final Member fixedMember = member;
        plugin.userBind().unbind(member.getId())
                .thenAccept(errorMessage -> {
                    if (errorMessage != null) {
                        fixedMember.send(plugin.text().of("unbind-failure", errorMessage).dodoText());
                        return;
                    }
                    fixedMember.send(plugin.text().of("unbind-success").dodoText());
                })
                .exceptionally(err -> {
                    fixedMember.send(plugin.text().of("internal-error").dodoText());
                    return null;
                });

    }
}
