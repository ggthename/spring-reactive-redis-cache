package org.my.reactor;

import com.sktelecom.di.reactor.annotation.ReactiveRedisCacheable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Aspect
@Component
public class ReactiveRedisCacheAspect {

    private final ReactiveRedisCacheManager reactiveRedisCacheManager;

    private final ReactiveRedisTemplate reactiveRedisTemplate;

    @Autowired
    public ReactiveRedisCacheAspect(ReactiveRedisCacheManager reactiveRedisCacheManager,
                                    ReactiveRedisTemplate reactiveRedisTemplate){
        this.reactiveRedisCacheManager = reactiveRedisCacheManager;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @Pointcut("@annotation(com.sktelecom.di.reactor.annotation.ReactiveRedisCacheable)")
    public void pointCut() {
    }

    @Around(value = "pointCut()")
    public Mono getReactiveRedisCache(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();

        ReactiveRedisCacheable reactorCacheable = method.getAnnotation(ReactiveRedisCacheable.class);
        String cacheName = reactorCacheable.name();
        Object[] args = proceedingJoinPoint.getArgs();

        return reactiveRedisCacheManager.findCacheMono(cacheName, generateKey(args), (Supplier) () -> {
            try {
                return proceedingJoinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }, this.reactiveRedisTemplate);

    }

    private String generateKey(Object... objects) {
        return Arrays.stream(objects)
                .map(obj -> obj == null ? "" : obj.toString())
                .collect(Collectors.joining(":"));
    }
}
