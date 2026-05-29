package com.certimaster.learning.application.query;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.learning.adapter.persistence.CertificationReadRepository;
import com.certimaster.learning.application.query.SearchExamsQuery;
import com.certimaster.learning.adapter.web.response.ExamResponse;
import com.certimaster.learning.domain.entity.Exam;
import com.certimaster.learning.adapter.web.mapper.ExamMapper;
import com.certimaster.learning.adapter.persistence.ExamReadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamQueryServiceTest {

    @Mock
    private ExamReadRepository examRepository;

    @Mock
    private CertificationReadRepository certificationRepository;

    @Mock
    private ExamMapper examMapper;

    @InjectMocks
    private ExamQueryService service;

    @Test
    void searchReturnsPagedResponses() {
        SearchExamsQuery query = new SearchExamsQuery(
                "cka",
                7L,
                "MOCK",
                "ACTIVE",
                0,
                10,
                "createdAt",
                "DESC"
        );
        Exam exam = new Exam();
        ExamResponse response = ExamResponse.builder().id(10L).title("CKA Mock Exam").build();

        when(examRepository.search(
                org.mockito.ArgumentMatchers.eq("cka"),
                org.mockito.ArgumentMatchers.eq(7L),
                org.mockito.ArgumentMatchers.eq("MOCK"),
                org.mockito.ArgumentMatchers.eq("ACTIVE"),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(exam)));
        when(examMapper.toResponse(exam)).thenReturn(response);

        PageResponse<ExamResponse> result = service.search(query);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}
