package com.clinkast.quiz.web.rest.mapper;

import com.clinkast.quiz.domain.*;
import com.clinkast.quiz.web.rest.dto.QuestionDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Question and its DTO QuestionDTO.
 */
@Mapper(componentModel = "spring", uses = {PropositionMapper.class})
public interface QuestionMapper {

    @Mapping(source = "topic.id", target = "topicId")
    @Mapping(source = "topic.name", target = "topicName")
    @Mapping(source = "quiz.id", target = "quizId")
    QuestionDTO questionToQuestionDTO(Question question);

    List<QuestionDTO> questionsToQuestionDTOs(List<Question> questions);

    @Mapping(source = "topicId", target = "topic")
    @Mapping(source = "quizId", target = "quiz")
    Question questionDTOToQuestion(QuestionDTO questionDTO);

    List<Question> questionDTOsToQuestions(List<QuestionDTO> questionDTOs);

    default Topic topicFromId(Long id) {
        if (id == null) {
            return null;
        }
        Topic topic = new Topic();
        topic.setId(id);
        return topic;
    }

    default Quiz quizFromId(Long id) {
        if (id == null) {
            return null;
        }
        Quiz quiz = new Quiz();
        quiz.setId(id);
        return quiz;
    }
}
