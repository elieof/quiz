package com.clinkast.quiz.service.impl;

import com.clinkast.quiz.service.ResultService;
import com.clinkast.quiz.domain.Result;
import com.clinkast.quiz.repository.ResultRepository;
import com.clinkast.quiz.repository.search.ResultSearchRepository;
import com.clinkast.quiz.web.rest.dto.ResultDTO;
import com.clinkast.quiz.web.rest.mapper.ResultMapper;
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
 * Service Implementation for managing Result.
 */
@Service
@Transactional
public class ResultServiceImpl implements ResultService{

    private final Logger log = LoggerFactory.getLogger(ResultServiceImpl.class);
    
    @Inject
    private ResultRepository resultRepository;
    
    @Inject
    private ResultMapper resultMapper;
    
    @Inject
    private ResultSearchRepository resultSearchRepository;
    
    /**
     * Save a result.
     * 
     * @param resultDTO the entity to save
     * @return the persisted entity
     */
    public ResultDTO save(ResultDTO resultDTO) {
        log.debug("Request to save Result : {}", resultDTO);
        Result result = resultMapper.resultDTOToResult(resultDTO);
        result = resultRepository.save(result);
        ResultDTO result = resultMapper.resultToResultDTO(result);
        resultSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the results.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Result> findAll(Pageable pageable) {
        log.debug("Request to get all Results");
        Page<Result> result = resultRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one result by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ResultDTO findOne(Long id) {
        log.debug("Request to get Result : {}", id);
        Result result = resultRepository.findOne(id);
        ResultDTO resultDTO = resultMapper.resultToResultDTO(result);
        return resultDTO;
    }

    /**
     *  Delete the  result by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Result : {}", id);
        resultRepository.delete(id);
        resultSearchRepository.delete(id);
    }

    /**
     * Search for the result corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Result> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Results for query {}", query);
        return resultSearchRepository.search(queryStringQuery(query), pageable);
    }
}
