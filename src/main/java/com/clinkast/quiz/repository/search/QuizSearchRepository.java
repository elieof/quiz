package com.clinkast.quiz.repository.search;

import com.clinkast.quiz.domain.Quiz;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Quiz entity.
 */
public interface QuizSearchRepository extends ElasticsearchRepository<Quiz, Long> {
}
