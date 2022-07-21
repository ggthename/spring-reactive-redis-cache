package org.my.reactor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="spring.cache.redis")
@Getter
@Setter
public class ReactiveRedisProperties {
    private long defaultExpireSeconds = -1;
}
