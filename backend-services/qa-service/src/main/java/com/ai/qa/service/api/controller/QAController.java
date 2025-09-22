package com.ai.qa.service.api.controller;
import com.ai.qa.service.domain.service.QAService;
import com.ai.qa.service.infrastructure.client.GeminiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
public class QAController {

    private final QAService qaService;
    private final GeminiClient geminiClient;

    @GetMapping("/test")
    public String testFeign() {
        return "qa-service ok";
    }

    @PostMapping("/ask")
    public Mono<ResponseEntity<Map<String, String>>> askQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        if (question == null || question.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Question is required")));
        }

        return geminiClient.askQuestion(question)
                .map(answer -> ResponseEntity.ok(Map.of("answer", answer)))
                .onErrorResume(e -> Mono
                        .just(ResponseEntity.internalServerError().body(Map.of("error", "Failed to get answer"))));
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
