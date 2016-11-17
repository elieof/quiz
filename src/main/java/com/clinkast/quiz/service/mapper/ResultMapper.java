package com.clinkast.quiz.service.mapper;

import com.clinkast.quiz.domain.*;
import com.clinkast.quiz.service.dto.ResultDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Result and its DTO ResultDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, })
public interface ResultMapper {

    @Mapping(source = "quiz.id", target = "quizId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "question.id", target = "questionId")
    @Mapping(source = "proposition.id", target = "propositionId")
    ResultDTO resultToResultDTO(Result result);

    List<ResultDTO> resultsToResultDTOs(List<Result> results);

    @Mapping(source = "quizId", target = "quiz")
    @Mapping(source = "userId", target = "user")
    @Mapping(source = "questionId", target = "question")
    @Mapping(source = "propositionId", target = "proposition")
    Result resultDTOToResult(ResultDTO resultDTO);

    List<Result> resultDTOsToResults(List<ResultDTO> resultDTOs);

    default Quiz quizFromId(Long id) {
        if (id == null) {
            return null;
        }
        Quiz quiz = new Quiz();
        quiz.setId(id);
        return quiz;
    }

    default Question questionFromId(Long id) {
        if (id == null) {
            return null;
        }
        Question question = new Question();
        question.setId(id);
        return question;
    }

    default Proposition propositionFromId(Long id) {
        if (id == null) {
            return null;
        }
        Proposition proposition = new Proposition();
        proposition.setId(id);
        return proposition;
    }
}
