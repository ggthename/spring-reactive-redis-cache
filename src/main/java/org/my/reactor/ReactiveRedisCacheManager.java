package org.my.reactor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class ReactiveRedisCacheManager {

    private ReactiveRedisProperties reactiveRedisProperties;

    @Autowired
    public ReactiveRedisCacheManager(ReactiveRedisProperties reactiveRedisProperties){
        this.reactiveRedisProperties=reactiveRedisProperties;
    }

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

    public <T> Mono<Void> deleteCache(String cacheName, Object cacheKey, ReactiveRedisTemplate<String, T> reactiveRedisTemplate){

        String prefix = cacheName;
        String cacheNameKey = prefix + "::" + cacheKey.toString();

        CacheMono
                .lookup(k-> reactiveRedisTemplate.opsForValue()
                        .delete(k)
                        .map(Signal::next), cacheNameKey);

        return Mono.empty().then();
    }

}
