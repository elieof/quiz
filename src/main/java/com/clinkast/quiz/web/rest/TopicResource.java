package com.clinkast.quiz.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.clinkast.quiz.domain.Topic;
import com.clinkast.quiz.service.TopicService;
import com.clinkast.quiz.web.rest.util.HeaderUtil;
import com.clinkast.quiz.web.rest.util.PaginationUtil;
import com.clinkast.quiz.web.rest.dto.TopicDTO;
import com.clinkast.quiz.web.rest.mapper.TopicMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Topic.
 */
@RestController
@RequestMapping("/api")
public class TopicResource {

    private final Logger log = LoggerFactory.getLogger(TopicResource.class);
        
    @Inject
    private TopicService topicService;
    
    @Inject
    private TopicMapper topicMapper;
    
    /**
     * POST  /topics : Create a new topic.
     *
     * @param topicDTO the topicDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new topicDTO, or with status 400 (Bad Request) if the topic has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/topics",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TopicDTO> createTopic(@Valid @RequestBody TopicDTO topicDTO) throws URISyntaxException {
        log.debug("REST request to save Topic : {}", topicDTO);
        if (topicDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("topic", "idexists", "A new topic cannot already have an ID")).body(null);
        }
        TopicDTO result = topicService.save(topicDTO);
        return ResponseEntity.created(new URI("/api/topics/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("topic", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /topics : Updates an existing topic.
     *
     * @param topicDTO the topicDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated topicDTO,
     * or with status 400 (Bad Request) if the topicDTO is not valid,
     * or with status 500 (Internal Server Error) if the topicDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/topics",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TopicDTO> updateTopic(@Valid @RequestBody TopicDTO topicDTO) throws URISyntaxException {
        log.debug("REST request to update Topic : {}", topicDTO);
        if (topicDTO.getId() == null) {
            return createTopic(topicDTO);
        }
        TopicDTO result = topicService.save(topicDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("topic", topicDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /topics : get all the topics.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of topics in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/topics",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<TopicDTO>> getAllTopics(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Topics");
        Page<Topic> page = topicService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/topics");
        return new ResponseEntity<>(topicMapper.topicsToTopicDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /topics/:id : get the "id" topic.
     *
     * @param id the id of the topicDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the topicDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/topics/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TopicDTO> getTopic(@PathVariable Long id) {
        log.debug("REST request to get Topic : {}", id);
        TopicDTO topicDTO = topicService.findOne(id);
        return Optional.ofNullable(topicDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /topics/:id : delete the "id" topic.
     *
     * @param id the id of the topicDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/topics/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        log.debug("REST request to delete Topic : {}", id);
        topicService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("topic", id.toString())).build();
    }

    /**
     * SEARCH  /_search/topics?query=:query : search for the topic corresponding
     * to the query.
     *
     * @param query the query of the topic search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/topics",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<TopicDTO>> searchTopics(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Topics for query {}", query);
        Page<Topic> page = topicService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/topics");
        return new ResponseEntity<>(topicMapper.topicsToTopicDTOs(page.getContent()), headers, HttpStatus.OK);
    }

}
