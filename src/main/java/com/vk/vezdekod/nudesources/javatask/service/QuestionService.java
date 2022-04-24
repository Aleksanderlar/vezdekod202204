package com.vk.vezdekod.nudesources.javatask.service;

import com.vk.vezdekod.nudesources.javatask.entity.Category;
import com.vk.vezdekod.nudesources.javatask.entity.Question;
import com.vk.vezdekod.nudesources.javatask.repository.QuestionRepository;
import com.vk.vezdekod.nudesources.javatask.service.dto.ExQuestionDto;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final RestTemplate restTemplate;
    private final CacheManager cacheManager;

    public QuestionService(QuestionRepository questionRepository, RestTemplate restTemplate, CacheManager cacheManager) {
        this.questionRepository = questionRepository;
        this.restTemplate = restTemplate;
        this.cacheManager = cacheManager;
    }

    public List<Question> getRandom(Integer count) {
        if (count == null) {
            count = 1;
        }

        if (new Random().nextInt(2) == 0) {
            return getRandomFromExt(count);
        }

        var questions = questionRepository.findRandom(count);
        questions.forEach(question -> cache().put(question.getId().toString(), question));
        return questions;
    }

    private List<Question> getRandomFromExt(Integer count) {
        if (count == null) {
            count = 1;
        }

        ExQuestionDto[] questions;
        try {
            questions = restTemplate.getForObject("http://jservice.io/api/random?count=" + count, ExQuestionDto[].class);
        } catch (Exception e) {
            return Collections.emptyList();
        }

        if (questions == null || questions.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.stream(questions).map(ext -> {
            var question = Question.builder()
                    .id(ext.getId())
                    .question(ext.getQuestion())
                    .answer(ext.getAnswer())
                    .difficulty(ext.getValue())
                    .category(
                            ext.getCategory() != null ? Category.builder()
                                    .id(ext.getCategory().getId())
                                    .name(ext.getCategory().getTitle())
                                    .build() : null
                    ).build();
            cache().put(question.getId().toString(), question);
            return question;
        }).toList();
    }

    public Optional<Question> findById(Long id) {
        var _question = cache().get(id.toString(), Question.class);
        return Optional.ofNullable(_question).or(() -> questionRepository.findById(id).map(question -> {
            cache().put(id.toString(), question);
            return question;
        }));
    }

    private Cache cache() {
        return cacheManager.getCache("questions");
    }
}
