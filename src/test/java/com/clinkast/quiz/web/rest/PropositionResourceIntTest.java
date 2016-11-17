package com.clinkast.quiz.web.rest;

import com.clinkast.quiz.QuizApp;

import com.clinkast.quiz.domain.Proposition;
import com.clinkast.quiz.repository.PropositionRepository;
import com.clinkast.quiz.service.PropositionService;
import com.clinkast.quiz.repository.search.PropositionSearchRepository;
import com.clinkast.quiz.service.dto.PropositionDTO;
import com.clinkast.quiz.service.mapper.PropositionMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PropositionResource REST controller.
 *
 * @see PropositionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = QuizApp.class)
public class PropositionResourceIntTest {

    private static final String DEFAULT_STATEMENT = "AAAAAAAAAA";
    private static final String UPDATED_STATEMENT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_VALID = false;
    private static final Boolean UPDATED_VALID = true;

    private static final String DEFAULT_EXPLANATION = "AAAAAAAAAA";
    private static final String UPDATED_EXPLANATION = "BBBBBBBBBB";

    @Inject
    private PropositionRepository propositionRepository;

    @Inject
    private PropositionMapper propositionMapper;

    @Inject
    private PropositionService propositionService;

    @Inject
    private PropositionSearchRepository propositionSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPropositionMockMvc;

