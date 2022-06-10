package org.my.reactor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Component
public class ReactiveRedisCacheManager {

    private ReactiveRedisProperties reactiveRedisProperties;

    public <T> Mono<T> findCacheMono(String cacheName, Object cacheKey, Supplier<Mono<T>> retriever, ReactiveRedisTemplate<String, T> reactiveRedisTemplate){

        String prefix = cacheName;
        String cacheNameKey = prefix + "::" + cacheKey.toString();

        return CacheMono
                .lookup(k-> reactiveRedisTemplate.opsForValue()
                            .get(k)
                            .map(Signal::next), cacheNameKey)
                .onCacheMissResume(retriever)
                .andWriteWith( (key, signal) ->  Mono.fromRunnable( () -> Optional.ofNullable( signal.get())
                                                                                 .ifPresent(result -> reactiveRedisTemplate.opsForValue()
                                                                                                        .set(key, result, Duration.ofSeconds(reactiveRedisProperties.getDefaultExpireSeconds()))
                                                                                                        .subscribe()
                                                                                )
                        )
                );
    }
}
