package com.clinkast.quiz.service;

import com.clinkast.quiz.domain.Question;
import com.clinkast.quiz.web.rest.dto.QuestionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Question.
 */
public interface QuestionService {

    /**
     * Save a question.
     * 
     * @param questionDTO the entity to save
     * @return the persisted entity
     */
    QuestionDTO save(QuestionDTO questionDTO);

    /**
     *  Get all the questions.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Question> findAll(Pageable pageable);

    /**
     *  Get the "id" question.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    QuestionDTO findOne(Long id);

    /**
     *  Delete the "id" question.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the question corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Question> search(String query, Pageable pageable);
}
