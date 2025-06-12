package com.nacos;

import org.springframework.beans.factory.ObjectProvider;

import java.util.Collections;
import java.util.Iterator;

/**
 * 用于封装一个固定对象为 ObjectProvider 的通用实现（兼容 Java 8）
 */
public class StaticObjectProvider<T> implements ObjectProvider<T> {

    private final T object;

    public StaticObjectProvider(T object) {
        this.object = object;
    }

    @Override
    public T getObject(Object... args) {
        return object;
    }

    @Override
    public T getIfAvailable() {
        return object;
    }

    @Override
    public T getIfUnique() {
        return object;
    }

    @Override
    public T getObject() {
        return object;
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.singletonList(object).iterator();
    }
}
