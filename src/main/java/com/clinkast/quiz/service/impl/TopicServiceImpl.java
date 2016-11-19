package com.clinkast.quiz.service.impl;

import com.clinkast.quiz.service.TopicService;
import com.clinkast.quiz.domain.Topic;
import com.clinkast.quiz.repository.TopicRepository;
import com.clinkast.quiz.repository.search.TopicSearchRepository;
import com.clinkast.quiz.web.rest.dto.TopicDTO;
import com.clinkast.quiz.web.rest.mapper.TopicMapper;
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
 * Service Implementation for managing Topic.
 */
@Service
@Transactional
public class TopicServiceImpl implements TopicService{

    private final Logger log = LoggerFactory.getLogger(TopicServiceImpl.class);
    
    @Inject
    private TopicRepository topicRepository;
    
    @Inject
    private TopicMapper topicMapper;
    
    @Inject
    private TopicSearchRepository topicSearchRepository;
    
    /**
     * Save a topic.
     * 
     * @param topicDTO the entity to save
     * @return the persisted entity
     */
    public TopicDTO save(TopicDTO topicDTO) {
        log.debug("Request to save Topic : {}", topicDTO);
        Topic topic = topicMapper.topicDTOToTopic(topicDTO);
        topic = topicRepository.save(topic);
        TopicDTO result = topicMapper.topicToTopicDTO(topic);
        topicSearchRepository.save(topic);
        return result;
    }

    /**
     *  Get all the topics.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Topic> findAll(Pageable pageable) {
        log.debug("Request to get all Topics");
        Page<Topic> result = topicRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one topic by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public TopicDTO findOne(Long id) {
        log.debug("Request to get Topic : {}", id);
        Topic topic = topicRepository.findOne(id);
        TopicDTO topicDTO = topicMapper.topicToTopicDTO(topic);
        return topicDTO;
    }

    /**
     *  Delete the  topic by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Topic : {}", id);
        topicRepository.delete(id);
        topicSearchRepository.delete(id);
    }

    /**
     * Search for the topic corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Topic> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Topics for query {}", query);
        return topicSearchRepository.search(queryStringQuery(query), pageable);
    }
}
