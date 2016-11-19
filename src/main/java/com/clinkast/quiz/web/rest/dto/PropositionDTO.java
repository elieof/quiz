package com.clinkast.quiz.web.rest.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Proposition entity.
 */
public class PropositionDTO implements Serializable {

    private Long id;

    @NotNull
    private String statement;


    @NotNull
    private Boolean valid;


    private String explanation;


    private Long questionId;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }
    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PropositionDTO propositionDTO = (PropositionDTO) o;

        if ( ! Objects.equals(id, propositionDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PropositionDTO{" +
            "id=" + id +
            ", statement='" + statement + "'" +
            ", valid='" + valid + "'" +
            ", explanation='" + explanation + "'" +
            '}';
    }
}
