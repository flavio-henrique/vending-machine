package com.xpto.vendingmachine.persistence.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@RedisHash("Session")
@Builder
@Getter
public class TokenRevoke {
    @Id
    private String jti;
    @TimeToLive(unit = TimeUnit.DAYS)
    private Long expiration;
}
