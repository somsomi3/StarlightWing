package com.minjoo.StarlightWing.service.impl;

import com.minjoo.StarlightWing.service.TokenBlackListService;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlackListServiceImpl implements TokenBlackListService {

    //Redis기반의 블랙리스트: 레디스를 데이터 저장소로 사용(빠른성능, 영속성, 데이터TTL)
    private final RedisTemplate<String, Object> redisTemplate;

    private final String REDIS_BLACK_LIST_KEY = "tokenBlackList";

    @Override
    public void addTokenToList(String value) {
        redisTemplate.opsForList().rightPush(REDIS_BLACK_LIST_KEY, value);
    }


    @Override
    public boolean isContainToken(String value) {
        List<Object> allItems = redisTemplate.opsForList().range(REDIS_BLACK_LIST_KEY, 0, -1);
        return allItems.stream()
                .anyMatch(item -> item.equals(value));
    }


    public List<Object> getTokenBlackList() {
        return redisTemplate.opsForList().range(REDIS_BLACK_LIST_KEY, 0, -1);
    }


    @Override
    public void removeToken(String value) {
        redisTemplate.opsForList().remove(REDIS_BLACK_LIST_KEY, 0, value);
    }

    //토큰 TTL 추가
    public void addTokenToList(String value, long ttlInSeconds) {
        redisTemplate.opsForValue().set(value, "blacklisted", ttlInSeconds, TimeUnit.SECONDS);
    }
}
