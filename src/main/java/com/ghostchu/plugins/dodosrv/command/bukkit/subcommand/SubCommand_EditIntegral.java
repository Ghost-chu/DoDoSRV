package com.ghostchu.plugins.dodosrv.command.bukkit.subcommand;

import com.ghostchu.plugins.dodosrv.DoDoSRV;
import com.ghostchu.plugins.dodosrv.command.bukkit.CommandHandler;
import com.ghostchu.plugins.dodosrv.util.Util;
import net.deechael.dodo.api.Member;
import net.deechael.dodo.types.IntegralOperateType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class SubCommand_EditIntegral implements CommandHandler<CommandSender>, Listener, net.deechael.dodo.event.Listener {
    private final DoDoSRV plugin;

    public SubCommand_EditIntegral(DoDoSRV plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.bot().addEventListener(this);
    }

    @Override
    public void onCommand(CommandSender sender, @NotNull String commandLabel, @NotNull String[] cmdArg) {
        Util.asyncThreadRun(() -> {
            @SuppressWarnings("deprecation") OfflinePlayer player = Bukkit.getOfflinePlayer(cmdArg[0]);
            int opType = Integer.parseInt(cmdArg[1]);
            long amount = Long.parseLong(cmdArg[2]);
            String dodoId = plugin.database().getDatabaseHelper().getBindFromPlayer(player.getUniqueId()).join();
            if (dodoId == null) {
                plugin.text().of(player.getPlayer(), "dodo-not-bind").send();
                plugin.text().of(sender, "dodo-not-bind").send();
                return;
            }
            Member member = null;
            try {
                member = plugin.bot().getClient().fetchMember(plugin.getIslandId(), dodoId);
            } catch (Throwable err) {
                err.printStackTrace();
            }
            if (member == null) {
                plugin.text().of(player.getPlayer(), "op-failure-no-dodo-found").send();
                plugin.text().of(sender, "op-failure-no-dodo-found").send();
                return;
            }
            member.editIntegral(IntegralOperateType.of(opType), amount);
            plugin.text().of(sender, "integral-op-success").send();
        });
    }

}
