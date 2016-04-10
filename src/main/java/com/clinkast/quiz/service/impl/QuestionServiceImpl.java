package com.clinkast.quiz.service.impl;

import com.clinkast.quiz.service.QuestionService;
import com.clinkast.quiz.domain.Question;
import com.clinkast.quiz.repository.QuestionRepository;
import com.clinkast.quiz.repository.search.QuestionSearchRepository;
import com.clinkast.quiz.web.rest.dto.QuestionDTO;
import com.clinkast.quiz.web.rest.mapper.QuestionMapper;
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
 * Service Implementation for managing Question.
 */
@Service
@Transactional
public class QuestionServiceImpl implements QuestionService{

    private final Logger log = LoggerFactory.getLogger(QuestionServiceImpl.class);
    
    @Inject
    private QuestionRepository questionRepository;
    
    @Inject
    private QuestionMapper questionMapper;
    
    @Inject
    private QuestionSearchRepository questionSearchRepository;
    
    /**
     * Save a question.
     * 
     * @param questionDTO the entity to save
     * @return the persisted entity
     */
    public QuestionDTO save(QuestionDTO questionDTO) {
        log.debug("Request to save Question : {}", questionDTO);
        Question question = questionMapper.questionDTOToQuestion(questionDTO);
        question = questionRepository.save(question);
        QuestionDTO result = questionMapper.questionToQuestionDTO(question);
        questionSearchRepository.save(question);
        return result;
    }

    /**
     *  Get all the questions.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Question> findAll(Pageable pageable) {
        log.debug("Request to get all Questions");
        Page<Question> result = questionRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one question by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public QuestionDTO findOne(Long id) {
        log.debug("Request to get Question : {}", id);
        Question question = questionRepository.findOne(id);
        QuestionDTO questionDTO = questionMapper.questionToQuestionDTO(question);
        return questionDTO;
    }

    /**
     *  Delete the  question by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Question : {}", id);
        questionRepository.delete(id);
        questionSearchRepository.delete(id);
    }

    /**
     * Search for the question corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Question> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Questions for query {}", query);
        return questionSearchRepository.search(queryStringQuery(query), pageable);
    }
}
