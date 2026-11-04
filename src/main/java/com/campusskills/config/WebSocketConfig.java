package com.campusskills.config;

import com.campusskills.entity.User;
import com.campusskills.repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.Collections;
import java.util.Arrays;
import java.util.Optional;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final CollegeProperties collegeProperties;

    public WebSocketConfig(JwtTokenProvider tokenProvider, UserRepository userRepository, CollegeProperties collegeProperties) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.collegeProperties = collegeProperties;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String configuredOrigins = collegeProperties.getCors().getAllowedOrigins();
        if ("*".equals(configuredOrigins.trim())) {
            registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
            registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
        } else {
            String[] origins = Arrays.stream(configuredOrigins.split(",")).map(String::trim)
                    .filter(origin -> !origin.isEmpty()).toArray(String[]::new);
            registry.addEndpoint("/ws").setAllowedOrigins(origins);
            registry.addEndpoint("/ws").setAllowedOrigins(origins).withSockJS();
        }
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (!StringUtils.hasText(authHeader)) {
                        authHeader = accessor.getFirstNativeHeader("token");
                    }
                    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                        authHeader = authHeader.substring(7);
                    }

                    if (StringUtils.hasText(authHeader) && tokenProvider.validateToken(authHeader)) {
                        Long userId = tokenProvider.getUserIdFromToken(authHeader);
                        if (userId != null) {
                            Optional<User> userOpt = userRepository.findById(userId);
                            if (userOpt.isPresent()) {
                                User user = userOpt.get();
                                Principal principal = () -> String.valueOf(user.getId());
                                accessor.setUser(principal);
                            }
                        }
                    }
                }
                return message;
            }
        });
    }
}
