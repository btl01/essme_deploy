package org.vietsearch.essme.repository;

import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.vietsearch.essme.model.answer_question.Question;
import org.vietsearch.essme.model.corporate_title.Corporate;

import java.util.List;
import java.util.Optional;

public interface AnswerQuestionRepository extends MongoRepository<Question,String> {
    Page<Question> findByTopic(String topic, Pageable pageable);
    Page<Question> findBy(TextCriteria criteria, Pageable pageable);
    List<Question> findByCustomerId(String customerId);
    @Query("{'answers.expert_id':?0}")
    List<Question> findByAnswersExpertId(String expertId);
}
