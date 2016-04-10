package com.clinkast.quiz.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.clinkast.quiz.domain.Result;
import com.clinkast.quiz.service.ResultService;
import com.clinkast.quiz.web.rest.util.HeaderUtil;
import com.clinkast.quiz.web.rest.util.PaginationUtil;
import com.clinkast.quiz.web.rest.dto.ResultDTO;
import com.clinkast.quiz.web.rest.mapper.ResultMapper;
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
 * REST controller for managing Result.
 */
@RestController
@RequestMapping("/api")
public class ResultResource {

    private final Logger log = LoggerFactory.getLogger(ResultResource.class);
        
    @Inject
    private ResultService resultService;
    
    @Inject
    private ResultMapper resultMapper;
    
    /**
     * POST  /results : Create a new result.
     *
     * @param resultDTO the resultDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new resultDTO, or with status 400 (Bad Request) if the result has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/results",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResultDTO> createResult(@Valid @RequestBody ResultDTO resultDTO) throws URISyntaxException {
        log.debug("REST request to save Result : {}", resultDTO);
        if (resultDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("result", "idexists", "A new result cannot already have an ID")).body(null);
        }
        ResultDTO result = resultService.save(resultDTO);
        return ResponseEntity.created(new URI("/api/results/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("result", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /results : Updates an existing result.
     *
     * @param resultDTO the resultDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated resultDTO,
     * or with status 400 (Bad Request) if the resultDTO is not valid,
     * or with status 500 (Internal Server Error) if the resultDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/results",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResultDTO> updateResult(@Valid @RequestBody ResultDTO resultDTO) throws URISyntaxException {
        log.debug("REST request to update Result : {}", resultDTO);
        if (resultDTO.getId() == null) {
            return createResult(resultDTO);
        }
        ResultDTO result = resultService.save(resultDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("result", resultDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /results : get all the results.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of results in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/results",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<ResultDTO>> getAllResults(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Results");
        Page<Result> page = resultService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/results");
        return new ResponseEntity<>(resultMapper.resultsToResultDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /results/:id : get the "id" result.
     *
     * @param id the id of the resultDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the resultDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/results/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResultDTO> getResult(@PathVariable Long id) {
        log.debug("REST request to get Result : {}", id);
        ResultDTO resultDTO = resultService.findOne(id);
        return Optional.ofNullable(resultDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /results/:id : delete the "id" result.
     *
     * @param id the id of the resultDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/results/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        log.debug("REST request to delete Result : {}", id);
        resultService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("result", id.toString())).build();
    }

    /**
     * SEARCH  /_search/results?query=:query : search for the result corresponding
     * to the query.
     *
     * @param query the query of the result search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/results",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<ResultDTO>> searchResults(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Results for query {}", query);
        Page<Result> page = resultService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/results");
        return new ResponseEntity<>(resultMapper.resultsToResultDTOs(page.getContent()), headers, HttpStatus.OK);
    }

}
