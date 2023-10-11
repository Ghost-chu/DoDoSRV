package com.ghostchu.plugins.dodosrv.dodo;

import com.ghostchu.plugins.dodosrv.DoDoSRV;
import com.ghostchu.plugins.dodosrv.database.DatabaseManager;
import com.ghostchu.plugins.dodosrv.database.SimpleDatabaseHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UserBindManager {
    private final DatabaseManager databaseManager;
    private final SimpleDatabaseHelper databaseHelper;
    private final DoDoSRV plugin;

    private final Cache<UUID, Optional<String>> player2DoDoCache = CacheBuilder
            .newBuilder()
            .maximumSize(1500)
            .expireAfterWrite(Duration.of(5, ChronoUnit.MINUTES))
            .build();
    private final Cache<String, Optional<UUID>> doDo2PlayerCache = CacheBuilder
            .newBuilder()
            .maximumSize(1500)
            .expireAfterWrite(Duration.of(5, ChronoUnit.MINUTES))
            .build();

    public UserBindManager(DoDoSRV plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.databaseHelper = databaseManager.getDatabaseHelper();
    }

    public CompletableFuture<Optional<UUID>> queryBind(String dodoSourceId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return doDo2PlayerCache.get(dodoSourceId, () -> Optional.ofNullable(databaseHelper.getBindFromdodo(dodoSourceId).join()));
            } catch (ExecutionException e) {
                throw new IllegalStateException("无法从数据库加载数据到缓存", e);
            }
        });
    }

    public CompletableFuture<Optional<String>> queryBind(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return player2DoDoCache.get(player, () -> Optional.ofNullable(databaseHelper.getBindFromPlayer(player).join()));
            } catch (ExecutionException e) {
                throw new IllegalStateException("无法从数据库加载数据到缓存", e);
            }
        });
    }

    public CompletableFuture<@Nullable String> bind(UUID player, String dodoSourceId) {
        return CompletableFuture.supplyAsync(() -> {
            if (databaseHelper.getBindFromPlayer(player).join() != null) {
                return "错误：此玩家已经绑定了一个 DoDo 账号，请先解绑。";
            }
            if (databaseHelper.getBindFromdodo(dodoSourceId).join() != null) {
                return "错误：此 DoDo 账号已经绑定了一个玩家，请先解绑。";
            }
            databaseHelper.bind(player, dodoSourceId);
            player2DoDoCache.put(player, Optional.of(dodoSourceId));
            doDo2PlayerCache.put(dodoSourceId, Optional.of(player));
            return null;
        });
    }

    public CompletableFuture<@Nullable String> unbind(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            String dodo = databaseHelper.getBindFromPlayer(player).join();
            if (dodo == null) {
                return "错误：此玩家还没有绑定任何 DoDo 账号";
            }
            databaseHelper.unbind(player);
            player2DoDoCache.invalidate(player);
            doDo2PlayerCache.invalidate(dodo);
            return null;
        });
    }

    public CompletableFuture<@Nullable String> unbind(String dodo) {
        return CompletableFuture.supplyAsync(() -> {
            UUID player = databaseHelper.getBindFromdodo(dodo).join();
            if (player == null) {
                return "错误：此 DoDo 账号还没有绑定任何玩家。";
            }
            databaseHelper.unbind(dodo);
            doDo2PlayerCache.invalidate(dodo);
            player2DoDoCache.invalidate(player);
            return null;
        });
    }

}
