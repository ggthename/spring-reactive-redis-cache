package org.my.reactor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

@Configuration
@ComponentScan(basePackages = {"org.my.reactor"})
public class ReactiveRedisCacheAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ReactiveRedisCacheAspect reactiveRedisCacheAspect(ReactiveRedisCacheManager reactiveRedisCacheManager,
                                                             ReactiveRedisTemplate reactiveRedisTemplate){
        return new ReactiveRedisCacheAspect(reactiveRedisCacheManager, reactiveRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReactiveRedisCacheManager reactiveRedisCacheManager(ReactiveRedisProperties reactiveRedisProperties){
        return new ReactiveRedisCacheManager(reactiveRedisProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReactiveRedisProperties reactiveRedisProperties(){
        return new ReactiveRedisProperties();
    }
}
