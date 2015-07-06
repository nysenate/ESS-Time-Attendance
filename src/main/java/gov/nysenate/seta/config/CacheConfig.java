package gov.nysenate.seta.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer
{
    @Bean
    @Override
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(springCaches());
        return cacheManager;
    }

    @Override
    public CacheResolver cacheResolver() {
        return null;
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return null;
    }

    @Bean
    public List<Cache> springCaches() {
        Cache employeeCache = new ConcurrentMapCache("employees");
        Cache supervisorCache = new ConcurrentMapCache("supervisors");
        Cache supervisorEmpCache = new ConcurrentMapCache("supervisorEmps");
        Cache transactionCache = new ConcurrentMapCache("transactions");
        return Arrays.asList(employeeCache, supervisorCache, supervisorEmpCache, transactionCache);
    }
}