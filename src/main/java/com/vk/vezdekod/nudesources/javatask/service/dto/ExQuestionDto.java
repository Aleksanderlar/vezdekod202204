package com.vk.vezdekod.nudesources.javatask.service.dto;

import com.vk.vezdekod.nudesources.javatask.entity.Category;
import lombok.Data;

@Data
public class ExQuestionDto {
    private Long id;
    private String question;
    private String answer;
    private Integer value;
    private ExtCategoryDto category;
}
