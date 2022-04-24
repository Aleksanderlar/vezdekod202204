package com.vk.vezdekod.nudesources.javatask.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @JsonIgnore
    @Column(nullable = false)
    private String answer;

    @ManyToOne
    @JoinColumn
    private Category category;

    @Column(nullable = false)
    private Integer difficulty;
}
