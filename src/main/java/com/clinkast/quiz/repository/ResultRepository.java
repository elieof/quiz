package com.clinkast.quiz.repository;

import com.clinkast.quiz.domain.Result;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Result entity.
 */
public interface ResultRepository extends JpaRepository<Result,Long> {

    @Query("select result from Result result where result.user.login = ?#{principal.username}")
    List<Result> findByUserIsCurrentUser();

}
