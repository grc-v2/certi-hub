package com.certimaster.learning.application.query;

import com.certimaster.learning.application.query.GetQuestionReportQuery;
import com.certimaster.learning.application.query.GetResultDetailQuery;
import com.certimaster.learning.application.query.GetUserHistoryQuery;
import com.certimaster.learning.adapter.web.response.QuestionReportResponse;
import com.certimaster.learning.adapter.web.response.ResultDetailResponse;
import com.certimaster.learning.adapter.web.response.UserHistoryResponse;
import org.springframework.stereotype.Service;

@Service
public class ExamResultQueryService {

    private final GetResultDetailHandler getResultDetailHandler;
    private final GetQuestionReportHandler getQuestionReportHandler;
    private final GetUserHistoryHandler getUserHistoryHandler;

    public ExamResultQueryService(GetResultDetailHandler getResultDetailHandler,
                                  GetQuestionReportHandler getQuestionReportHandler,
                                  GetUserHistoryHandler getUserHistoryHandler) {
        this.getResultDetailHandler = getResultDetailHandler;
        this.getQuestionReportHandler = getQuestionReportHandler;
        this.getUserHistoryHandler = getUserHistoryHandler;
    }

    public ResultDetailResponse getResultDetail(GetResultDetailQuery query) {
        return getResultDetailHandler.handle(query);
    }

    public QuestionReportResponse getQuestionReport(GetQuestionReportQuery query) {
        return getQuestionReportHandler.handle(query);
    }

    public UserHistoryResponse getUserHistory(GetUserHistoryQuery query) {
        return getUserHistoryHandler.handle(query);
    }
}
