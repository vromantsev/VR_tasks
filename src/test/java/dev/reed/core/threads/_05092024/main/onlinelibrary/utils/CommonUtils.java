package dev.reed.core.threads._05092024.main.onlinelibrary.utils;

import dev.reed.core.threads._05092024.main.onlinelibrary.service.BookService;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;

@UtilityClass
public class CommonUtils {

    public Field getReadWriteLockField(final Class<? extends BookService> serviceClass) {
        return Arrays.stream(serviceClass.getDeclaredFields())
                .filter(f -> ReadWriteLock.class.isAssignableFrom(f.getType()))
                .findAny()
                .orElse(null);
    }

    public String getLockClassName(final Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(f -> ReadWriteLock.class.isAssignableFrom(f.getType()))
                .findAny()
                .map(Field::getType)
                .map(cl -> Arrays.stream(cl.getDeclaredFields())
                        .filter(f -> ReadWriteLock.class.isAssignableFrom(f.getType()))
                        .map(Field::getType)
                        .map(Class::getName)
                        .findAny()
                        .orElseThrow()
                )
                .orElseThrow();
    }

    public Method findMethod(final Field field, final String methodName) {
        Class<?> type = field.getType();
        return Arrays.stream(type.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .findAny()
                .orElseThrow();
    }
}
