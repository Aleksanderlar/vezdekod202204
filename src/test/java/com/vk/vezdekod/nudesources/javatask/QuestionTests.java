package com.vk.vezdekod.nudesources.javatask;

import com.vk.vezdekod.nudesources.javatask.controller.dto.AnswerCheckDto;
import com.vk.vezdekod.nudesources.javatask.controller.dto.AnswerDto;
import com.vk.vezdekod.nudesources.javatask.controller.dto.QuestionDto;
import com.vk.vezdekod.nudesources.javatask.controller.dto.ResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void loadRandomQuestion() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/question/random",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ResponseDto<QuestionDto>>() {
                }).getBody();
        assert dto != null;
        assert "ok".equals(dto.getCode());

        var result = dto.getResult();
        assert result.getQuestion() != null;
        assert result.getCategory() != null;
        assert result.getDifficulty() != null;
    }

    @Test
    void checkCorrectAnswer() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/question/check",
                HttpMethod.POST,
                new HttpEntity<>(new AnswerDto(1L, "Пушкин")),
                new ParameterizedTypeReference<ResponseDto<AnswerCheckDto>>() {
                }).getBody();

        assert dto != null;
        assert "ok".equals(dto.getCode());

        var result = dto.getResult();
        assert result.getIsCorrect();
        assert result.getQuestionId() == 1L;
        assert "Пушкин".equals(result.getCorrectAnswer());
    }

    @Test
    void checkIncorrectAnswer() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/question/check",
                HttpMethod.POST,
                new HttpEntity<>(new AnswerDto(2L, "Пушкин")),
                new ParameterizedTypeReference<ResponseDto<AnswerCheckDto>>() {
                }).getBody();

        assert dto != null;
        assert "ok".equals(dto.getCode());

        var result = dto.getResult();
        assert !result.getIsCorrect();
        assert result.getQuestionId() == 2L;
        assert "Гоголь".equals(result.getCorrectAnswer());
    }


    @Test
    void checkIncorrectRequest1() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/question/check",
                HttpMethod.POST,
                new HttpEntity<>(new AnswerDto()),
                new ParameterizedTypeReference<ResponseDto<AnswerCheckDto>>() {
                }).getBody();

        assert dto != null;
        assert "bad_request".equals(dto.getCode());

        assert "Не заполнен id вопроса".equals(dto.getDescription());
        var result = dto.getResult();
        assert result == null;
    }

    @Test
    void checkIncorrectRequest2() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/question/check",
                HttpMethod.POST,
                new HttpEntity<>(new AnswerDto(2L, null)),
                new ParameterizedTypeReference<ResponseDto<AnswerCheckDto>>() {
                }).getBody();

        assert dto != null;
        assert "bad_request".equals(dto.getCode());

        assert "Не заполнен ответ".equals(dto.getDescription());
        var result = dto.getResult();
        assert result == null;
    }
}
