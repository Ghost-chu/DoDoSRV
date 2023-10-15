package com.ghostchu.plugins.dodosrv.text;

import com.ghostchu.plugins.dodosrv.DoDoSRV;
import com.ghostchu.plugins.dodosrv.util.JsonUtil;
import com.ghostchu.plugins.dodosrv.util.Util;
import de.themoep.minedown.adventure.MineDown;
import net.deechael.dodo.api.Member;
import net.deechael.dodo.content.Message;
import net.deechael.dodo.content.TextMessage;
import net.deechael.dodo.types.MessageType;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TextManager {
    private final File file;
    private final DoDoSRV plugin;
    private YamlConfiguration config;
    private MiniMessage miniMessage;

    public TextManager(DoDoSRV plugin, File file) {
        this.plugin = plugin;
        this.file = file;
        init();
    }

    private void init() {
        this.config = YamlConfiguration.loadConfiguration(file);
        this.miniMessage = MiniMessage.miniMessage();

    }

    public CompletableFuture<Component> dodoToComponent(String content) {
        return CompletableFuture.supplyAsync(() -> {
            String replaced = content;
            Map<String, Component> replacements = new LinkedHashMap<>();
            String[] found = StringUtils.substringsBetween(content, "<@!", ">");
            if (found != null) {
                for (int i = 0; i < found.length; i++) {
                    try {
                        String cursor = found[i];
                        String replaceTo = "{" + i + "}";
                        String origin = "<@!" + cursor + ">";
                        replaced = replaced.replace(origin, replaceTo);
                        Member member = plugin.bot().getClient().fetchMember(plugin.getIslandId(), cursor);
                        replacements.put(replaceTo, plugin.dodoManager().getMemberDisplayComponent(plugin.getIslandId(), member));
                    } catch (Throwable ignored) {
                    }
                }
            }
            Component component = new MineDown(replaced).toComponent();
            for (Map.Entry<String, Component> e : replacements.entrySet()) {
                component = component.replaceText(TextReplacementConfig.builder()
                        .matchLiteral(e.getKey())
                        .replacement(e.getValue())
                        .build());
            }
            return component;
        });

    }


    public Text of(CommandSender sender, String key, Object... args) {
        return new Text(plugin.audience(), sender, Util.fillArgs(miniMessage.deserialize(config.getString(key, "Missing no: " + key)), convert(args)));
    }

    public Text of(String key, Object... args) {
        return new Text(plugin.audience(), null, Util.fillArgs(miniMessage.deserialize(config.getString(key, "Missing no: " + key)), convert(args)));
    }

    @NotNull
    public Component[] convert(@Nullable Object... args) {
        if (args == null || args.length == 0) {
            return new Component[0];
        }
        Component[] components = new Component[args.length];
        for (int i = 0; i < args.length; i++) {
            Object obj = args[i];
            if (obj == null) {
                components[i] = Component.text("null");
                continue;
            }
            Class<?> clazz = obj.getClass();
            if (obj instanceof Component) {
                Component component = (Component) obj;
                components[i] = component;
                continue;
            }
            if (obj instanceof ComponentLike) {
                ComponentLike componentLike = (ComponentLike) obj;
                components[i] = componentLike.asComponent();
                continue;
            }
            // Check
            try {
                if (Character.class.equals(clazz)) {
                    components[i] = Component.text((char) obj);
                    continue;
                }
                if (Byte.class.equals(clazz)) {
                    components[i] = Component.text((Byte) obj);
                    continue;
                }
                if (Integer.class.equals(clazz)) {
                    components[i] = Component.text((Integer) obj);
                    continue;
                }
                if (Long.class.equals(clazz)) {
                    components[i] = Component.text((Long) obj);
                    continue;
                }
                if (Float.class.equals(clazz)) {
                    components[i] = Component.text((Float) obj);
                    continue;
                }
                if (Double.class.equals(clazz)) {
                    components[i] = Component.text((Double) obj);
                    continue;
                }
                if (Boolean.class.equals(clazz)) {
                    components[i] = Component.text((Boolean) obj);
                    continue;
                }
                if (String.class.equals(clazz)) {
                    components[i] = LegacyComponentSerializer.legacySection().deserialize((String) obj);
                    continue;
                }
                if (Text.class.equals(clazz)) {
                    components[i] = ((Text) obj).component();
                }
                components[i] = LegacyComponentSerializer.legacySection().deserialize(obj.toString());
            } catch (Exception exception) {
                components[i] = LegacyComponentSerializer.legacySection().deserialize(obj.toString());
            }
            // undefined

        }
        return components;
    }

    public static class Text {
        private final Component component;
        private final CommandSender sender;
        private final BukkitAudiences audiences;

        public Text(BukkitAudiences audiences, CommandSender sender, Component component) {
            this.audiences = audiences;
            this.sender = sender;
            this.component = component.compact();
        }

        public Message dodoText() {
            String raw = PlainTextComponentSerializer.plainText().serialize(component);
            if (StringUtils.isBlank(raw)) {
                raw = "Missing no: text is null";
            }
            Message message = new TextMessage(raw);
            if (false && JsonUtil.isJson(raw)) {
                message = Message.parse(MessageType.CARD, raw);
            }

            return message;
        }

        public Component component() {
            return this.component;
        }

        public String plain() {
            return PlainTextComponentSerializer.plainText().serialize(component);
        }

        public CommandSender sender() {
            return this.sender;
        }

        public void send() {
            if (this.sender != null) {
                audiences.sender(this.sender).sendMessage(component);
            }
        }

    }
}
