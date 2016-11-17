package com.clinkast.quiz.repository.search;

import com.clinkast.quiz.domain.Result;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Result entity.
 */
public interface ResultSearchRepository extends ElasticsearchRepository<Result, Long> {
}
