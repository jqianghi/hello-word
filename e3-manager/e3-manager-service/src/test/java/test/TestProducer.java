package test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class TestProducer {

	@Test
	public void testQueueProducer() throws Exception {
		// 创建ConnectionFactory
		String brokerURL = "tcp://192.168.242.139:61616";
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
		// 通过ConnectionFactory获取Connection
		Connection connection = connectionFactory.createConnection();
		// 启动Connection
		connection.start();
		// 通过Connection获取Session
		// 第一个参数:是否启用activemq的事务
		// 第二个参数:设置应答模式(broker接收消息成功失败需要应答给消息生产者)
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 通过Session创建消息目的地(Queue\Topic)
		Queue queue = session.createQueue("test-queue");
		// 通过Session创建Producer
		MessageProducer producer = session.createProducer(queue);
		// 通过Session创建Message
		TextMessage message = session.createTextMessage("queue:send message");
		// 调用Producer的方法发送消息到指定的目的地中
		producer.send(message);
		// 释放资源
		producer.close();
		session.close();
		connection.close();
	}

	@Test
	public void testTopicProducer() throws Exception {
		// 创建ConnectionFactory
		String brokerURL = "tcp://192.168.242.139:61616";
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
		// 通过ConnectionFactory获取Connection
		Connection connection = connectionFactory.createConnection();
		// 启动Connection
		connection.start();

		// 通过Connection获取Session
		// 第一个参数:是否启用activemq的事务
		// 第二个参数:设置应答模式(broker接收消息成功失败需要应答给消息生产者)
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 通过Session创建消息目的地(Queue\Topic)
		Topic topic = session.createTopic("test-topic");
		// 通过Session创建Producer
		MessageProducer producer = session.createProducer(topic);
		// 通过Session创建Message
		TextMessage message = session.createTextMessage("topic:send message");
		// 调用Producer的方法发送消息到指定的目的地中
		producer.send(message);
		// 释放资源
		producer.close();
		session.close();
		connection.close();
	}

	@Test
	public void testSpring4QueueProducer() throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:spring/applicationContext-activemq.xml");
		// 获取Destination实例
		Destination destination = context.getBean(Destination.class);
		// 获取JMSTemplate实例
		JmsTemplate template = context.getBean(JmsTemplate.class);
		// 调用Template的send方法发送消息
		template.send(destination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage("test-spring-queue");
			}
		});
	}
}
