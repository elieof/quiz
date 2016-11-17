package com.clinkast.quiz.service.impl;

import com.clinkast.quiz.service.QuizService;
import com.clinkast.quiz.domain.Quiz;
import com.clinkast.quiz.repository.QuizRepository;
import com.clinkast.quiz.repository.search.QuizSearchRepository;
import com.clinkast.quiz.service.dto.QuizDTO;
import com.clinkast.quiz.service.mapper.QuizMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Quiz.
 */
@Service
@Transactional
public class QuizServiceImpl implements QuizService{

    private final Logger log = LoggerFactory.getLogger(QuizServiceImpl.class);
    
    @Inject
    private QuizRepository quizRepository;

    @Inject
    private QuizMapper quizMapper;

    @Inject
    private QuizSearchRepository quizSearchRepository;

    /**
     * Save a quiz.
     *
     * @param quizDTO the entity to save
     * @return the persisted entity
     */
    public QuizDTO save(QuizDTO quizDTO) {
        log.debug("Request to save Quiz : {}", quizDTO);
        Quiz quiz = quizMapper.quizDTOToQuiz(quizDTO);
        quiz = quizRepository.save(quiz);
        QuizDTO result = quizMapper.quizToQuizDTO(quiz);
        quizSearchRepository.save(quiz);
        return result;
    }

    /**
     *  Get all the quizzes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<QuizDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Quizzes");
        Page<Quiz> result = quizRepository.findAll(pageable);
        return result.map(quiz -> quizMapper.quizToQuizDTO(quiz));
    }

    /**
     *  Get one quiz by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public QuizDTO findOne(Long id) {
        log.debug("Request to get Quiz : {}", id);
        Quiz quiz = quizRepository.findOne(id);
        QuizDTO quizDTO = quizMapper.quizToQuizDTO(quiz);
        return quizDTO;
    }

    /**
     *  Delete the  quiz by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Quiz : {}", id);
        quizRepository.delete(id);
        quizSearchRepository.delete(id);
    }

    /**
     * Search for the quiz corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<QuizDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Quizzes for query {}", query);
        Page<Quiz> result = quizSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(quiz -> quizMapper.quizToQuizDTO(quiz));
    }
}
