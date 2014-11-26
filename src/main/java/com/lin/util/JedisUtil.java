package com.lin.util;

import java.io.IOException;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtil {
	private static JedisPool pool = null;
	static {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(200);
		config.setMinIdle(5);
		Properties prop = new Properties();
		try {
			prop.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("redis.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String host = prop.getProperty("redis.ip");
		pool = new JedisPool(config, host);
	}

	public static JedisPool getPool() throws IOException {
		return pool;
	}

	public static void returnResource(Jedis jedis) {
		if (jedis != null) {
			pool.returnResource(jedis);
		}
	}

	public static Jedis getJedis() {
		if (pool != null) {
			return pool.getResource();
		}
		return null;
	}

}
