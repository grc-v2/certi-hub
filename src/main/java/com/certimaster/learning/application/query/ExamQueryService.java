package com.certimaster.learning.application.query;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.adapter.persistence.CertificationReadRepository;
import com.certimaster.learning.application.query.GetExamByIdQuery;
import com.certimaster.learning.application.query.GetExamsByCertificationQuery;
import com.certimaster.learning.application.query.SearchExamsQuery;
import com.certimaster.learning.adapter.web.response.ExamDetailResponse;
import com.certimaster.learning.adapter.web.response.ExamResponse;
import com.certimaster.learning.domain.entity.Exam;
import com.certimaster.learning.adapter.web.mapper.ExamMapper;
import com.certimaster.learning.adapter.persistence.ExamReadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ExamQueryService {


    private static final String EXAM = "Exam";
    private static final String CERTIFICATION = "Certification";

    private final ExamReadRepository examRepository;
    private final CertificationReadRepository certificationRepository;
    private final ExamMapper examMapper;

    public ExamQueryService(ExamReadRepository examRepository,
                            CertificationReadRepository certificationRepository,
                            ExamMapper examMapper) {
        this.examRepository = examRepository;
        this.certificationRepository = certificationRepository;
        this.examMapper = examMapper;
    }

    public PageResponse<ExamResponse> search(SearchExamsQuery query) {
        Sort sort = createSort(query.sortBy(), query.sortDirection());
        Pageable pageable = PageRequest.of(query.page(), query.size(), sort);

        Page<Exam> page = examRepository.search(
                query.keyword(),
                query.certificationId(),
                query.type(),
                query.status(),
                pageable
        );

        Page<ExamResponse> responsePage = page.map(examMapper::toResponse);
        return PageResponse.of(responsePage);
    }

    public ExamDetailResponse getById(GetExamByIdQuery query) {
        Exam exam = findActiveOrThrow(query.id());
        return examMapper.toDetailResponse(exam);
    }

    public List<ExamResponse> getByCertificationId(GetExamsByCertificationQuery query) {
        boolean exists = certificationRepository.existsById(query.certificationId());
        if (!exists) {
            throw ResourceNotFoundException.byId(CERTIFICATION, query.certificationId());
        }

        List<Exam> exams = examRepository.findByCertificationId(query.certificationId());
        return exams.stream()
                .map(examMapper::toResponse)
                .toList();
    }

    private Exam findActiveOrThrow(Long id) {
        return examRepository.findActiveById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(EXAM, id));
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        String field = sortBy != null && !sortBy.isBlank() ? sortBy : "createdAt";
        return Sort.by(direction, field);
    }
}
