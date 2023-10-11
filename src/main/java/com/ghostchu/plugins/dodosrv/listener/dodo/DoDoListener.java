package com.ghostchu.plugins.dodosrv.listener.dodo;

import com.ghostchu.plugins.dodosrv.DoDoSRV;
import net.deechael.dodo.api.Member;
import net.deechael.dodo.api.TextChannel;
import net.deechael.dodo.content.*;
import net.deechael.dodo.event.EventHandler;
import net.deechael.dodo.event.Listener;
import net.deechael.dodo.event.channel.ChannelMessageEvent;
import net.deechael.dodo.types.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;

public class DoDoListener implements Listener {
    private final DoDoSRV plugin;
    private boolean dodoToMinecraftEnabled;
    private String requriedPrefix;
    private String formatTemplate;

    public DoDoListener(DoDoSRV plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        dodoToMinecraftEnabled = plugin.getConfig().getBoolean("feature.dodo-to-minecraft.enable");
        requriedPrefix = plugin.getConfig().getString("feature.dodo-to-minecraft.require-prefix");
        if (StringUtils.isBlank(requriedPrefix)) requriedPrefix = null;
    }


    @EventHandler
    public void onDoDoTextMessage(ChannelMessageEvent event) {
        if (!dodoToMinecraftEnabled){
            return;
        }
        if (event.getMessageType() != MessageType.TEXT) {
            return;
        }
        String content = event.getBody().get().getAsJsonObject().get("content").getAsString();
        if (requriedPrefix != null && !content.startsWith(requriedPrefix)) {
            return;
        }
        if(!event.getChannelId().equals(plugin.getChatChannel())) {
            return;
        }
        Member sender = event.getMember();
        TextChannel channel = (TextChannel) plugin.bot().getClient().fetchChannel(event.getIslandId(), event.getChannelId());
        String channelName = channel.getName();
        Component channelNameComponent = Component.text(channelName);
        Component senderComponent = plugin.dodoManager().getMemberDisplayComponent(event.getIslandId(), sender);
        Bukkit.spigot().broadcast(BungeeComponentSerializer.get().serialize(plugin.text().of("dodo-to-minecraft-format",
                channelNameComponent,
                senderComponent,
                content
        ).component()));
    }
    @EventHandler
    public void onDoDoFileMessage(ChannelMessageEvent event) {
        if (!dodoToMinecraftEnabled){
            return;
        }
        if (event.getMessageType() != MessageType.FILE) {
            return;
        }
        FileMessage message = (FileMessage) event.getBody();
        String fileSize = FileUtils.byteCountToDisplaySize(message.getSize());
        Component component = Component.text("[文件："+message.getName()+": "+ fileSize+"]");
        component = component.clickEvent(ClickEvent.openUrl(message.getUrl()));
        component = component.hoverEvent(HoverEvent.showText(Component.text("点击在浏览器中打开")));
        Member sender = event.getMember();
        TextChannel channel = (TextChannel) plugin.bot().getClient().fetchChannel(event.getIslandId(), event.getChannelId());
        String channelName = channel.getName();
        Component channelNameComponent = Component.text(channelName);
        Component senderComponent = plugin.dodoManager().getMemberDisplayComponent(event.getIslandId(), sender);
        Bukkit.spigot().broadcast(BungeeComponentSerializer.get().serialize(plugin.text().of("dodo-to-minecraft-format",
                channelNameComponent,
                senderComponent,
                component
        ).component()));
    }
    @EventHandler
    public void onDoDoImgMessage(ChannelMessageEvent event) {
        if (!dodoToMinecraftEnabled){
            return;
        }
        if (event.getMessageType() != MessageType.IMAGE) {
            return;
        }
        ImageMessage message = (ImageMessage) event.getBody();
        Component component = Component.text("[图片: "+message.getWidth()+"px*"+message.getHeight()+"px]");
        component = component.clickEvent(ClickEvent.openUrl(message.getUrl()));
        component = component.hoverEvent(HoverEvent.showText(Component.text("点击在浏览器中打开")));
        Member sender = event.getMember();
        TextChannel channel = (TextChannel) plugin.bot().getClient().fetchChannel(event.getIslandId(), event.getChannelId());
        String channelName = channel.getName();
        Component channelNameComponent = Component.text(channelName);
        Component senderComponent = plugin.dodoManager().getMemberDisplayComponent(event.getIslandId(), sender);
        Bukkit.spigot().broadcast(BungeeComponentSerializer.get().serialize(plugin.text().of("dodo-to-minecraft-format",
                channelNameComponent,
                senderComponent,
                component
        ).component()));
    }
    @EventHandler
    public void onDoDoShareMessage(ChannelMessageEvent event) {
        if (!dodoToMinecraftEnabled){
            return;
        }
        if (event.getMessageType() != MessageType.SHARE) {
            return;
        }
        ShareMessage message = (ShareMessage) event.getBody();
        Component component = Component.text("[分享]");
        component = component.clickEvent(ClickEvent.openUrl(message.getJumpUrl()));
        component = component.hoverEvent(HoverEvent.showText(Component.text("点击在浏览器中打开")));
        Member sender = event.getMember();
        TextChannel channel = (TextChannel) plugin.bot().getClient().fetchChannel(event.getIslandId(), event.getChannelId());
        String channelName = channel.getName();
        Component channelNameComponent = Component.text(channelName);
        Component senderComponent = plugin.dodoManager().getMemberDisplayComponent(event.getIslandId(), sender);
        Bukkit.spigot().broadcast(BungeeComponentSerializer.get().serialize(plugin.text().of("dodo-to-minecraft-format",
                channelNameComponent,
                senderComponent,
                component
        ).component()));
    }
    @EventHandler
    public void onDoDoVideoMessage(ChannelMessageEvent event) {
        if (!dodoToMinecraftEnabled){
            return;
        }
        if (event.getMessageType() != MessageType.VIDEO) {
            return;
        }
        VideoMessage message = (VideoMessage) event.getBody();
        Component component = Component.text("[视频]");
        component = component.clickEvent(ClickEvent.openUrl(message.getUrl()));
        component = component.hoverEvent(HoverEvent.showText(Component.text("点击在浏览器中打开")));
        Member sender = event.getMember();
        TextChannel channel = (TextChannel) plugin.bot().getClient().fetchChannel(event.getIslandId(), event.getChannelId());
        String channelName = channel.getName();
        Component channelNameComponent = Component.text(channelName);
        Component senderComponent = plugin.dodoManager().getMemberDisplayComponent(event.getIslandId(), sender);
        Bukkit.spigot().broadcast(BungeeComponentSerializer.get().serialize(plugin.text().of("dodo-to-minecraft-format",
                channelNameComponent,
                senderComponent,
                component
        ).component()));
    }
    @EventHandler
    public void onDoDoCardMessage(ChannelMessageEvent event) {
        if (!dodoToMinecraftEnabled){
            return;
        }
        if (event.getMessageType() != MessageType.CARD) {
            return;
        }
        Component component = Component.text("[卡片消息]");
        Member sender = event.getMember();
        TextChannel channel = (TextChannel) plugin.bot().getClient().fetchChannel(event.getIslandId(), event.getChannelId());
        String channelName = channel.getName();
        Component channelNameComponent = Component.text(channelName);
        Component senderComponent = plugin.dodoManager().getMemberDisplayComponent(event.getIslandId(), sender);
        Bukkit.spigot().broadcast(BungeeComponentSerializer.get().serialize(plugin.text().of("dodo-to-minecraft-format",
                channelNameComponent,
                senderComponent,
                component
        ).component()));
    }
}
