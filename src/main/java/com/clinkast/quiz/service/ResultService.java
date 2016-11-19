package com.clinkast.quiz.service;

import com.clinkast.quiz.domain.Result;
import com.clinkast.quiz.web.rest.dto.ResultDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Result.
 */
public interface ResultService {

    /**
     * Save a result.
     * 
     * @param resultDTO the entity to save
     * @return the persisted entity
     */
    ResultDTO save(ResultDTO resultDTO);

    /**
     *  Get all the results.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Result> findAll(Pageable pageable);

    /**
     *  Get the "id" result.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    ResultDTO findOne(Long id);

    /**
     *  Delete the "id" result.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the result corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Result> search(String query, Pageable pageable);
}
