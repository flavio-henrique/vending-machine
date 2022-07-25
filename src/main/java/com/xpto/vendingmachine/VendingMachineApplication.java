package com.xpto.vendingmachine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableRedisRepositories
public class VendingMachineApplication {

	public static void main(String[] args) {
		SpringApplication.run(VendingMachineApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
	}

	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory();
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		return template;
	}
}
