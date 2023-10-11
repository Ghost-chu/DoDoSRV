package com.ghostchu.plugins.dodosrv.database;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SimpleDatabaseHelper {
    private final SQLManager sqlManager;
    private final String prefix;
    private final DatabaseDriverType databaseDriverType;

    public SimpleDatabaseHelper(SQLManager sqlManager, String prefix, DatabaseDriverType databaseDriverType) throws SQLException {
        this.sqlManager = sqlManager;
        this.prefix = prefix;
        this.databaseDriverType = databaseDriverType;
        checkTables();
    }

    private void checkTables() throws SQLException {
        DataTables.initializeTables(sqlManager, prefix);
    }

    public CompletableFuture<@Nullable String> getBindFromPlayer(@NotNull UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            try (SQLQuery query = DataTables.PLAYERS.createQuery()
                    .addCondition("uuid", player.toString())
                    .selectColumns("uuid", "dodo")
                    .setLimit(1)
                    .build().execute();
                 ResultSet resultSet = query.getResultSet()) {
                if (resultSet.next()) {
                    return resultSet.getString("dodo");
                }
                return null;
            } catch (SQLException e) {
                throw new IllegalStateException("执行 SQL 查询时出现错误", e);
            }
        });
    }

    public CompletableFuture<@Nullable UUID> getBindFromdodo(@NotNull String dodoSourceId) {
        return CompletableFuture.supplyAsync(() -> {
            try (SQLQuery query = DataTables.PLAYERS.createQuery()
                    .addCondition("dodo", dodoSourceId)
                    .selectColumns("uuid", "dodo")
                    .setLimit(1)
                    .build().execute();
                 ResultSet resultSet = query.getResultSet()) {
                if (resultSet.next()) {
                    return UUID.fromString(resultSet.getString("uuid"));
                }
                return null;
            } catch (SQLException e) {
                throw new IllegalStateException("执行 SQL 查询时出现错误", e);
            }
        });
    }

    public CompletableFuture<@NotNull Integer> bind(UUID player, String dodoSourceId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return DataTables.PLAYERS.createInsert()
                        .setColumnNames("uuid", "dodo")
                        .setParams(player.toString(), dodoSourceId)
                        .returnGeneratedKey()
                        .execute();
            } catch (SQLException e) {
                throw new IllegalStateException("执行 SQL 查询时出现错误", e);
            }
        });
    }

    public CompletableFuture<@NotNull Integer> unbind(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return DataTables.PLAYERS.createDelete()
                        .addCondition("uuid", player.toString())
                        .build().execute();
            } catch (SQLException e) {
                throw new IllegalStateException("执行 SQL 查询时出现错误", e);
            }
        });
    }

    public CompletableFuture<@NotNull Integer> unbind(String dodoSourceId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return DataTables.PLAYERS.createDelete()
                        .addCondition("dodo", dodoSourceId)
                        .build().execute();
            } catch (SQLException e) {
                throw new IllegalStateException("执行 SQL 查询时出现错误", e);
            }
        });
    }


}
