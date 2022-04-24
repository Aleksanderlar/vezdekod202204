package com.vk.vezdekod.nudesources.javatask.controller;

import com.vk.vezdekod.nudesources.javatask.AppController;
import com.vk.vezdekod.nudesources.javatask.controller.dto.*;
import com.vk.vezdekod.nudesources.javatask.service.GameService;
import com.vk.vezdekod.nudesources.javatask.service.dto.GameAnswerResultDto;
import com.vk.vezdekod.nudesources.javatask.service.dto.GameInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("game")
public class GameController extends AppController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<GameInfoDto>> createGame(@RequestBody CreateGameDto dto) {
        if (dto.getCount() == null) {
            return badRequest("Не заполнено количество игры");
        }

        if (dto.getMinDifficulty() == null) {
            return badRequest("Не заполнена минимальная сложность игры");
        }

        if (dto.getMaxDifficulty() == null) {
            return badRequest("Не заполнена максимальная сложность игры");
        }

        if (dto.getCount() > 100) {
            dto.setCount(100);
        }
        return ok(gameService.createGame(dto));
    }

    @GetMapping("{gameId}/{number}")
    public ResponseEntity<ResponseDto<QuestionDto>> getQuestion(@PathVariable String gameId, @PathVariable Integer number) {
        return gameService.getQuestion(gameId, number)
                .map(question -> ok(QuestionDto.builder()
                        .id(question.getId())
                        .question(question.getQuestion())
                        .difficulty(question.getDifficulty())
                        .category(question.getCategory())
                        .build())
                ).orElse(badRequest("Не найден вопрос"));
    }

    @PostMapping("{gameId}/{number}/check")
    public ResponseEntity<ResponseDto<AnswerCheckDto>> checkAnswer(@PathVariable String gameId, @PathVariable Integer number, @RequestBody AnswerDto answer) {
        return gameService.checkAnswer(gameId, number, answer.getAnswer())
                .map(this::ok)
                .orElse(badRequest("Не найден вопрос"));
    }

    @PostMapping("{gameId}/finish")
    public ResponseEntity<ResponseDto<List<GameAnswerResultDto>>> finish(@PathVariable String gameId) {
        var list = gameService.finish(gameId);
        if (list.isEmpty()) {
            return badRequest("Не найдена игра");
        }

        return ok(list);
    }
}
