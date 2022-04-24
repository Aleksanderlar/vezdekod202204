package com.vk.vezdekod.nudesources.javatask.controller.dto;

import com.vk.vezdekod.nudesources.javatask.entity.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionDto {
    private Long id;
    private String question;
    private Integer difficulty;
    private Category category;
}
