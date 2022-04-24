package com.vk.vezdekod.nudesources.javatask.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameAnswerResultDto {
    private String question;
    private Boolean isCorrect;
}
