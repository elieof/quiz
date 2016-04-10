package com.clinkast.quiz.repository;

import com.clinkast.quiz.domain.Proposition;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Proposition entity.
 */
public interface PropositionRepository extends JpaRepository<Proposition,Long> {

}
