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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonObject;
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
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class DoDoSRV extends JavaPlugin {

    private DodoBot bot;
    private DatabaseManager databaseManager;
    private UserBindManager userBindManager;
    private TextManager textManager;
    private SimpleCommandManager commandManager;
    private DoDoListener doDoListener;
    private DodoManager dodoManager;
    private static Cache<String, String> MESSAGE_ID_TO_ECHO = CacheBuilder.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(15000)
            .build();


    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        saveDefTranslations();
        this.textManager = new TextManager(this, new File(getDataFolder(), "messages.yml"));
        this.databaseManager = initDatabase();
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
        String backupClientId = System.getProperty("dodosrv.client-id");
        if(backupClientId == null) backupClientId = "0";
        int clientId = getConfig().getInt("client-id", Integer.parseInt(backupClientId) );
        this.bot = new DodoBot(clientId, getConfig().getString("bot-token", System.getProperty("dodosrv.bot-token")));
        initListeners();
        this.bot.runAfter(this::postInit);
        this.bot.start();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public CompletableFuture<String> sendMessageToDefChannel(Message message) {
        return CompletableFuture.supplyAsync(() -> {
            Channel channel = bot().getClient().fetchChannel(getIslandId(), getChatChannel());
            if (!(channel instanceof TextChannel)) {
                return null;
            }
            TextChannel textChannel = (TextChannel) channel;
            String msgId = textChannel.send(message);
            if(message instanceof TextMessage){
                TextMessage msg = (TextMessage) message;
                MESSAGE_ID_TO_ECHO.put(msgId, msg.getContent());
            }
            return msgId;
        });
    }

    public CompletableFuture<String> sendMessageToDefChannel(String string) {
        if (!JsonUtil.isJson(string)) {
            return sendMessageToDefChannel(new TextMessage(string));
        } else {
            return sendCardMessageToDefChannel(string);
        }
    }

    public CompletableFuture<String> sendCardMessageToDefChannel(String json) {
        return CompletableFuture.supplyAsync(() -> {
            JsonObject finalJson = JsonUtil.readObject(json);
            Channel channel = bot().getClient().fetchChannel(getIslandId(), getChatChannel());
            if (!(channel instanceof TextChannel)) {
                return null;
            }
            TextChannel textChannel = (TextChannel) channel;
            Field gatewayField;
            try {
                gatewayField = ChannelImpl.class.getDeclaredField("gateway");
                gatewayField.setAccessible(true);
                Gateway gateway = (Gateway) gatewayField.get(channel);
                Route route = API.V2.Channel.messageSend().param("channelId", channel.getId()).param("messageType", MessageType.CARD.getCode()).param("messageBody", finalJson);
                return gateway.executeRequest(route).getAsJsonObject().get("messageId").getAsString();
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

    public DodoManager dodoManager() {
        return dodoManager;
    }

    public Cache<String,String> echoCache(){
        return MESSAGE_ID_TO_ECHO;
    }

}
