package com.clinkast.quiz.service;

import com.clinkast.quiz.service.dto.PropositionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Proposition.
 */
public interface PropositionService {

    /**
     * Save a proposition.
     *
     * @param propositionDTO the entity to save
     * @return the persisted entity
     */
    PropositionDTO save(PropositionDTO propositionDTO);

    /**
     *  Get all the propositions.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<PropositionDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" proposition.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    PropositionDTO findOne(Long id);

    /**
     *  Delete the "id" proposition.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the proposition corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<PropositionDTO> search(String query, Pageable pageable);
}
