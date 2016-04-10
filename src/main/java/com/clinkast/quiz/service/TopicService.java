package com.clinkast.quiz.service;

import com.clinkast.quiz.domain.Topic;
import com.clinkast.quiz.web.rest.dto.TopicDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Topic.
 */
public interface TopicService {

    /**
     * Save a topic.
     * 
     * @param topicDTO the entity to save
     * @return the persisted entity
     */
    TopicDTO save(TopicDTO topicDTO);

    /**
     *  Get all the topics.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Topic> findAll(Pageable pageable);

    /**
     *  Get the "id" topic.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    TopicDTO findOne(Long id);

    /**
     *  Delete the "id" topic.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the topic corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Topic> search(String query, Pageable pageable);
}
