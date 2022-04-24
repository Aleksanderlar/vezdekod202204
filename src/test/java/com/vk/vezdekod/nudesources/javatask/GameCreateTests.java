package com.vk.vezdekod.nudesources.javatask;

import com.vk.vezdekod.nudesources.javatask.controller.dto.CreateGameDto;
import com.vk.vezdekod.nudesources.javatask.controller.dto.ResponseDto;
import com.vk.vezdekod.nudesources.javatask.service.dto.GameInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameCreateTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createGame1() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/game",
                HttpMethod.POST,
                new HttpEntity<>(new CreateGameDto()),
                new ParameterizedTypeReference<ResponseDto<GameInfoDto>>() {
                }).getBody();
        assert dto != null;
        assert "bad_request".equals(dto.getCode());

        assert "Не заполнено количество игры".equals(dto.getDescription());
        var result = dto.getResult();
        assert result == null;
    }

    @Test
    void createGame2() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/game",
                HttpMethod.POST,
                new HttpEntity<>(new CreateGameDto(10, null, null, null)),
                new ParameterizedTypeReference<ResponseDto<GameInfoDto>>() {
                }).getBody();
        assert dto != null;
        assert "bad_request".equals(dto.getCode());

        assert "Не заполнена минимальная сложность игры".equals(dto.getDescription());
        var result = dto.getResult();
        assert result == null;
    }


    @Test
    void createGame3() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/game",
                HttpMethod.POST,
                new HttpEntity<>(new CreateGameDto(10, 0, null, null)),
                new ParameterizedTypeReference<ResponseDto<GameInfoDto>>() {
                }).getBody();
        assert dto != null;
        assert "bad_request".equals(dto.getCode());

        assert "Не заполнена максимальная сложность игры".equals(dto.getDescription());
        var result = dto.getResult();
        assert result == null;
    }

    @Test
    void createGame4() {
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
    }

    @Test
    void createGame5() {
        var dto = restTemplate.exchange(
                "http://localhost:" + port + "/game",
                HttpMethod.POST,
                new HttpEntity<>(new CreateGameDto(10, 0, 2000, List.of(2L))),
                new ParameterizedTypeReference<ResponseDto<GameInfoDto>>() {
                }).getBody();
        assert dto != null;
        assert "ok".equals(dto.getCode());

        var result = dto.getResult();
        assert result != null;
    }

    @Test
    void createGame6() {
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

        assert result.getCount() == 10;
    }
}
