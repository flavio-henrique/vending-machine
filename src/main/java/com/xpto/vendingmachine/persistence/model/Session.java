package com.xpto.vendingmachine.persistence.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@RedisHash("Session")
@Builder
@Getter
public class Session implements Serializable {

    @Id
    private String username;
    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long expiration;
    private String jti;
}
