package com.ai.qa.service.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AIClientService {

    private static final Logger logger = LoggerFactory.getLogger(AIClientService.class);

    private final List<AIClient> aiClients;

    public AIClientService(List<AIClient> aiClients) {
        this.aiClients = aiClients;
        logger.info("Available AI clients: {}", aiClients.stream().map(AIClient::getProviderName).toList());
    }

    public Mono<String> askQuestion(String question) {
        return askQuestionWithClients(question, 0);
    }

    private Mono<String> askQuestionWithClients(String question, int clientIndex) {
        if (clientIndex >= aiClients.size()) {
            logger.error("All AI clients failed for question: {}", question);
            return Mono.just(getFallbackAnswer(question));
        }

        AIClient client = aiClients.get(clientIndex);
        logger.debug("Trying AI client: {} for question: {}", client.getProviderName(), question);

        return client.askQuestion(question)
                .doOnNext(answer -> logger.info("Successfully got answer from {}: {}", client.getProviderName(),
                        answer.substring(0, Math.min(100, answer.length()))))
                .onErrorResume(throwable -> {
                    logger.warn("{} client failed: {}, trying next client", client.getProviderName(),
                            throwable.getMessage());
                    return askQuestionWithClients(question, clientIndex + 1);
                });
    }

    private String getFallbackAnswer(String question) {
        // 提供简单的备用回答
        if (question.toLowerCase().contains("hello") || question.toLowerCase().contains("hi")) {
            return "Hello! I'm currently experiencing some technical difficulties with all my AI services. How can I help you with basic information?";
        } else if (question.toLowerCase().contains("how") && question.toLowerCase().contains("you")) {
            return "I'm an AI assistant, but I'm currently having trouble connecting to all my AI services. I can provide basic responses for now.";
        } else {
            return "I'm sorry, but I'm currently experiencing technical difficulties with all my AI services. Please try again later or contact support if the issue persists.";
        }
    }
}
