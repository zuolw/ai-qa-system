package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.ChatRequest;
import com.ai.qa.service.domain.service.QAService;
import com.ai.qa.service.infrastructure.client.AIClientService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
public class QAController {

    private static final Logger logger = LoggerFactory.getLogger(QAController.class);

    private final QAService qaService;
    private final AIClientService aiClientService;

    @GetMapping("/test")
    public String testFeign() {
        return "qa-service ok";
    }

    @PostMapping("/ask")
    public Mono<ResponseEntity<Map<String, String>>> askQuestion(@RequestBody Map<String, String> request) {
        logger.info("Ask endpoint called with request: {}", request);

        String question = request.get("question");
        if (question == null || question.trim().isEmpty()) {
            Map<String, String> errorResponse = Map.of("error", "问题不能为空");
            logger.warn("Empty question received");
            return Mono.just(ResponseEntity.badRequest().body(errorResponse));
        }

        logger.info("Processing question: {}", question);

        return aiClientService.askQuestion(question)
                .map(answer -> {
                    logger.info("Successfully generated answer for question: {}", question);
                    Map<String, String> successResponse = Map.of("answer", answer);
                    return ResponseEntity.ok(successResponse);
                })
                .doOnError(error -> {
                    logger.error("Error in QA service for question: {}", question, error);
                })
                .onErrorResume(e -> {
                    logger.error("Error processing question: {}", question, e);
                    String errorMessage = "服务暂时不可用，请稍后再试。";
                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("API请求错误")) {
                            errorMessage = "AI服务请求参数错误，请检查问题内容。";
                        } else if (e.getMessage().contains("parse")) {
                            errorMessage = "AI响应格式错误，请重试。";
                        }
                    }
                    Map<String, String> errorResponse = Map.of("error", errorMessage);
                    return Mono.just(ResponseEntity.internalServerError().body(errorResponse));
                });
    }

    @PostMapping("/chat")
    public Mono<ResponseEntity<String>> chat(@RequestBody ChatRequest request) {
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("问题不能为空"));
        }

        String question = request.getMessages().get(request.getMessages().size() - 1).getContent();
        if (question == null || question.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("问题不能为空"));
        }

        logger.info("Processing chat question: {}", question);

        return aiClientService.askQuestion(question)
                .map(answer -> {
                    logger.info("Chat response generated successfully");
                    return ResponseEntity.ok(answer);
                })
                .onErrorResume(e -> {
                    logger.error("Error in chat: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.internalServerError().body("服务暂时不可用，请稍后再试。"));
                });
    }

    // 修复原有的save方法（假设SaveHistoryRequest和qaHistoryService存在）
    // @PostMapping("/save")
    // public ResponseEntity<QAHistoryDTO> saveHistory(@RequestBody
    // SaveHistoryRequest request) {
    // SaveHistoryCommand command = new SaveHistoryCommand();
    // QAHistoryDTO dto = qaHistoryService.saveHistory(command);
    // return ResponseEntity.ok(dto);
    // }
}