package org.moon.figura.utils.caching;

import org.apache.commons.lang3.NotImplementedException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public class CacheUtils {

    public static final int DEFAULT_MAX_SIZE = 500;

    public static <T extends CachedType<T>> Indifferent<T> getIndifferent(Supplier<T> generator, int count) {
        return new Indifferent<>(generator, count);
    }

    public static <T extends CachedType<T>> Cache<T> getCache(Supplier<T> generator, int maxSize) {
        return new Cache<>(generator, maxSize);
    }

    public static <T extends CachedType<T>> Cache<T> getCache(Supplier<T> generator) {
        return new Cache<>(generator, DEFAULT_MAX_SIZE);
    }

    public static class Cache<T extends CachedType<T>> {
        protected final Queue<T> cache;
        protected final Supplier<T> generator;
        protected final int maxSize;

        protected Cache(Supplier<T> generator) {
            this(generator, Integer.MAX_VALUE);
        }

        protected Cache(Supplier<T> generator, int maxSize) {
            cache = new LinkedList<>();
            this.generator = generator;
            this.maxSize = maxSize;
        }

        public T getFresh() {
            T result = cache.poll();
            if (result == null)
                result = generator.get();
            result.reset();
            return result;
        }

        public void offerOld(T old) {
            if (cache.size() >= maxSize)
                return;
            cache.offer(old);
        }
    }

    public static class Indifferent<T extends CachedType<T>> extends Cache<T> {
        private Indifferent(Supplier<T> generator) {
            this(generator, DEFAULT_MAX_SIZE);
        }

        private Indifferent(Supplier<T> generator, int maxSize) {
            super(generator, maxSize);
            for (int i = 0; i < maxSize; i++) {
                cache.offer(generator.get());
            }
        }

        @Override
        public T getFresh() {
            T result = cache.poll();
            cache.offer(result);
            assert result != null;
            return result.reset();
        }

        @Override
        public void offerOld(T old) {
            throw new NotImplementedException("Cannot offer old value to an Indifferent cache");
        }
    }
}
