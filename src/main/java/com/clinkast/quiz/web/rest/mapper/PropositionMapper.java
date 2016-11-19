package com.clinkast.quiz.web.rest.mapper;

import com.clinkast.quiz.domain.*;
import com.clinkast.quiz.web.rest.dto.PropositionDTO;

import org.mapstruct.*;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.clinkast.quiz.domain.Proposition;
import com.clinkast.quiz.domain.Question;
import com.clinkast.quiz.web.rest.dto.PropositionDTO;

/**
 * Mapper for the entity Proposition and its DTO PropositionDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PropositionMapper {

    @Mapping(source = "question.id", target = "questionId")
    PropositionDTO propositionToPropositionDTO(Proposition proposition);

    List<PropositionDTO> propositionsToPropositionDTOs(List<Proposition> propositions);
    
    Set<PropositionDTO> propositionsToPropositionDTOs(Set<Proposition> propositions);
    
    @Mapping(source = "questionId", target = "question")
    Proposition propositionDTOToProposition(PropositionDTO propositionDTO);

    List<Proposition> propositionDTOsToPropositions(List<PropositionDTO> propositionDTOs);
    
    Set<Proposition> propositionDTOsToPropositions(Set<PropositionDTO> propositionDTOs);
    
    default Question questionFromId(Long id) {
        if (id == null) {
            return null;
        }
        Question question = new Question();
        question.setId(id);
        return question;
    }
}