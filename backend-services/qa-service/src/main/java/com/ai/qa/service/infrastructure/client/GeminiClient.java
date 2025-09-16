package com.ai.qa.service.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class GeminiClient {

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public GeminiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<String> askQuestion(String question) {
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text", question)
                        })
                });

        return webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    // 解析Gemini API响应，提取回答文本
                    Map<String, Object> candidates = (Map<String, Object>) ((java.util.List) response.get("candidates"))
                            .get(0);
                    Map<String, Object> content = (Map<String, Object>) candidates.get("content");
                    java.util.List parts = (java.util.List) content.get("parts");
                    Map<String, Object> part = (Map<String, Object>) parts.get(0);
                    return (String) part.get("text");
                });
    }
}
