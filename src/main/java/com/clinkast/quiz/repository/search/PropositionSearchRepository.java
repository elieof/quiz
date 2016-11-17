package com.clinkast.quiz.repository.search;

import com.clinkast.quiz.domain.Proposition;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Proposition entity.
 */
public interface PropositionSearchRepository extends ElasticsearchRepository<Proposition, Long> {
}
