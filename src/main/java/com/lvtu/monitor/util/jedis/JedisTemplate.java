package com.lvtu.monitor.util.jedis;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

import com.lvtu.monitor.util.ConfigHelper;

public class JedisTemplate {

	private static Log logger = Logs.get();
	private Pool<Jedis> jedisPool;
	private static JedisTemplate readerInstance;
	private static JedisTemplate writerInstance;
	private static Object ReadLock = new Object();
	private static Object WriteLock = new Object();
	private static Properties properties = ConfigHelper.getProperties("redis.properties");
	// redis是否启用
	private static boolean isRedisEnable = "true".equals(properties.getProperty("redis.enable"));

	public JedisTemplate(Pool<Jedis> jedisPool) {
		this.jedisPool = jedisPool;
	}

	/**
	 * writer instance
	 *
	 * @return 如果配置了redis不启用，返回null
	 */
	public static JedisTemplate getWriterInstance(){
		if (!isRedisEnable) {
			return null;
		}
		if (writerInstance==null) {
			synchronized(WriteLock) {
				if (writerInstance==null) {
					String ip = properties.getProperty("redis.writer.server");
					int port = Integer.parseInt(properties.getProperty("redis.writer.port"));
					int maxIdle = Integer.parseInt(properties.getProperty("redis.writer.maxIdle"));
					int maxTotal = Integer.parseInt(properties.getProperty("redis.writer.maxTotal"));
					int checkingIntervalSecs = Integer.parseInt(properties.getProperty("redis.writer.checkingIntervalSecs"));
					int evictableIdleTimeSecs = Integer.parseInt(properties.getProperty("redis.writer.evictableIdleTimeSecs"));
					int maxWaitMillis = Integer.parseInt(properties.getProperty("redis.writer.maxWaitMillis"));

					JedisPoolConfig config = JedisUtils.createPoolConfig(maxIdle, maxTotal, checkingIntervalSecs, evictableIdleTimeSecs, maxWaitMillis);
					config.setTestWhileIdle(true);
			    	writerInstance = new JedisTemplate(new JedisPool(config, ip, port));
				}
			}
		}
		return writerInstance;
	}
	
	/**
	 * reader instance
	 *
	 * @return 如果配置了redis不启用，返回null
	 */
	public static JedisTemplate getReaderInstance(){
		if (!isRedisEnable) {
			return null;
		}
		if (readerInstance==null) {
			synchronized(ReadLock) {
				if (readerInstance==null) {
					String ip = properties.getProperty("redis.reader.server");
					int port = Integer.parseInt(properties.getProperty("redis.reader.port"));
					int maxIdle = Integer.parseInt(properties.getProperty("redis.reader.maxIdle"));
					int maxTotal = Integer.parseInt(properties.getProperty("redis.reader.maxTotal"));
					int checkingIntervalSecs = Integer.parseInt(properties.getProperty("redis.reader.checkingIntervalSecs"));
					int evictableIdleTimeSecs = Integer.parseInt(properties.getProperty("redis.reader.evictableIdleTimeSecs"));
					int maxWaitMillis = Integer.parseInt(properties.getProperty("redis.reader.maxWaitMillis"));
					
					JedisPoolConfig config = JedisUtils.createPoolConfig(maxIdle, maxTotal, checkingIntervalSecs, evictableIdleTimeSecs, maxWaitMillis);
					config.setTestWhileIdle(true);
					readerInstance = new JedisTemplate(new JedisPool(config, ip, port));
				}
			}
		}
		return readerInstance;
	}

