package com.vk.vezdekod.nudesources.javatask.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGameDto {
    private Integer count;
    private Integer minDifficulty;
    private Integer maxDifficulty;
    private List<Long> categories;
}
