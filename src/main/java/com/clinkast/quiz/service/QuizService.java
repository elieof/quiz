package com.clinkast.quiz.service;

import com.clinkast.quiz.domain.Quiz;
import com.clinkast.quiz.web.rest.dto.QuizDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Quiz.
 */
public interface QuizService {

    /**
     * Save a quiz.
     * 
     * @param quizDTO the entity to save
     * @return the persisted entity
     */
    QuizDTO save(QuizDTO quizDTO);

    /**
     *  Get all the quizzes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Quiz> findAll(Pageable pageable);

    /**
     *  Get the "id" quiz.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    QuizDTO findOne(Long id);

    /**
     *  Delete the "id" quiz.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the quiz corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Quiz> search(String query, Pageable pageable);
}
