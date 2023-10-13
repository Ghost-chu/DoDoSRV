package com.ghostchu.plugins.dodosrv;

import com.ghostchu.plugins.dodosrv.command.bukkit.CommandManager;
import com.ghostchu.plugins.dodosrv.command.bukkit.SimpleCommandManager;
import com.ghostchu.plugins.dodosrv.database.DatabaseManager;
import com.ghostchu.plugins.dodosrv.dodo.DodoManager;
import com.ghostchu.plugins.dodosrv.dodo.UserBindManager;
import com.ghostchu.plugins.dodosrv.listener.bukkit.BukkitListener;
import com.ghostchu.plugins.dodosrv.listener.dodo.DoDoListener;
import com.ghostchu.plugins.dodosrv.text.TextManager;
import com.ghostchu.plugins.dodosrv.util.JsonUtil;
import com.ghostchu.plugins.dodosrv.util.Util;
import com.google.gson.JsonParser;
import net.deechael.dodo.API;
import net.deechael.dodo.api.Channel;
import net.deechael.dodo.api.TextChannel;
import net.deechael.dodo.content.Message;
import net.deechael.dodo.content.TextMessage;
import net.deechael.dodo.gate.Gateway;
import net.deechael.dodo.impl.ChannelImpl;
import net.deechael.dodo.impl.DodoBot;
import net.deechael.dodo.network.Route;
import net.deechael.dodo.types.MessageType;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;

public final class DoDoSRV extends JavaPlugin {
    private DodoBot bot;
    private DatabaseManager databaseManager;
    private UserBindManager userBindManager;
    private TextManager textManager;
    private SimpleCommandManager commandManager;
    private DoDoListener doDoListener;
    private DodoManager dodoManager;
    private BukkitAudiences audience;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.audience = BukkitAudiences.create(this);
        saveDefaultConfig();
        saveDefTranslations();
        this.textManager = new TextManager(this, new File(getDataFolder(), "messages.yml"));
        try{
            this.databaseManager = initDatabase();
        }catch (Throwable ignored){
            ignored.printStackTrace();
        }
        this.userBindManager = new UserBindManager(this, databaseManager);
        try {
            initDoDoBot();
        } catch (Exception e) {
            Bukkit.getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }
        this.dodoManager = new DodoManager(this);
        this.commandManager = new SimpleCommandManager(this);
        getCommand("dodosrv").setExecutor(this.commandManager);
        getCommand("dodosrv").setTabCompleter(this.commandManager);
    }

    private void postInit() {
        //initListeners();
    }

    private void initListeners() {
        Bukkit.getPluginManager().registerEvents(new BukkitListener(this), this);
        this.doDoListener = new DoDoListener(this);
        bot.addEventListener(doDoListener);
    }

    private DatabaseManager initDatabase() {
        return new DatabaseManager(this);
    }

    private void initDoDoBot() {
        this.bot = new DodoBot(getConfig().getInt("client-id"), getConfig().getString("bot-token"));
        initListeners();
        this.bot.runAfter(this::postInit);
        this.bot.start();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.audience.close();
    }

    public void sendMessageToDefChannel(Message message) {
        Util.asyncThreadRun(() -> {
            Channel channel = bot().getClient().fetchChannel(getIslandId(), getChatChannel());
            if (!(channel instanceof TextChannel)) {
                return;
            }
            ((TextChannel)channel).send(message);
        });
    }

    public void sendMessageToDefChannel(String string) {
        sendMessageToDefChannel(new TextMessage(string));
    }

    public void sendCardMessageToDefChannel(String json) {
        Bukkit.getScheduler().runTaskAsynchronously(this,()->{
            String finalJson = JsonUtil.regular().toJson(new JsonParser().parse(json));
            Channel channel = bot().getClient().fetchChannel(getIslandId(), getChatChannel());
            if (!(channel instanceof TextChannel)) {
                return;
            }
            Field gatewayField;
            try {
                gatewayField = ChannelImpl.class.getDeclaredField("gateway");
                gatewayField.setAccessible(true);
                Gateway gateway = (Gateway) gatewayField.get(channel);
                Route route = API.V2.Channel.messageSend().param("channelId", channel.getId()).param("messageType", MessageType.CARD.getCode()).param("messageBody", finalJson);
                gateway.executeRequest(route).getAsJsonObject().get("messageId").getAsString();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public String getIslandId() {
        return getConfig().getString("dodo.island-id");
    }

    public String getChatChannel() {
        return getConfig().getString("dodo.chat-channel");
    }

    private void saveDefTranslations() {
        File file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) {
            saveResource("messages.yml", false);
        }
    }

    public DodoBot bot() {
        return bot;
    }

    public TextManager text() {
        return textManager;
    }

    public DatabaseManager database() {
        return databaseManager;
    }

    public UserBindManager userBind() {
        return userBindManager;
    }

    public CommandManager commandManager() {
        return commandManager;
    }
    public DodoManager dodoManager() { return dodoManager; }

    public BukkitAudiences audience() {
        return audience;
    }
}
