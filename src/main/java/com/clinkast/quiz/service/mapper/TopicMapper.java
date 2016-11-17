package com.clinkast.quiz.service.mapper;

import com.clinkast.quiz.domain.*;
import com.clinkast.quiz.service.dto.TopicDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Topic and its DTO TopicDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TopicMapper {

    TopicDTO topicToTopicDTO(Topic topic);

    List<TopicDTO> topicsToTopicDTOs(List<Topic> topics);

    Topic topicDTOToTopic(TopicDTO topicDTO);

    List<Topic> topicDTOsToTopics(List<TopicDTO> topicDTOs);
}
