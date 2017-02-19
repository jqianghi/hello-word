package test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class TestJedisClient {

	private int port = 6379;
	private String host = "192.168.242.139";

	@Test
	public void testJedis() {
		// 创建Jedis实例
		Jedis jedis = new Jedis(host, port);

		// jedis的api方法实现数据的存取
		jedis.set("s11", "21312312");

		System.out.println(jedis.get("s11"));

		// 释放资源
		jedis.close();
	}

	@Test
	public void testJedisPool() {
		// 创建JedisPool
		JedisPool pool = new JedisPool(host, port);
		// 通过pool获取jedis实例
		Jedis jedis = pool.getResource();
		// jedis的api方法实现数据的存取
		System.out.println(jedis.get("s11"));
		// 释放资源
		jedis.close();
		pool.close();
	}

	@Test
	public void testJedisCluster() {
		// 创建JedisCluster实例
		Set<HostAndPort> nodes = new HashSet<>();
		nodes.add(new HostAndPort(host, 7001));
		nodes.add(new HostAndPort(host, 7002));
		nodes.add(new HostAndPort(host, 7003));
		nodes.add(new HostAndPort(host, 7004));
		nodes.add(new HostAndPort(host, 7005));
		nodes.add(new HostAndPort(host, 7006));
		JedisCluster cluster = new JedisCluster(nodes);
		// 操作api方法实现存取
		cluster.set("s4", "4444");
		System.out.println(cluster.get("s1"));
		// 释放资源
		cluster.close();
	}

	@Test
	public void testSpring4JedisPool() {
		// 获取JedisPool实例
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:spring/applicationContext-redis.xml");
		JedisPool pool = context.getBean(JedisPool.class);
		// 通过pool获取jedis实例
		Jedis jedis = pool.getResource();
		// jedis的api方法实现数据的存取
		System.out.println(jedis.get("s11"));

		// 释放资源
		jedis.close();
	}

	@Test
	public void testSpring4JedisCluster() {
		// 获取JedisPool实例
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:spring/applicationContext-redis.xml");
		JedisCluster cluster = context.getBean(JedisCluster.class);
		// 操作api方法实现存取
		System.out.println(cluster.get("s1"));
	}
}
