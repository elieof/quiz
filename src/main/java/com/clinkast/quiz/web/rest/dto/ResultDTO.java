package com.clinkast.quiz.web.rest.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Result entity.
 */
public class ResultDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean valid;


    private Long quizId;
    private Long userId;
    private Long questionId;
    private Long propositionId;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    public Long getPropositionId() {
        return propositionId;
    }

    public void setPropositionId(Long propositionId) {
        this.propositionId = propositionId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResultDTO resultDTO = (ResultDTO) o;

        if ( ! Objects.equals(id, resultDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ResultDTO{" +
            "id=" + id +
            ", valid='" + valid + "'" +
            '}';
    }
}
