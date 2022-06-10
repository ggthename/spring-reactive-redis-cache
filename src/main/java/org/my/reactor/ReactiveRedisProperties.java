package org.my.reactor;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="spring.cache.redis")
@Getter
public class ReactiveRedisProperties {
    private long defaultExpireSeconds = -1;
}
