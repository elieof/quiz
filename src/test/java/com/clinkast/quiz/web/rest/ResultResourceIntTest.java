package com.clinkast.quiz.web.rest;

import com.clinkast.quiz.QuizApp;
import com.clinkast.quiz.domain.Result;
import com.clinkast.quiz.repository.ResultRepository;
import com.clinkast.quiz.service.ResultService;
import com.clinkast.quiz.repository.search.ResultSearchRepository;
import com.clinkast.quiz.web.rest.dto.ResultDTO;
import com.clinkast.quiz.web.rest.mapper.ResultMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ResultResource REST controller.
 *
 * @see ResultResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = QuizApp.class)
@WebAppConfiguration
@IntegrationTest
public class ResultResourceIntTest {


    private static final Boolean DEFAULT_VALID = false;
    private static final Boolean UPDATED_VALID = true;

    @Inject
    private ResultRepository resultRepository;

    @Inject
    private ResultMapper resultMapper;

    @Inject
    private ResultService resultService;

    @Inject
    private ResultSearchRepository resultSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restResultMockMvc;

    private Result result;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ResultResource resultResource = new ResultResource();
        ReflectionTestUtils.setField(resultResource, "resultService", resultService);
        ReflectionTestUtils.setField(resultResource, "resultMapper", resultMapper);
        this.restResultMockMvc = MockMvcBuilders.standaloneSetup(resultResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        resultSearchRepository.deleteAll();
        result = new Result();
        result.setValid(DEFAULT_VALID);
    }

    @Test
    @Transactional
    public void createResult() throws Exception {
        int databaseSizeBeforeCreate = resultRepository.findAll().size();

        // Create the Result
        ResultDTO resultDTO = resultMapper.resultToResultDTO(result);

        restResultMockMvc.perform(post("/api/results")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resultDTO)))
                .andExpect(status().isCreated());

        // Validate the Result in the database
        List<Result> results = resultRepository.findAll();
        assertThat(results).hasSize(databaseSizeBeforeCreate + 1);
        Result testResult = results.get(results.size() - 1);
        assertThat(testResult.isValid()).isEqualTo(DEFAULT_VALID);

        // Validate the Result in ElasticSearch
        Result resultEs = resultSearchRepository.findOne(testResult.getId());
        assertThat(resultEs).isEqualToComparingFieldByField(testResult);
    }

    @Test
    @Transactional
    public void checkValidIsRequired() throws Exception {
        int databaseSizeBeforeTest = resultRepository.findAll().size();
        // set the field null
        result.setValid(null);

        // Create the Result, which fails.
        ResultDTO resultDTO = resultMapper.resultToResultDTO(result);

        restResultMockMvc.perform(post("/api/results")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resultDTO)))
                .andExpect(status().isBadRequest());

        List<Result> results = resultRepository.findAll();
        assertThat(results).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllResults() throws Exception {
        // Initialize the database
        resultRepository.saveAndFlush(result);

        // Get all the results
        restResultMockMvc.perform(get("/api/results?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(result.getId().intValue())))
                .andExpect(jsonPath("$.[*].valid").value(hasItem(DEFAULT_VALID.booleanValue())));
    }

    @Test
    @Transactional
    public void getResult() throws Exception {
        // Initialize the database
        resultRepository.saveAndFlush(result);

        // Get the result
        restResultMockMvc.perform(get("/api/results/{id}", result.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(result.getId().intValue()))
            .andExpect(jsonPath("$.valid").value(DEFAULT_VALID.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingResult() throws Exception {
        // Get the result
        restResultMockMvc.perform(get("/api/results/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateResult() throws Exception {
        // Initialize the database
        resultRepository.saveAndFlush(result);
        resultSearchRepository.save(result);
        int databaseSizeBeforeUpdate = resultRepository.findAll().size();

        // Update the result
        Result updatedResult = new Result();
        updatedResult.setId(result.getId());
        updatedResult.setValid(UPDATED_VALID);
        ResultDTO resultDTO = resultMapper.resultToResultDTO(updatedResult);

        restResultMockMvc.perform(put("/api/results")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resultDTO)))
                .andExpect(status().isOk());

        // Validate the Result in the database
        List<Result> results = resultRepository.findAll();
        assertThat(results).hasSize(databaseSizeBeforeUpdate);
        Result testResult = results.get(results.size() - 1);
        assertThat(testResult.isValid()).isEqualTo(UPDATED_VALID);

        // Validate the Result in ElasticSearch
        Result resultEs = resultSearchRepository.findOne(testResult.getId());
        assertThat(resultEs).isEqualToComparingFieldByField(testResult);
    }

    @Test
    @Transactional
    public void deleteResult() throws Exception {
        // Initialize the database
        resultRepository.saveAndFlush(result);
        resultSearchRepository.save(result);
        int databaseSizeBeforeDelete = resultRepository.findAll().size();

        // Get the result
        restResultMockMvc.perform(delete("/api/results/{id}", result.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean resultExistsInEs = resultSearchRepository.exists(result.getId());
        assertThat(resultExistsInEs).isFalse();

        // Validate the database is empty
        List<Result> results = resultRepository.findAll();
        assertThat(results).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchResult() throws Exception {
        // Initialize the database
        resultRepository.saveAndFlush(result);
        resultSearchRepository.save(result);

        // Search the result
        restResultMockMvc.perform(get("/api/_search/results?query=id:" + result.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(result.getId().intValue())))
            .andExpect(jsonPath("$.[*].valid").value(hasItem(DEFAULT_VALID.booleanValue())));
    }
}
