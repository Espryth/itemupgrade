package com.wasmake.itemupgrade.util;

import org.spongepowered.configurate.serialize.SerializationException;

@FunctionalInterface
public interface ThrowingConsumer<T> {

    void accept(T t) throws SerializationException;

}