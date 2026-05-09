package com.ecommerce.cartrecovery.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

@Service
public class RedisRepository {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean set(String key, Object value) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(value);
        redisTemplate.opsForValue().set(key, json);
        return true;
    }

    public boolean zAdd(String key, String member, long score) {
        redisTemplate.opsForZSet().add(key, member, score);
        return true;
    }

    public Set<String> zRangeByScore(String key, long maxScore) {
        return redisTemplate.opsForZSet().rangeByScore(
                key,
                0,
                maxScore
        );
    }
    public Set<ZSetOperations.TypedTuple<String>> getCartIdsWithScores(String key, long score) {
        return redisTemplate.opsForZSet()
                .rangeByScoreWithScores(
                        key,
                        0,
                        score
                );
    }

    public Long delete(String key, Set<String> cartIds) {
        return redisTemplate.opsForZSet().remove(key, cartIds.toArray());
    }
}