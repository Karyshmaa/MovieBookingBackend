package com.kary.moviebooking.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

        @Autowired
        private StringRedisTemplate redisTemplate;

        public void saveData(){
            redisTemplate.opsForValue().set("name","Kary");
        }
        public String getData(){
            return redisTemplate.opsForValue().get("name");
        }
    }

