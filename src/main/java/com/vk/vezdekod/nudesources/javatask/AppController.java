package com.vk.vezdekod.nudesources.javatask;

import com.vk.vezdekod.nudesources.javatask.controller.dto.ResponseDto;
import org.springframework.http.ResponseEntity;

public class AppController {
    protected <T> ResponseEntity<ResponseDto<T>> ok(T body) {
        return ResponseEntity.ok(ResponseDto.ok(body));
    }

    protected  <T> ResponseEntity<ResponseDto<T>> badRequest(String description) {
        return ResponseEntity.badRequest().body(ResponseDto.badRequest(description));
    }
}
