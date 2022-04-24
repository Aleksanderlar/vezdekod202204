package com.vk.vezdekod.nudesources.javatask;

import com.vk.vezdekod.nudesources.javatask.controller.dto.*;
import com.vk.vezdekod.nudesources.javatask.entity.Question;
import com.vk.vezdekod.nudesources.javatask.service.dto.GameAnswerResultDto;
import com.vk.vezdekod.nudesources.javatask.service.dto.GameInfoDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameTests {
    private static String gameId;
    private static Long questionId;
    private static LinkedList<Question> questions;

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void createGame() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/game",
                HttpMethod.POST,
                new HttpEntity<>(new CreateGameDto(10, 0, 2000, null)),
                new ParameterizedTypeReference<ResponseDto<GameInfoDto>>() {
                }).getBody();
        assert dto != null;
        assert "ok".equals(dto.getCode());

        var result = dto.getResult();
        assert result != null;
        gameId = result.getId();
        questions = (LinkedList<Question>) cacheManager.getCache("games").get(gameId, LinkedList.class);
    }

    @Test
    void getFirstQuestion() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/game/" + gameId + "/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ResponseDto<QuestionDto>>() {
                }).getBody();
        assert dto != null;
        assert "ok".equals(dto.getCode());

        var result = dto.getResult();
        assert !result.getQuestion().isBlank();
    }

    @Test
    void answerFirstQuestion() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/game/" + gameId + "/1",
                HttpMethod.POST,
                new HttpEntity<>(new AnswerDto(null, "Ответ")),
                new ParameterizedTypeReference<ResponseDto<AnswerCheckDto>>() {
                }).getBody();
        assert dto != null;
        assert "ok".equals(dto.getCode());

        var result = dto.getResult();
        assert !result.getIsCorrect();
        assert !result.getCorrectAnswer().equals("Ответ");
    }

    @Test
    void answerSecondQuestion() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/game/" + gameId + "/1",
                HttpMethod.POST,
                new HttpEntity<>(new AnswerDto(null, "Ответ")),
                new ParameterizedTypeReference<ResponseDto<AnswerCheckDto>>() {
                }).getBody();
        assert dto != null;
        assert "ok".equals(dto.getCode());

        var result = dto.getResult();
        assert !result.getIsCorrect();
        assert !result.getCorrectAnswer().equals("Ответ");
    }


    @Test
    void finishGame() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/game/" + gameId + "/finish",
                HttpMethod.POST,
                new HttpEntity<>(Collections.emptyMap()),
                new ParameterizedTypeReference<ResponseDto<List<GameAnswerResultDto>>>() {
                }).getBody();
        assert dto != null;
        assert "ok".equals(dto.getCode());

        var result = dto.getResult();
        assert result.size() == questions.size();
    }
}