    private Proposition proposition;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PropositionResource propositionResource = new PropositionResource();
        ReflectionTestUtils.setField(propositionResource, "propositionService", propositionService);
        this.restPropositionMockMvc = MockMvcBuilders.standaloneSetup(propositionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Proposition createEntity(EntityManager em) {
        Proposition proposition = new Proposition();
        proposition.setStatement(DEFAULT_STATEMENT);
        proposition.setValid(DEFAULT_VALID);
        proposition.setExplanation(DEFAULT_EXPLANATION);
        return proposition;
    }

    @Before
    public void initTest() {
        propositionSearchRepository.deleteAll();
        proposition = createEntity(em);
    }

    @Test
    @Transactional
    public void createProposition() throws Exception {
        int databaseSizeBeforeCreate = propositionRepository.findAll().size();

        // Create the Proposition
        PropositionDTO propositionDTO = propositionMapper.propositionToPropositionDTO(proposition);

        restPropositionMockMvc.perform(post("/api/propositions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(propositionDTO)))
                .andExpect(status().isCreated());

        // Validate the Proposition in the database
        List<Proposition> propositions = propositionRepository.findAll();
        assertThat(propositions).hasSize(databaseSizeBeforeCreate + 1);
        Proposition testProposition = propositions.get(propositions.size() - 1);
        assertThat(testProposition.getStatement()).isEqualTo(DEFAULT_STATEMENT);
        assertThat(testProposition.isValid()).isEqualTo(DEFAULT_VALID);
        assertThat(testProposition.getExplanation()).isEqualTo(DEFAULT_EXPLANATION);

        // Validate the Proposition in ElasticSearch
        Proposition propositionEs = propositionSearchRepository.findOne(testProposition.getId());
        assertThat(propositionEs).isEqualToComparingFieldByField(testProposition);
    }

    @Test
    @Transactional
    public void checkStatementIsRequired() throws Exception {
        int databaseSizeBeforeTest = propositionRepository.findAll().size();
        // set the field null
        proposition.setStatement(null);

        // Create the Proposition, which fails.
        PropositionDTO propositionDTO = propositionMapper.propositionToPropositionDTO(proposition);

        restPropositionMockMvc.perform(post("/api/propositions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(propositionDTO)))
                .andExpect(status().isBadRequest());

        List<Proposition> propositions = propositionRepository.findAll();
        assertThat(propositions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValidIsRequired() throws Exception {
        int databaseSizeBeforeTest = propositionRepository.findAll().size();
        // set the field null
        proposition.setValid(null);

        // Create the Proposition, which fails.
        PropositionDTO propositionDTO = propositionMapper.propositionToPropositionDTO(proposition);

        restPropositionMockMvc.perform(post("/api/propositions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(propositionDTO)))
                .andExpect(status().isBadRequest());

        List<Proposition> propositions = propositionRepository.findAll();
        assertThat(propositions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPropositions() throws Exception {
        // Initialize the database
        propositionRepository.saveAndFlush(proposition);

        // Get all the propositions
        restPropositionMockMvc.perform(get("/api/propositions?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(proposition.getId().intValue())))
                .andExpect(jsonPath("$.[*].statement").value(hasItem(DEFAULT_STATEMENT.toString())))
                .andExpect(jsonPath("$.[*].valid").value(hasItem(DEFAULT_VALID.booleanValue())))
                .andExpect(jsonPath("$.[*].explanation").value(hasItem(DEFAULT_EXPLANATION.toString())));
    }

    @Test
    @Transactional
    public void getProposition() throws Exception {
        // Initialize the database
        propositionRepository.saveAndFlush(proposition);

        // Get the proposition
        restPropositionMockMvc.perform(get("/api/propositions/{id}", proposition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(proposition.getId().intValue()))
            .andExpect(jsonPath("$.statement").value(DEFAULT_STATEMENT.toString()))
            .andExpect(jsonPath("$.valid").value(DEFAULT_VALID.booleanValue()))
            .andExpect(jsonPath("$.explanation").value(DEFAULT_EXPLANATION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProposition() throws Exception {
        // Get the proposition
        restPropositionMockMvc.perform(get("/api/propositions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProposition() throws Exception {
        // Initialize the database
        propositionRepository.saveAndFlush(proposition);
        propositionSearchRepository.save(proposition);
        int databaseSizeBeforeUpdate = propositionRepository.findAll().size();

        // Update the proposition
        Proposition updatedProposition = propositionRepository.findOne(proposition.getId());
        updatedProposition.setStatement(UPDATED_STATEMENT);
        updatedProposition.setValid(UPDATED_VALID);
        updatedProposition.setExplanation(UPDATED_EXPLANATION);
        PropositionDTO propositionDTO = propositionMapper.propositionToPropositionDTO(updatedProposition);

        restPropositionMockMvc.perform(put("/api/propositions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(propositionDTO)))
                .andExpect(status().isOk());

        // Validate the Proposition in the database
        List<Proposition> propositions = propositionRepository.findAll();
        assertThat(propositions).hasSize(databaseSizeBeforeUpdate);
        Proposition testProposition = propositions.get(propositions.size() - 1);
        assertThat(testProposition.getStatement()).isEqualTo(UPDATED_STATEMENT);
        assertThat(testProposition.isValid()).isEqualTo(UPDATED_VALID);
        assertThat(testProposition.getExplanation()).isEqualTo(UPDATED_EXPLANATION);

        // Validate the Proposition in ElasticSearch
        Proposition propositionEs = propositionSearchRepository.findOne(testProposition.getId());
        assertThat(propositionEs).isEqualToComparingFieldByField(testProposition);
    }

    @Test
    @Transactional
    public void deleteProposition() throws Exception {
        // Initialize the database
        propositionRepository.saveAndFlush(proposition);
        propositionSearchRepository.save(proposition);
        int databaseSizeBeforeDelete = propositionRepository.findAll().size();

        // Get the proposition
        restPropositionMockMvc.perform(delete("/api/propositions/{id}", proposition.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean propositionExistsInEs = propositionSearchRepository.exists(proposition.getId());
        assertThat(propositionExistsInEs).isFalse();

        // Validate the database is empty
        List<Proposition> propositions = propositionRepository.findAll();
        assertThat(propositions).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProposition() throws Exception {
        // Initialize the database
        propositionRepository.saveAndFlush(proposition);
        propositionSearchRepository.save(proposition);

        // Search the proposition
        restPropositionMockMvc.perform(get("/api/_search/propositions?query=id:" + proposition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(proposition.getId().intValue())))
            .andExpect(jsonPath("$.[*].statement").value(hasItem(DEFAULT_STATEMENT.toString())))
            .andExpect(jsonPath("$.[*].valid").value(hasItem(DEFAULT_VALID.booleanValue())))
            .andExpect(jsonPath("$.[*].explanation").value(hasItem(DEFAULT_EXPLANATION.toString())));
    }
}
