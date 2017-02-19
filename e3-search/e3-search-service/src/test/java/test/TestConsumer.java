package test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestConsumer {

	@Test
	public void testQueueConsumer() throws Exception {
		// 创建ConnectionFactory
		String brokerURL = "tcp://192.168.242.139:61616";
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
		// 创建Connection
		Connection connection = connectionFactory.createConnection();
		// 启动Connection
		connection.start();
		// 获取Session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 创建Destination(Queue\Topic)
		Queue queue = session.createQueue("test-queue");
		// 创建Consumer
		MessageConsumer consumer = session.createConsumer(queue);
		// 设置MessageListener接收消息
		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {
				// 处理消息
				try {
					if (message instanceof TextMessage) {
						TextMessage msg = (TextMessage) message;
						System.out.println(msg.getText());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// 等待
		System.in.read();
		// 释放资源
		consumer.close();
		session.close();
		connection.close();
	}

	@Test
	public void testTopicConsumer() throws Exception {
		// 创建ConnectionFactory
		String brokerURL = "tcp://192.168.242.139:61616";
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
		// 创建Connection
		Connection connection = connectionFactory.createConnection();
		// 启动Connection
		connection.start();
		// 获取Session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 创建Destination(Queue\Topic)
		Topic topic = session.createTopic("test-topic");
		// 创建Consumer
		MessageConsumer consumer = session.createConsumer(topic);
		System.out.println("console3:");
		// 设置MessageListener接收消息
		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {
				// 处理消息
				try {
					if (message instanceof TextMessage) {
						TextMessage msg = (TextMessage) message;
						System.out.println(msg.getText());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// 等待
		System.in.read();
		// 释放资源
		consumer.close();
		session.close();
		connection.close();
	}

	@Test
	public void testSpring4QueueConsumer() throws Exception{
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:spring/applicationContext-activemq.xml");
		// 等待
		System.in.read();
	}
}
