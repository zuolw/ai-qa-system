package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.domain.service.QAService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
public class QAController {

    private final QAService qaService;

    @GetMapping("/test")
    public String testFeign() {
        System.out.println("测试feign");
        return qaService.processQuestion(1L);
    }


    @PostMapping("/save")
    public ReponseEntity<QAHistoryDTO> saveHistory(@RequestBody SaveHistoryRequest request){
//        request.getUserId
        SaveHistoryCommand command new = SaveHistoryCommand()

        QAHistoryDTO dto= qaHistorySerive.saveHistory(command);

        return  new ReponseEntity(dto) ;
    }
}
