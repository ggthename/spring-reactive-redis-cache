package org.my.reactor.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReactiveRedisCacheEvict {
    String name();
    String key();
}
