package com.clinkast.quiz.web.rest;

import com.clinkast.quiz.QuizApp;
import com.clinkast.quiz.domain.Topic;
import com.clinkast.quiz.repository.TopicRepository;
import com.clinkast.quiz.service.TopicService;
import com.clinkast.quiz.repository.search.TopicSearchRepository;
import com.clinkast.quiz.web.rest.dto.TopicDTO;
import com.clinkast.quiz.web.rest.mapper.TopicMapper;

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
 * Test class for the TopicResource REST controller.
 *
 * @see TopicResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = QuizApp.class)
@WebAppConfiguration
@IntegrationTest
public class TopicResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private TopicRepository topicRepository;

    @Inject
    private TopicMapper topicMapper;

    @Inject
    private TopicService topicService;

    @Inject
    private TopicSearchRepository topicSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTopicMockMvc;

    private Topic topic;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TopicResource topicResource = new TopicResource();
        ReflectionTestUtils.setField(topicResource, "topicService", topicService);
        ReflectionTestUtils.setField(topicResource, "topicMapper", topicMapper);
        this.restTopicMockMvc = MockMvcBuilders.standaloneSetup(topicResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        topicSearchRepository.deleteAll();
        topic = new Topic();
        topic.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createTopic() throws Exception {
        int databaseSizeBeforeCreate = topicRepository.findAll().size();

        // Create the Topic
        TopicDTO topicDTO = topicMapper.topicToTopicDTO(topic);

        restTopicMockMvc.perform(post("/api/topics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(topicDTO)))
                .andExpect(status().isCreated());

        // Validate the Topic in the database
        List<Topic> topics = topicRepository.findAll();
        assertThat(topics).hasSize(databaseSizeBeforeCreate + 1);
        Topic testTopic = topics.get(topics.size() - 1);
        assertThat(testTopic.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Topic in ElasticSearch
        Topic topicEs = topicSearchRepository.findOne(testTopic.getId());
        assertThat(topicEs).isEqualToComparingFieldByField(testTopic);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = topicRepository.findAll().size();
        // set the field null
        topic.setName(null);

        // Create the Topic, which fails.
        TopicDTO topicDTO = topicMapper.topicToTopicDTO(topic);

        restTopicMockMvc.perform(post("/api/topics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(topicDTO)))
                .andExpect(status().isBadRequest());

        List<Topic> topics = topicRepository.findAll();
        assertThat(topics).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTopics() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get all the topics
        restTopicMockMvc.perform(get("/api/topics?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(topic.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getTopic() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);

        // Get the topic
        restTopicMockMvc.perform(get("/api/topics/{id}", topic.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(topic.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTopic() throws Exception {
        // Get the topic
        restTopicMockMvc.perform(get("/api/topics/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTopic() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);
        topicSearchRepository.save(topic);
        int databaseSizeBeforeUpdate = topicRepository.findAll().size();

        // Update the topic
        Topic updatedTopic = new Topic();
        updatedTopic.setId(topic.getId());
        updatedTopic.setName(UPDATED_NAME);
        TopicDTO topicDTO = topicMapper.topicToTopicDTO(updatedTopic);

        restTopicMockMvc.perform(put("/api/topics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(topicDTO)))
                .andExpect(status().isOk());

        // Validate the Topic in the database
        List<Topic> topics = topicRepository.findAll();
        assertThat(topics).hasSize(databaseSizeBeforeUpdate);
        Topic testTopic = topics.get(topics.size() - 1);
        assertThat(testTopic.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Topic in ElasticSearch
        Topic topicEs = topicSearchRepository.findOne(testTopic.getId());
        assertThat(topicEs).isEqualToComparingFieldByField(testTopic);
    }

    @Test
    @Transactional
    public void deleteTopic() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);
        topicSearchRepository.save(topic);
        int databaseSizeBeforeDelete = topicRepository.findAll().size();

        // Get the topic
        restTopicMockMvc.perform(delete("/api/topics/{id}", topic.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean topicExistsInEs = topicSearchRepository.exists(topic.getId());
        assertThat(topicExistsInEs).isFalse();

        // Validate the database is empty
        List<Topic> topics = topicRepository.findAll();
        assertThat(topics).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTopic() throws Exception {
        // Initialize the database
        topicRepository.saveAndFlush(topic);
        topicSearchRepository.save(topic);

        // Search the topic
        restTopicMockMvc.perform(get("/api/_search/topics?query=id:" + topic.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(topic.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
