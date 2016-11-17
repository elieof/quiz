package com.clinkast.quiz.service.impl;

import com.clinkast.quiz.service.PropositionService;
import com.clinkast.quiz.domain.Proposition;
import com.clinkast.quiz.repository.PropositionRepository;
import com.clinkast.quiz.repository.search.PropositionSearchRepository;
import com.clinkast.quiz.service.dto.PropositionDTO;
import com.clinkast.quiz.service.mapper.PropositionMapper;
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
 * Service Implementation for managing Proposition.
 */
@Service
@Transactional
public class PropositionServiceImpl implements PropositionService{

    private final Logger log = LoggerFactory.getLogger(PropositionServiceImpl.class);
    
    @Inject
    private PropositionRepository propositionRepository;

    @Inject
    private PropositionMapper propositionMapper;

    @Inject
    private PropositionSearchRepository propositionSearchRepository;

    /**
     * Save a proposition.
     *
     * @param propositionDTO the entity to save
     * @return the persisted entity
     */
    public PropositionDTO save(PropositionDTO propositionDTO) {
        log.debug("Request to save Proposition : {}", propositionDTO);
        Proposition proposition = propositionMapper.propositionDTOToProposition(propositionDTO);
        proposition = propositionRepository.save(proposition);
        PropositionDTO result = propositionMapper.propositionToPropositionDTO(proposition);
        propositionSearchRepository.save(proposition);
        return result;
    }

    /**
     *  Get all the propositions.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<PropositionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Propositions");
        Page<Proposition> result = propositionRepository.findAll(pageable);
        return result.map(proposition -> propositionMapper.propositionToPropositionDTO(proposition));
    }

    /**
     *  Get one proposition by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public PropositionDTO findOne(Long id) {
        log.debug("Request to get Proposition : {}", id);
        Proposition proposition = propositionRepository.findOne(id);
        PropositionDTO propositionDTO = propositionMapper.propositionToPropositionDTO(proposition);
        return propositionDTO;
    }

    /**
     *  Delete the  proposition by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Proposition : {}", id);
        propositionRepository.delete(id);
        propositionSearchRepository.delete(id);
    }

    /**
     * Search for the proposition corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PropositionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Propositions for query {}", query);
        Page<Proposition> result = propositionSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(proposition -> propositionMapper.propositionToPropositionDTO(proposition));
    }
}
