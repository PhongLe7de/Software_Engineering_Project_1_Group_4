package com.otp.whiteboard.config;


import com.otp.whiteboard.listener.DrawSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisListenerConfig {
    @Bean
    public RedisMessageListenerContainer redisContainer(
            final RedisConnectionFactory connectionFactory,final  DrawSubscriber drawSubscriber, final ChannelTopic cursorTopic, final ChannelTopic drawTopic) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(drawSubscriber, drawTopic);
        container.addMessageListener(drawSubscriber, cursorTopic);
        return container;

    }

}
