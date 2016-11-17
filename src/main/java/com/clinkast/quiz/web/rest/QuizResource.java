package com.clinkast.quiz.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.clinkast.quiz.service.QuizService;
import com.clinkast.quiz.web.rest.util.HeaderUtil;
import com.clinkast.quiz.web.rest.util.PaginationUtil;
import com.clinkast.quiz.service.dto.QuizDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Quiz.
 */
@RestController
@RequestMapping("/api")
public class QuizResource {

    private final Logger log = LoggerFactory.getLogger(QuizResource.class);
        
    @Inject
    private QuizService quizService;

    /**
     * POST  /quizzes : Create a new quiz.
     *
     * @param quizDTO the quizDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new quizDTO, or with status 400 (Bad Request) if the quiz has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/quizzes")
    @Timed
    public ResponseEntity<QuizDTO> createQuiz(@RequestBody QuizDTO quizDTO) throws URISyntaxException {
        log.debug("REST request to save Quiz : {}", quizDTO);
        if (quizDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("quiz", "idexists", "A new quiz cannot already have an ID")).body(null);
        }
        QuizDTO result = quizService.save(quizDTO);
        return ResponseEntity.created(new URI("/api/quizzes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("quiz", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /quizzes : Updates an existing quiz.
     *
     * @param quizDTO the quizDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated quizDTO,
     * or with status 400 (Bad Request) if the quizDTO is not valid,
     * or with status 500 (Internal Server Error) if the quizDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/quizzes")
    @Timed
    public ResponseEntity<QuizDTO> updateQuiz(@RequestBody QuizDTO quizDTO) throws URISyntaxException {
        log.debug("REST request to update Quiz : {}", quizDTO);
        if (quizDTO.getId() == null) {
            return createQuiz(quizDTO);
        }
        QuizDTO result = quizService.save(quizDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("quiz", quizDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /quizzes : get all the quizzes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of quizzes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/quizzes")
    @Timed
    public ResponseEntity<List<QuizDTO>> getAllQuizzes(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Quizzes");
        Page<QuizDTO> page = quizService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/quizzes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /quizzes/:id : get the "id" quiz.
     *
     * @param id the id of the quizDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the quizDTO, or with status 404 (Not Found)
     */
    @GetMapping("/quizzes/{id}")
    @Timed
    public ResponseEntity<QuizDTO> getQuiz(@PathVariable Long id) {
        log.debug("REST request to get Quiz : {}", id);
        QuizDTO quizDTO = quizService.findOne(id);
        return Optional.ofNullable(quizDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /quizzes/:id : delete the "id" quiz.
     *
     * @param id the id of the quizDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/quizzes/{id}")
    @Timed
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        log.debug("REST request to delete Quiz : {}", id);
        quizService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("quiz", id.toString())).build();
    }

    /**
     * SEARCH  /_search/quizzes?query=:query : search for the quiz corresponding
     * to the query.
     *
     * @param query the query of the quiz search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/quizzes")
    @Timed
    public ResponseEntity<List<QuizDTO>> searchQuizzes(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Quizzes for query {}", query);
        Page<QuizDTO> page = quizService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/quizzes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
