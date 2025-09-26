package com.ai.qa.service.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AIClientService {

    private static final Logger logger = LoggerFactory.getLogger(AIClientService.class);

    private final List<AIClient> aiClients;

    @Value("${ai.mock.enabled:false}")
    private boolean mockEnabled;

    public AIClientService(List<AIClient> aiClients) {
        this.aiClients = aiClients;
        if (mockEnabled) {
            logger.info("AIClientService initialized for mock responses");
        } else {
            logger.info("AIClientService initialized with {} AI clients: {}",
                    aiClients.size(),
                    aiClients.stream().map(AIClient::getProviderName).toList());
        }
    }

    public Mono<String> askQuestion(String question) {
        if (mockEnabled) {
            // 模拟回答，直接返回 fallback
            return Mono.just(getFallbackAnswer(question));
        }

        // 按顺序尝试每个AI客户端，第一个成功的返回结果
        return Flux.fromIterable(aiClients)
                .concatMap(client -> {
                    logger.debug("Trying AI client: {}", client.getProviderName());
                    return client.askQuestion(question)
                            .doOnNext(
                                    answer -> logger.info("Successfully got answer from {}", client.getProviderName()))
                            .onErrorResume(e -> {
                                logger.warn("AI client {} failed: {}", client.getProviderName(), e.getMessage());
                                return Mono.empty();
                            });
                })
                .next() // 取第一个成功的响应
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("All AI clients failed, using fallback response");
                    return Mono.just(getFallbackAnswer(question));
                }));
    }

    private String getFallbackAnswer(String question) {
        // 模拟中文回答
        logger.info("Generating fallback answer for question: {}", question);
        if (question.contains("天气")) {
            logger.info("Question contains '天气', returning weather answer");
            return "今天天气晴朗，温度适宜，非常适合户外活动。";
        } else {
            logger.info("Question does not contain '天气', returning general answer");
            return "这是一个很好的问题！我可以为您提供相关信息。如果您有具体的问题，请详细描述一下。";
        }
    }
}
