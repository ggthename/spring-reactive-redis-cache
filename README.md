# spring-reactive-redis-cache

<개요>
- spring-webflux 는 reactive 사상으로 구성되어 있으며 publisher에 해당하는 reactor구현체 Mono< >로 감싸는 형태로 되어있다.
- 따라서 기존의 @Cacheable로는 원하는 형태의 캐싱을 사용할 수 없다.
- Spring에서는 Cache에 대해서 추상화된 형태로 다양한 기능을 제공하는데 각 Cache 구현체마다 특성이 다르며 non-blocking을 지원하지 못하는 경우가 있다.
- Redis의 경우 Reactive Operation, Template등을 지원하고 있다.

<라이브러리 설명>
1. Reactor Addons 
- Reactive 방식으로 Mono / Flux를 캐시할 수 있도록 제공하는 라이브러리

2. Redis Reactive
- Reactive 방식으로 Redis를 사용하게 제공하는 라이브러리

3. Spring AOP
- 사용자 로직내부에 불필요한 공통로직이 들어가지 않도록 Aspect관점을 제공하는 라이브러리

4. JDK 8이상

<구현 설명>
- @Around 를 통해서 cacheName, cacheKey 등을 생성
- CacheMono lookup / onCacheMissResume, andWriteWith 를 활용하여 캐시 기본동작 구현
- ReactiveRedisTemplate를 사용하여 blocking이 발생하지 않도록 함.
 (주의 : Cache저장소에 접근하는 부분의 구현체에서 blocking요소가 발생할 경우 서비스에 영향을 줄 수 있음)
- ReactiveRedisTemplate에서 반환하는 타입을 Mono <T> 로 사용
 (추후 타입 및 Template주입에 대한 개선이 필요함)
