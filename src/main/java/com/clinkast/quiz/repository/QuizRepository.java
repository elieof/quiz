package com.clinkast.quiz.repository;

import com.clinkast.quiz.domain.Quiz;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Quiz entity.
 */
@SuppressWarnings("unused")
public interface QuizRepository extends JpaRepository<Quiz,Long> {

}
