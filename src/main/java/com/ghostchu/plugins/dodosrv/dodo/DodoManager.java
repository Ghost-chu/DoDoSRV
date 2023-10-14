package com.ghostchu.plugins.dodosrv.dodo;

import com.ghostchu.plugins.dodosrv.DoDoSRV;
import com.ghostchu.plugins.dodosrv.util.AbstractCacheable;
import net.deechael.dodo.api.Member;
import net.deechael.dodo.api.Role;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Iterator;

public class DodoManager extends AbstractCacheable {
    private final DoDoSRV plugin;

    public DodoManager(DoDoSRV plugin) {
        this.plugin = plugin;
    }

    public Role getMemberPrimaryRole(Member member) {
        Iterator<Role> it = member.getRoles().iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public Component getMemberDisplayComponent(String islandId, Member member) {
        Role role = getMemberPrimaryRole(member);
        TextColor color = null;
        if (role != null) {
            color = TextColor.fromHexString(role.getColor());
        }
        Component component = Component.text(member.getNickname());
        component = component.color(color);
        component = component.hoverEvent(getMemberHoverComponent(islandId, member));
        return component;
    }

    public Component getMemberHoverComponent(String islandId, Member member) {
        Component username = Component.text(member.getNickname());
        Role primaryRole = getMemberPrimaryRole(member);
        if (primaryRole != null) {
            username = username.color(TextColor.fromHexString(primaryRole.getColor()));
        }
        Component level = Component.text(member.getLevel());
        Component onlineStatus = getMemberOnlineStatusComponent(member);
        Component ranks = getMemberRanksComponent(islandId, member);
        return plugin.text().of("sender-name-hover", username, level, onlineStatus, ranks,member.getIntegral()).component();
    }

    public Component getMemberRanksComponent(String islandId, Member member) {
        Component[] components = member.getRoles().stream()
                .filter(role -> role.getIslandId().equals(islandId))
                .map(role -> Component.text(role.getName()).color(TextColor.fromHexString(role.getColor()))).toArray(Component[]::new);
        return Component.join(JoinConfiguration.separator(Component.text(" ")), components);
    }

    public Component getMemberOnlineStatusComponent(Member member) {
        return switch (member.getOnlineStatus()) {
            case ONLINE -> Component.text("在线").color(NamedTextColor.GREEN);
            case WORKING -> Component.text("请勿打扰").color(NamedTextColor.RED);
            case LEAVE -> Component.text("离开").color(NamedTextColor.GRAY);
            case OFFLINE -> Component.text("离线").color(NamedTextColor.DARK_GRAY);
            //noinspection UnnecessaryDefault
            default -> Component.text("未知").color(NamedTextColor.WHITE);
        };
    }
}
