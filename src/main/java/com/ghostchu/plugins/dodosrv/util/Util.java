package com.ghostchu.plugins.dodosrv.util;

import com.ghostchu.plugins.dodosrv.DoDoSRV;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Util {
    /**
     * Replace args in raw to args
     *
     * @param raw  text
     * @param args args
     * @return filled text
     */
    @NotNull
    public static String fillArgs(@Nullable String raw, @Nullable String... args) {
        if (StringUtils.isEmpty(raw)) {
            return "";
        }
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                raw = StringUtils.replace(raw, "{" + i + "}", args[i] == null ? "" : args[i]);
            }
        }
        return raw;
    }

    /**
     * Replace args in origin to args
     *
     * @param origin origin
     * @param args   args
     * @return filled component
     */
    @NotNull
    public static Component fillArgs(@NotNull Component origin, @Nullable Component... args) {
        for (int i = 0; i < args.length; i++) {
            origin = origin.replaceText(TextReplacementConfig.builder()
                    .matchLiteral("{" + i + "}")
                    .replacement(args[i] == null ? Component.empty() : args[i])
                    .build());
        }
        return origin.compact();
    }
    /**
     * Execute the Runnable in async thread.
     * If it already on main-thread, will be move to async thread.
     *
     * @param runnable The runnable
     */
    public static void asyncThreadRun(@NotNull Runnable runnable) {
        if (!DoDoSRV.getPlugin(DoDoSRV.class).isEnabled()) {
            runnable.run();
            return;
        }
        if (!Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(DoDoSRV.getPlugin(DoDoSRV.class), runnable);
        }
    }
    /**
     * Execute the Runnable in server main thread.
     * If it already on main-thread, will be executed directly.
     * or post to main-thread if came from any other thread.
     *
     * @param runnable The runnable
     */
    public static void mainThreadRun(@NotNull Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(DoDoSRV.getPlugin(DoDoSRV.class), runnable);
        }
    }


    /**
     * Converts a name like IRON_INGOT into Iron Ingot to improve readability
     *
     * @param ugly The string such as IRON_INGOT
     * @return A nicer version, such as Iron Ingot
     */
    @NotNull
    public static String prettifyText(@NotNull String ugly) {
        String[] nameParts = ugly.split("_");
        if (nameParts.length == 1) {
            return firstUppercase(ugly);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nameParts.length; i++) {
            if (!nameParts[i].isEmpty()) {
                sb.append(Character.toUpperCase(nameParts[i].charAt(0))).append(nameParts[i].substring(1).toLowerCase());
            }
            if (i + 1 != nameParts.length) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }
    /**
     * First uppercase for every words the first char for a text.
     *
     * @param string text
     * @return Processed text.
     */
    @NotNull
    public static String firstUppercase(@NotNull String string) {
        if (string.length() > 1) {
            return Character.toUpperCase(string.charAt(0)) + string.substring(1).toLowerCase();
        } else {
            return string.toUpperCase();
        }
    }
}
