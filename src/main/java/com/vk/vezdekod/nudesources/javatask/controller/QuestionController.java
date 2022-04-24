package com.vk.vezdekod.nudesources.javatask.controller;

import com.vk.vezdekod.nudesources.javatask.AppController;
import com.vk.vezdekod.nudesources.javatask.controller.dto.AnswerCheckDto;
import com.vk.vezdekod.nudesources.javatask.controller.dto.AnswerDto;
import com.vk.vezdekod.nudesources.javatask.controller.dto.QuestionDto;
import com.vk.vezdekod.nudesources.javatask.controller.dto.ResponseDto;
import com.vk.vezdekod.nudesources.javatask.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("question")
public class QuestionController extends AppController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("random")
    public ResponseEntity<ResponseDto<QuestionDto>> getRandom() {
        var list = questionService.getRandom(1);
        if (list.isEmpty()) {
            return badRequest("Не найден ни один вопрос");
        }

        var question = list.get(0);
        return ok(QuestionDto.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .difficulty(question.getDifficulty())
                .category(question.getCategory())
                .build());
    }

    @PostMapping("check")
    public ResponseEntity<ResponseDto<AnswerCheckDto>> checkAnswer(@RequestBody AnswerDto answer) {
        if (answer.getQuestionId() == null) {
            return badRequest("Не заполнен id вопроса");
        }

        if (answer.getAnswer() == null) {
            return badRequest("Не заполнен ответ");
        }

        return questionService.findById(answer.getQuestionId())
                .map(question -> ok(AnswerCheckDto.builder()
                        .questionId(question.getId())
                        .correctAnswer(question.getAnswer())
                        .isCorrect(question.getAnswer().equals(answer.getAnswer()))
                        .build())
                )
                .orElse(badRequest("Не найден вопрос"));
    }
}
