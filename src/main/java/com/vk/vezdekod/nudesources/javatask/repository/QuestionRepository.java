package com.vk.vezdekod.nudesources.javatask.repository;

import com.vk.vezdekod.nudesources.javatask.entity.Question;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface QuestionRepository extends PagingAndSortingRepository<Question, Long> {
    @Query(nativeQuery = true, value = "SELECT *  FROM question ORDER BY random() LIMIT :count")
    List<Question> findRandom(Integer count);
}
