package com.lottery.redis;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ibatis.cache.Cache;
//import org.hibernate.validator.internal.util.logging.Log_.logger;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

//使用第三方缓存服务器，处理二级缓存
//适合使用缓存的数据：1、很少更新的数据   2、经常被用到的数据  3、数据量不大的数据
//短信验证码、具有时间性的商品展示、秒杀、推荐新闻、最近、最热、点击率最高、活跃度最高、用户最近访问记录
//取最新N个数据的操作、排行榜应用，取TOP N操作、需要精准设定过期时间的应用、计数器应用、实时系统，反垃圾系统、Pub/Sub构建实时消息系统、构建队列系统、缓存
public class RedisCache implements Cache {
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	// Jedis客户端
	//@Autowired
	private Jedis redisClient = createClient();
	private String id;

	public RedisCache(final String id) {
		if (id == null)
			throw new IllegalArgumentException("必须传入ID");
		System.out.println("MybatisRedisCache:id=" + id);
		this.id = id;
	}

	@Override
	public void clear() {
		redisClient.flushDB();
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public Object getObject(Object key) {
		byte[] ob = redisClient.get(SerializeUtil.serialize(key.toString()));
		if (ob == null)
			return null;
		Object value = SerializeUtil.unserialize(ob);
		return value;
	}

	@Override
	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

	@Override
	public int getSize() {
		return Integer.valueOf(redisClient.dbSize().toString());
	}

	@Override
	public void putObject(Object key, Object value) {
		redisClient.set(SerializeUtil.serialize(key.toString()), SerializeUtil.serialize(value));
	}

	@Override
	public Object removeObject(Object key) {
		return redisClient.expire(SerializeUtil.serialize(key.toString()), 0);
	}

	protected static Jedis createClient() {
		try {
			@SuppressWarnings("resource")
			JedisPool pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379,100000);
			return pool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException("初始化连接池错误");
	}

	
	

}