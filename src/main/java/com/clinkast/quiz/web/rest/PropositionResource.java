package com.clinkast.quiz.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.clinkast.quiz.service.PropositionService;
import com.clinkast.quiz.web.rest.util.HeaderUtil;
import com.clinkast.quiz.web.rest.util.PaginationUtil;
import com.clinkast.quiz.service.dto.PropositionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * REST controller for managing Proposition.
 */
@RestController
@RequestMapping("/api")
public class PropositionResource {

    private final Logger log = LoggerFactory.getLogger(PropositionResource.class);
        
    @Inject
    private PropositionService propositionService;

    /**
     * POST  /propositions : Create a new proposition.
     *
     * @param propositionDTO the propositionDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new propositionDTO, or with status 400 (Bad Request) if the proposition has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/propositions")
    @Timed
    public ResponseEntity<PropositionDTO> createProposition(@Valid @RequestBody PropositionDTO propositionDTO) throws URISyntaxException {
        log.debug("REST request to save Proposition : {}", propositionDTO);
        if (propositionDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("proposition", "idexists", "A new proposition cannot already have an ID")).body(null);
        }
        PropositionDTO result = propositionService.save(propositionDTO);
        return ResponseEntity.created(new URI("/api/propositions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("proposition", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /propositions : Updates an existing proposition.
     *
     * @param propositionDTO the propositionDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated propositionDTO,
     * or with status 400 (Bad Request) if the propositionDTO is not valid,
     * or with status 500 (Internal Server Error) if the propositionDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/propositions")
    @Timed
    public ResponseEntity<PropositionDTO> updateProposition(@Valid @RequestBody PropositionDTO propositionDTO) throws URISyntaxException {
        log.debug("REST request to update Proposition : {}", propositionDTO);
        if (propositionDTO.getId() == null) {
            return createProposition(propositionDTO);
        }
        PropositionDTO result = propositionService.save(propositionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("proposition", propositionDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /propositions : get all the propositions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of propositions in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/propositions")
    @Timed
    public ResponseEntity<List<PropositionDTO>> getAllPropositions(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Propositions");
        Page<PropositionDTO> page = propositionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/propositions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /propositions/:id : get the "id" proposition.
     *
     * @param id the id of the propositionDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the propositionDTO, or with status 404 (Not Found)
     */
    @GetMapping("/propositions/{id}")
    @Timed
    public ResponseEntity<PropositionDTO> getProposition(@PathVariable Long id) {
        log.debug("REST request to get Proposition : {}", id);
        PropositionDTO propositionDTO = propositionService.findOne(id);
        return Optional.ofNullable(propositionDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /propositions/:id : delete the "id" proposition.
     *
     * @param id the id of the propositionDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/propositions/{id}")
    @Timed
    public ResponseEntity<Void> deleteProposition(@PathVariable Long id) {
        log.debug("REST request to delete Proposition : {}", id);
        propositionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("proposition", id.toString())).build();
    }

    /**
     * SEARCH  /_search/propositions?query=:query : search for the proposition corresponding
     * to the query.
     *
     * @param query the query of the proposition search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/propositions")
    @Timed
    public ResponseEntity<List<PropositionDTO>> searchPropositions(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Propositions for query {}", query);
        Page<PropositionDTO> page = propositionService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/propositions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
