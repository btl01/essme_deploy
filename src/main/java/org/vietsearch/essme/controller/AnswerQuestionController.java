package org.vietsearch.essme.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.filter.AuthenticatedRequest;
import org.vietsearch.essme.model.answer_question.Answer;
import org.vietsearch.essme.model.answer_question.Question;
import org.vietsearch.essme.repository.AnswerQuestionRepository;
import org.vietsearch.essme.repository.experts.ExpertRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
public class AnswerQuestionController {
    @Autowired
    private AnswerQuestionRepository questionRepository;
    @Autowired
    private ExpertRepository expertRepository;

    @GetMapping("/{id}")
    public Question getQuestionbyId(@PathVariable("id") String id) {
        return questionRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
    }

    @GetMapping
    public List<Question> getQuestions(@RequestParam(value = "limit", defaultValue = "20") int limit, @RequestParam(value = "sort", defaultValue = "createdAt") String sortAttr, @RequestParam(value = "desc", defaultValue = "false") boolean desc) {
        Sort sort = Sort.by(sortAttr);
        if (desc)
            sort = sort.descending();

        Page<Question> questionPage = questionRepository.findAll(PageRequest.of(0, limit, sort));
        return questionPage.getContent();
    }

    @GetMapping("/search")
    public Page<Question> searchEvents(@RequestParam(value = "what", required = false) String what,
                                       @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                       @RequestParam(value = "size", defaultValue = "20", required = false) int size
    ) {
        if (what == null || "".equals(what)) {
            return questionRepository.findAll(PageRequest.of(page, size));
        }
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(what);
        return questionRepository.findBy(criteria, PageRequest.of(page, size));
    }

    @GetMapping("/topic/{topic}")
    public Page<Question> getQuestionsbyTopic(@PathVariable("topic") String topic, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size, @RequestParam(value = "sort", defaultValue = "createdAt") String sortAttr, @RequestParam(value = "desc", defaultValue = "false") boolean desc) {
        Sort sort = Sort.by(sortAttr);
        if (desc)
            sort = sort.descending();

        Page<Question> questionPage = questionRepository.findByTopic(topic, PageRequest.of(page, size, sort));
        return questionPage;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearer-key")
    public Question addQuestion(AuthenticatedRequest request, @Valid @RequestBody Question question) {
        question.setUid(request.getUserId());
        questionRepository.save(question);
        return questionRepository.save(question);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearer-key")
    public Question updateQuestion(AuthenticatedRequest request, @PathVariable("id") String id, @Valid @RequestBody Question question) {
        String uuid = request.getUserId();
        if (questionRepository.existsById(id)) {
            // check id
            if (!matchUserQuestion(uuid, id)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
            }
            // update
            question.set_id(id);
            question.setUid(uuid);
            questionRepository.save(question);
            return question;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found", null);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearer-key")
    public String deleteQuestion(AuthenticatedRequest request, @PathVariable("id") String id) {
        String uuid = request.getUserId();
        if (questionRepository.existsById(id)) {
            // check id
            if (!matchUserQuestion(uuid, id)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
            }
            // delete
            questionRepository.deleteById(id);
            return "Deleted";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found", null);
        }
    }

    @PostMapping("/{questionId}/answers")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearer-key")
    public Question addAnswer(AuthenticatedRequest request, @PathVariable("questionId") String questionId, @Valid @RequestBody Answer answer) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found", null));
        if (question.getAnswers() == null)
            question.setAnswers(new ArrayList<>());
        answer.setUid(request.getUserId());
        question.getAnswers().add(answer);
        return questionRepository.save(question);
    }

    @GetMapping("/{questionId}/answers")
    public List<Answer> getAnswers(@PathVariable("questionId") String questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found", null));
        return question.getAnswers();
    }

    @GetMapping("/{questionId}/answers/{answerId}")
    public Answer getAnswerbyId(@PathVariable("questionId") String questionId, @PathVariable("answerId") String answerId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found", null));
        if (question.getAnswers() != null) {
            List<Answer> answerList = question.getAnswers();
            for (Answer answer : answerList) {
                if (answer.get_id().equals(answerId)) {
                    return answer;
                }
            }
        }
        return null;
    }

    @PutMapping("/{questionId}/answers/{answerId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearer-key")
    public Answer updateAnswer(AuthenticatedRequest request, @PathVariable("questionId") String questionId, @PathVariable("answerId") String answerId, @Valid @RequestBody Answer answer) {
        String uuid = request.getUserId();
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found", null));
        if (question.getAnswers() != null) {
            for (Answer answer1 : question.getAnswers()) {
                if (matchExpertAnswer(uuid, answerId, answer1)) {
                    answer1.setUid(uuid);
                    answer1.setExpertId(answer.getExpertId());
                    answer1.setAnswer(answer.getAnswer());
                    answer1.setUpdatedAt(new Date());
                    answer1.setVote(answer.getVote());
                    questionRepository.save(question);
                    return answer1;
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found", null);
    }

    @DeleteMapping("/{questionId}/answers/{answerId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearer-key")
    public String deleteAnswer(AuthenticatedRequest request, @PathVariable("questionId") String questionId, @PathVariable("answerId") String answerId) {
        String uuid = request.getUserId();
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found", null));
        if (question.getAnswers() != null) {
            for (Answer answer1 : question.getAnswers()) {
                if (matchExpertAnswer(uuid, answerId, answer1)) {
                    question.getAnswers().remove(answer1);
                    questionRepository.save(question);
                    return "Deleted";
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found", null);
    }

    @GetMapping("/byCustomerId/{CustomerId}")
    public List<Question> getQuestionbyCustomerId(@PathVariable("CustomerId") String customerId) {
        return questionRepository.findByCustomerId(customerId);
    }

    @GetMapping("/byExpertId/{ExpertId}")
    public List<Question> getQuestionbyexpertId(@PathVariable("ExpertId") String expertId) {
        return questionRepository.findByAnswersExpertId(expertId);
    }


    /*
     * uuid : firebase uuid
     * return true if user created question
     */
    private boolean matchUserQuestion(String uuid, String questionID) {
        // return true if uuid created question
        Optional<Question> optional = questionRepository.findById(questionID);
        return optional.map(question -> question.getUid().equals(uuid)).orElse(false);
    }

    /*
     * uuid : firebase uuid
     * return true if expert created answer
     */
    private boolean matchExpertAnswer(String uuid, String answerChangedId, Answer answer) {
        if (!answer.get_id().equals(answerChangedId))
            return false;
        if (!answer.getUid().equals(uuid))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
        return answer.getUid().equals(uuid);
    }
}
