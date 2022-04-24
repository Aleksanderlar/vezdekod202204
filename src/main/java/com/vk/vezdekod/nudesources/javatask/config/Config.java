package com.vk.vezdekod.nudesources.javatask.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableCaching
public class Config {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("questions", "games", "answers");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
