package com.vk.vezdekod.nudesources.javatask.service;

import com.vk.vezdekod.nudesources.javatask.controller.dto.AnswerCheckDto;
import com.vk.vezdekod.nudesources.javatask.controller.dto.CreateGameDto;
import com.vk.vezdekod.nudesources.javatask.entity.Question;
import com.vk.vezdekod.nudesources.javatask.service.dto.GameAnswerResultDto;
import com.vk.vezdekod.nudesources.javatask.service.dto.GameInfoDto;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GameService {

    private final QuestionService questionService;
    private final CacheManager cacheManager;

    public GameService(QuestionService questionService, CacheManager cacheManager) {
        this.questionService = questionService;
        this.cacheManager = cacheManager;
    }

    private Cache cache() {
        return cacheManager.getCache("games");
    }

    private Cache answersCache() {
        return cacheManager.getCache("answers");
    }

    public GameInfoDto createGame(CreateGameDto dto) {
        Set<Question> questions = new HashSet<>();
        var attempCount = 0;
        while (questions.size() < dto.getCount() && attempCount < 10) {
            attempCount++;
            questionService.getRandom(dto.getCount()).forEach(question -> {
                var canAdd = questions.size() < dto.getCount();
                canAdd = canAdd && (dto.getCategories() == null || dto.getCategories().isEmpty() || dto.getCategories().contains(question.getCategory().getId()));
                canAdd = canAdd && !question.getQuestion().isBlank();
                canAdd = canAdd && question.getDifficulty() != null && dto.getMinDifficulty() <= question.getDifficulty();
                canAdd = canAdd && question.getDifficulty() != null && dto.getMaxDifficulty() >= question.getDifficulty();
                if (canAdd) {
                    questions.add(question);
                }
            });
        }

        var gameInfo = new GameInfoDto(UUID.randomUUID().toString(), questions.size());
        cache().put(gameInfo.getId(), new LinkedList<>(questions));
        return gameInfo;
    }

    public Optional<Question> getQuestion(String gameId, Integer number) {
        var list = cache().get(gameId, LinkedList.class);
        if (list == null || list.size() < number) {
            return Optional.empty();
        }
        return Optional.ofNullable((Question) list.get(number - 1));
    }

    public Optional<AnswerCheckDto> checkAnswer(String gameId, Integer number, String answer) {
        return getQuestion(gameId, number).map(question -> {
            var res = AnswerCheckDto.builder()
                    .correctAnswer(question.getAnswer())
                    .isCorrect(question.getAnswer().equals(answer))
                    .build();
            answersCache().put(gameId + number, res);
            return res;
        });
    }

    public List<GameAnswerResultDto> finish(String gameId) {
        @SuppressWarnings("unchecked")
        var list = (LinkedList<Question>) cache().get(gameId, LinkedList.class);
        if (list == null) {
            return Collections.emptyList();
        }

        AtomicInteger number = new AtomicInteger(1);
        return list.stream().map(question -> {
            AnswerCheckDto answer = answersCache().get(gameId + number.getAndIncrement(), AnswerCheckDto.class);
            boolean isCorrect = answer != null && answer.getIsCorrect();
            return new GameAnswerResultDto(question.getQuestion(), isCorrect);
        }).toList();
    }
}