	/**
	 * 执行有返回结果的action。
	 */
	public <T> T execute(JedisAction<T> jedisAction) throws JedisException {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = jedisPool.getResource();
			return jedisAction.action(jedis);
		} catch (JedisConnectionException e) {
			logger.error("Redis connection lost.", e);
			broken = true;
			throw e;
		} finally {
			closeResource(jedis, broken);
		}
	}

	/**
	 * 执行无返回结果的action。
	 */
	public void execute(JedisActionNoResult jedisAction) throws JedisException {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = jedisPool.getResource();
			jedisAction.action(jedis);
		} catch (JedisConnectionException e) {
			logger.error("Redis connection lost.", e);
			broken = true;
			throw e;
		} finally {
			closeResource(jedis, broken);
		}
	}

	/**
	 * 根据连接是否已中断的标志，分别调用returnBrokenResource或returnResource。
	 */
	protected void closeResource(Jedis jedis, boolean connectionBroken) {
		if (jedis != null) {
			try {
				if (connectionBroken) {
					jedisPool.returnBrokenResource(jedis);
				} else {
					jedisPool.returnResource(jedis);
				}
			} catch (Exception e) {
				logger.error("Error happen when return jedis to pool, try to close it directly.", e);
				JedisUtils.closeJedis(jedis);
			}
		}
	}

	/**
	 * 获取内部的pool做进一步的动作。
	 */
	public Pool<Jedis> getJedisPool() {
		return jedisPool;
	}

	/**
	 * 有返回结果的回调接口定义。
	 */
	public interface JedisAction<T> {
		T action(Jedis jedis);
	}

	/**
	 * 无返回结果的回调接口定义。
	 */
	public interface JedisActionNoResult {
		void action(Jedis jedis);
	}

	// ////////////// 常用方法的封装 ///////////////////////// //

	// ////////////// 公共 ///////////////////////////
	/**
	 * 删除key, 如果key存在返回true, 否则返回false。
	 */
	public Boolean del(final String... keys) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.del(keys) == 1;
			}
		});
	}

	public void flushDB() {
		execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) {
				jedis.flushDB();
			}
		});
	}

	// ////////////// 关于String ///////////////////////////
	/**
	 * 如果key不存在, 返回null.
	 */
	public String get(final String key) {
		return execute(new JedisAction<String>() {

			@Override
			public String action(Jedis jedis) {
				return jedis.get(key);
			}
		});
	}

	/**
	 * 如果key不存在, 返回null.
	 */
	public Long getAsLong(final String key) {
		String result = get(key);
		return result != null ? Long.valueOf(result) : null;
	}

	/**
	 * 如果key不存在, 返回null.
	 */
	public Integer getAsInt(final String key) {
		String result = get(key);
		return result != null ? Integer.valueOf(result) : null;
	}

	public void set(final String key, final String value) {
		execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) {
				jedis.set(key, value);
			}
		});
	}

	public void setex(final String key, final String value, final int seconds) {
		execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) {
				jedis.setex(key, seconds, value);
			}
		});
	}

	/**
	 * 如果key还不存在则进行设置，返回true，否则返回false.
	 */
	public Boolean setnx(final String key, final String value) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.setnx(key, value) == 1;
			}
		});
	}

	/**
	 * 综合setNX与setEx的效果。
	 */
	public Boolean setnxex(final String key, final String value, final int seconds) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				String result = jedis.set(key, value, "NX", "EX", seconds);
				return JedisUtils.isStatusOk(result);
			}
		});
	}

	public Long incr(final String key) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.incr(key);
			}
		});
	}

	public Long decr(final String key) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.decr(key);
			}
		});
	}

	// ////////////// 关于List ///////////////////////////
	public void lpush(final String key, final String... values) {
		execute(new JedisActionNoResult() {
			@Override
			public void action(Jedis jedis) {
				jedis.lpush(key, values);
			}
		});
	}

	public String rpop(final String key) {
		return execute(new JedisAction<String>() {

			@Override
			public String action(Jedis jedis) {
				return jedis.rpop(key);
			}
		});
	}
	
	
	

	public String hmset(final String key,final Map<String,String> map){
		return execute(new JedisAction<String>() {

			@Override
			public String action(Jedis jedis) {
				return jedis.hmset(key, map);
			}
			
		});
	}
	
	public Long hdel(final String key,final String ...fields){
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.hdel(key, fields);
			}
		});
	}
	
	
	public List<String> hmget(final String key,final String ...fields){
		return execute(new JedisAction<List<String>>() {

			@Override
			public List<String> action(Jedis jedis) {
				return jedis.hmget(key, fields);
			}
		});
	}

	/**
	 * 返回List长度, key不存在时返回0，key类型不是list时抛出异常.
	 */
	public Long llen(final String key) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.llen(key);
			}
		});
	}
	
	/**
	 * 查询list中的指定范围的元素
	 */
	public List<String> lrange(final String key,final long start, final long end) {
		return execute(new JedisAction<List<String>>() {

			@Override
			public List<String> action(Jedis jedis) {
				return jedis.lrange(key, start, end);
			}
		});
	}
	
	/**
	 * 删除List中的第一个等于value的元素，value不存在或key不存在时返回false.
	 */
	public Boolean lremOne(final String key, final String value) {
		return execute(new JedisAction<Boolean>() {
			@Override
			public Boolean action(Jedis jedis) {
				return (jedis.lrem(key, 1, value)==1);
			}
		});
	}

	/**
	 * 删除List中的所有等于value的元素，value不存在或key不存在时返回false.
	 */
	public Boolean lremAll(final String key, final String value) {
		return execute(new JedisAction<Boolean>() {
			@Override
			public Boolean action(Jedis jedis) {
				return (jedis.lrem(key, 0, value) > 0);
			}
		});
	}
	
	// ////////////// 关于Set ///////////////////////////
	/**
	 * 加入set, 如果member在set里已存在, 返回false, 否则返回true.
	 */
	public Boolean sadd(final String key, final String member) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.sadd(key, member) == 1;
			}
		});
	}

	/**
	 * 删除set中的元素，成功删除返回true，key或member不存在返回false。
	 */
	public Boolean srem(final String key, final String member) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.srem(key, member) == 1;
			}
		});
	}
	
	/**
	 * 查询set中的元素，成功返回元素集合，key或member不存在返回null。
	 */
	public Set<String> smembers(final String key) {
		return execute(new JedisAction<Set<String>>() {

			@Override
			public Set<String> action(Jedis jedis) {
				return jedis.smembers(key);
			}
		});
	}

	/**
	 * 返回set长度, key不存在时返回0.
	 */
	public Long scard(final String key) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.scard(key);
			}
		});
	}

	// ////////////// 关于Sorted Set ///////////////////////////
	/**
	 * 加入Sorted set, 如果member在Set里已存在, 只更新score并返回false, 否则返回true.
	 */
	public Boolean zadd(final String key, final String member, final double score) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.zadd(key, score, member) == 1;
			}
		});
	}

	/**
	 * 删除sorted set中的元素，成功删除返回true，key或member不存在返回false。
	 */
	public Boolean zrem(final String key, final String member) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.zrem(key, member) == 1;
			}
		});
	}

	/**
	 * 当key不存在时返回null.
	 */
	public Double zscore(final String key, final String member) {
		return execute(new JedisAction<Double>() {

			@Override
			public Double action(Jedis jedis) {
				return jedis.zscore(key, member);
			}
		});
	}

	/**
	 * 返回sorted set长度, key不存在时返回0.
	 */
	public Long zcard(final String key) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.zcard(key);
			}
		});
	}

	// ////////////// 关于 List<Object> ///////////////////////////
	public <T> void setArray(final String key, final List<T> value, final int seconds) {
		if (isRedisEnable)
			this.setex(key, Json.toJson(value), seconds);
	}

	public <T> void setArray(final String key, final List<T> value) {
		if (isRedisEnable)
			this.set(key, Json.toJson(value));
	}

	/** key 不存在时返回 null */
	public <T> List<T> getArray(final String key, final Class<T> clazz) {
		if (!isRedisEnable)
			return null;
		return execute(new JedisAction<List<T>>() {
			@Override
			public List<T> action(Jedis jedis) {
				logger.info("jedis getArray(), key="+key);
				return Json.fromJsonAsList(clazz, jedis.get(key));
			}
		});
	}
	
	public Long expire(final String key, final int seconds) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.expire(key, seconds);
			}
		});
	}
	
	public Set<String> keys(final String key) {
		return execute(new JedisAction<Set<String>>() {

			@Override
			public Set<String> action(Jedis jedis) {
				return jedis.keys(key);
			}
		});
	}
}
