package com.clinkast.quiz.service.mapper;

import com.clinkast.quiz.domain.*;
import com.clinkast.quiz.service.dto.PropositionDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Proposition and its DTO PropositionDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PropositionMapper {

    @Mapping(source = "question.id", target = "questionId")
    PropositionDTO propositionToPropositionDTO(Proposition proposition);

    List<PropositionDTO> propositionsToPropositionDTOs(List<Proposition> propositions);

    @Mapping(source = "questionId", target = "question")
    Proposition propositionDTOToProposition(PropositionDTO propositionDTO);

    List<Proposition> propositionDTOsToPropositions(List<PropositionDTO> propositionDTOs);

    default Question questionFromId(Long id) {
        if (id == null) {
            return null;
        }
        Question question = new Question();
        question.setId(id);
        return question;
    }
}
