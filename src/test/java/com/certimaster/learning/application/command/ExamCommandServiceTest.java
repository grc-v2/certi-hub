package com.certimaster.learning.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.learning.domain.entity.Certification;
import com.certimaster.learning.adapter.persistence.CertificationWriteRepository;
import com.certimaster.learning.application.command.CreateExamCommand;
import com.certimaster.learning.adapter.web.response.ExamResponse;
import com.certimaster.learning.domain.entity.Exam;
import com.certimaster.learning.adapter.web.mapper.ExamMapper;
import com.certimaster.learning.adapter.persistence.ExamWriteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamCommandServiceTest {

    @Mock
    private ExamWriteRepository examRepository;

    @Mock
    private CertificationWriteRepository certificationRepository;

    @Mock
    private ExamMapper examMapper;

    @InjectMocks
    private ExamCommandService service;

    @Test
    void createSetsCertificationAndDefaultDraftStatus() {
        CreateExamCommand command = new CreateExamCommand(
                7L,
                "CKA Mock Exam",
                "MOCK",
                "description",
                120,
                60,
                70,
                null
        );
        Certification certification = new Certification();
        certification.setId(7L);
        Exam exam = new Exam();
        ExamResponse response = ExamResponse.builder().id(10L).title("CKA Mock Exam").build();

        when(certificationRepository.findActiveById(7L)).thenReturn(Optional.of(certification));
        when(examRepository.existsByTitleIgnoreCaseAndCertificationId("CKA Mock Exam", 7L)).thenReturn(false);
        when(examMapper.toEntity(command)).thenReturn(exam);
        when(examRepository.save(exam)).thenReturn(exam);
        when(examMapper.toResponse(exam)).thenReturn(response);

        ExamResponse result = service.create(command);

        ArgumentCaptor<Exam> savedExam = ArgumentCaptor.forClass(Exam.class);
        verify(examRepository).save(savedExam.capture());
        assertThat(savedExam.getValue().getCertification()).isSameAs(certification);
        assertThat(savedExam.getValue().getStatus()).isEqualTo("DRAFT");
        assertThat(result).isSameAs(response);
    }

    @Test
    void createRejectsDuplicateTitleInCertification() {
        CreateExamCommand command = new CreateExamCommand(
                7L,
                "CKA Mock Exam",
                "MOCK",
                null,
                120,
                60,
                70,
                "ACTIVE"
        );
        Certification certification = new Certification();
        certification.setId(7L);

        when(certificationRepository.findActiveById(7L)).thenReturn(Optional.of(certification));
        when(examRepository.existsByTitleIgnoreCaseAndCertificationId("CKA Mock Exam", 7L)).thenReturn(true);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(BusinessException.class);
        verifyNoMoreInteractions(examMapper);
    }
}
