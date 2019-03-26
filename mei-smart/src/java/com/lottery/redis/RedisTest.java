package com.lottery.redis;

import java.util.Set;

import redis.clients.jedis.Jedis;

public class RedisTest {

	public static void main(String[] args) {
		// getredis();
		getAllkeys();
		//deletekeys();
		//getAllkeys();
	}

	public static void getredis() {
		Jedis jedis = new Jedis("127.0.0.1");
		System.out.println(jedis.ping());
	}

	// 测试所有key
	public static void getAllkeys() {
		System.out.println("--------------getAllkeys---------------");
		Jedis jedis = new Jedis("127.0.0.1");
		Set<String> keys = jedis.keys("*");
		for (String k : keys) {
			System.out.println(k);
			// + "=" + jedis.get(k)
		}
	}

	// 删除所有keys
	public static void deletekeys() {
		System.out.println("--------------deletekeys---------------");
		Jedis jedis = new Jedis("127.0.0.1");
		jedis.flushDB();
	}

}
