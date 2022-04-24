package com.vk.vezdekod.nudesources.javatask.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerCheckDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long questionId;
    private Boolean isCorrect;
    private String correctAnswer;
}
