package com.certimaster.learning.application.command;

import com.certimaster.learning.application.command.BulkImportQuestionsCommand;
import com.certimaster.learning.application.command.BulkQuestionCommand;
import com.certimaster.learning.adapter.web.response.BulkImportResponse;
import com.certimaster.learning.domain.entity.CertificationDomain;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.domain.entity.QuestionOption;
import com.certimaster.learning.adapter.persistence.CertificationDomainWriteRepository;
import com.certimaster.learning.adapter.persistence.QuestionWriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionBulkImportService {

    private static final Logger log = LoggerFactory.getLogger(QuestionBulkImportService.class);

    private final QuestionWriteRepository questionRepository;
    private final CertificationDomainWriteRepository certificationDomainRepository;

    public QuestionBulkImportService(QuestionWriteRepository questionRepository,
                                     CertificationDomainWriteRepository certificationDomainRepository) {
        this.questionRepository = questionRepository;
        this.certificationDomainRepository = certificationDomainRepository;
    }

    @Transactional
    public BulkImportResponse bulkImport(BulkImportQuestionsCommand command) {
        if (command.questions() == null || command.questions().isEmpty()) {
            return new BulkImportResponse(0, 0, 0, List.of());
        }

        CertificationDomain domain = certificationDomainRepository.findById(command.domainId()).orElse(null);

        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < command.questions().size(); i++) {
            BulkQuestionCommand req = command.questions().get(i);
            try {
                Question question = new Question();
                question.setContent(req.questionText());
                question.setDifficulty(req.difficulty());
                question.setStatus("PUBLISHED");
                question.setDomain(domain);

                if (req.options() != null) {
                    for (var optReq : req.options()) {
                        QuestionOption opt = new QuestionOption();
                        opt.setContent(optReq.content());
                        opt.setIsCorrect(optReq.isCorrect());
                        opt.setQuestion(question);
                        question.getQuestionOptions().add(opt);
                    }
                }

                questionRepository.save(question);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                errors.add("Row " + (i + 1) + ": " + e.getMessage());
                log.warn("Bulk import failed for row {}: {}", i + 1, e.getMessage());
            }
        }

        return new BulkImportResponse(command.questions().size(), successCount, failureCount, errors);
    }
}
