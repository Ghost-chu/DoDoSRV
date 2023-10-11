package com.ghostchu.plugins.dodosrv.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractCacheable {
    private final Cache<String, Object> CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(3000)
            .build();
    @Nullable
    public <T> T getCache(@NotNull String key, @NotNull Class<T> clazz, @Nullable Callable<T> loading) {
        try {
            Object dat;
            if (loading != null) {
                dat = CACHE.get(key, loading);
            } else {
                dat = CACHE.getIfPresent(key);
            }
            if (dat == null) return null;
            if (clazz.equals(dat.getClass()) || clazz.isAssignableFrom(dat.getClass())) {
                return clazz.cast(dat);
            }
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public <T> T getCacheCapture(@NotNull String key, @NotNull T clazz, @Nullable Callable<T> loading) {
        try {
            Object dat;
            if (loading != null) {
                dat = CACHE.get(key, loading);
            } else {
                dat = CACHE.getIfPresent(key);
            }
            if (dat == null) return null;
            if (clazz.equals(dat.getClass()) || clazz.getClass().isAssignableFrom(dat.getClass())) {
                //noinspection unchecked
                return (T) clazz.getClass().cast(dat);
            }
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
