package com.clinkast.quiz.web.rest;

import com.clinkast.quiz.QuizApp;

import com.clinkast.quiz.domain.Quiz;
import com.clinkast.quiz.repository.QuizRepository;
import com.clinkast.quiz.service.QuizService;
import com.clinkast.quiz.repository.search.QuizSearchRepository;
import com.clinkast.quiz.service.dto.QuizDTO;
import com.clinkast.quiz.service.mapper.QuizMapper;

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
 * Test class for the QuizResource REST controller.
 *
 * @see QuizResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = QuizApp.class)
public class QuizResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Inject
    private QuizRepository quizRepository;

    @Inject
    private QuizMapper quizMapper;

    @Inject
    private QuizService quizService;

    @Inject
    private QuizSearchRepository quizSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restQuizMockMvc;

    private Quiz quiz;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        QuizResource quizResource = new QuizResource();
        ReflectionTestUtils.setField(quizResource, "quizService", quizService);
        this.restQuizMockMvc = MockMvcBuilders.standaloneSetup(quizResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quiz createEntity(EntityManager em) {
        Quiz quiz = new Quiz();
        quiz.setName(DEFAULT_NAME);
        quiz.setDescription(DEFAULT_DESCRIPTION);
        return quiz;
    }

    @Before
    public void initTest() {
        quizSearchRepository.deleteAll();
        quiz = createEntity(em);
    }

    @Test
    @Transactional
    public void createQuiz() throws Exception {
        int databaseSizeBeforeCreate = quizRepository.findAll().size();

        // Create the Quiz
        QuizDTO quizDTO = quizMapper.quizToQuizDTO(quiz);

        restQuizMockMvc.perform(post("/api/quizzes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(quizDTO)))
                .andExpect(status().isCreated());

        // Validate the Quiz in the database
        List<Quiz> quizzes = quizRepository.findAll();
        assertThat(quizzes).hasSize(databaseSizeBeforeCreate + 1);
        Quiz testQuiz = quizzes.get(quizzes.size() - 1);
        assertThat(testQuiz.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testQuiz.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Quiz in ElasticSearch
        Quiz quizEs = quizSearchRepository.findOne(testQuiz.getId());
        assertThat(quizEs).isEqualToComparingFieldByField(testQuiz);
    }

    @Test
    @Transactional
    public void getAllQuizzes() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get all the quizzes
        restQuizMockMvc.perform(get("/api/quizzes?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(quiz.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getQuiz() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);

        // Get the quiz
        restQuizMockMvc.perform(get("/api/quizzes/{id}", quiz.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(quiz.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingQuiz() throws Exception {
        // Get the quiz
        restQuizMockMvc.perform(get("/api/quizzes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQuiz() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);
        quizSearchRepository.save(quiz);
        int databaseSizeBeforeUpdate = quizRepository.findAll().size();

        // Update the quiz
        Quiz updatedQuiz = quizRepository.findOne(quiz.getId());
        updatedQuiz.setName(UPDATED_NAME);
        updatedQuiz.setDescription(UPDATED_DESCRIPTION);
        QuizDTO quizDTO = quizMapper.quizToQuizDTO(updatedQuiz);

        restQuizMockMvc.perform(put("/api/quizzes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(quizDTO)))
                .andExpect(status().isOk());

        // Validate the Quiz in the database
        List<Quiz> quizzes = quizRepository.findAll();
        assertThat(quizzes).hasSize(databaseSizeBeforeUpdate);
        Quiz testQuiz = quizzes.get(quizzes.size() - 1);
        assertThat(testQuiz.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testQuiz.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Quiz in ElasticSearch
        Quiz quizEs = quizSearchRepository.findOne(testQuiz.getId());
        assertThat(quizEs).isEqualToComparingFieldByField(testQuiz);
    }

    @Test
    @Transactional
    public void deleteQuiz() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);
        quizSearchRepository.save(quiz);
        int databaseSizeBeforeDelete = quizRepository.findAll().size();

        // Get the quiz
        restQuizMockMvc.perform(delete("/api/quizzes/{id}", quiz.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean quizExistsInEs = quizSearchRepository.exists(quiz.getId());
        assertThat(quizExistsInEs).isFalse();

        // Validate the database is empty
        List<Quiz> quizzes = quizRepository.findAll();
        assertThat(quizzes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchQuiz() throws Exception {
        // Initialize the database
        quizRepository.saveAndFlush(quiz);
        quizSearchRepository.save(quiz);

        // Search the quiz
        restQuizMockMvc.perform(get("/api/_search/quizzes?query=id:" + quiz.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quiz.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
