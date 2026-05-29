package com.certimaster.shared.advice;

import com.certimaster.shared.dto.ApiResponse;
import com.certimaster.shared.dto.ApiResponseMetadata;
import com.certimaster.shared.util.UlidUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Value("${api.version:1.0}")
    private String apiVersion;

    @Value("${app.domain:unknown}")
    private String appDomain;

    @Value("${app.service:${spring.application.name:unknown}}")
    private String appService;

    @Override
    public boolean supports(@NotNull MethodParameter returnType,
                            @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  @NotNull MethodParameter returnType,
                                  @NotNull MediaType selectedContentType,
                                  @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NotNull ServerHttpRequest request,
                                  @NotNull ServerHttpResponse response) {

        if (body == null
                || body instanceof ApiResponse<?>
                || body instanceof ProblemDetail
                || body instanceof String
                || body instanceof byte[]) {
            return body;
        }

        int statusCode = 200;
        if (response instanceof ServletServerHttpResponse servletResponse) {
            statusCode = servletResponse.getServletResponse().getStatus();
        }

        ApiResponseMetadata metadata = ApiResponseMetadata.builder()
                .id(UlidUtil.generate())
                .version(apiVersion)
                .timestamp(Instant.now().toString())
                .domain(appDomain)
                .service(appService)
                .type(deriveType(body))
                .correlationId(MDC.get("correlationId"))
                .statusCode(statusCode)
                .success(statusCode < 400)
                .build();

        return ApiResponse.of(metadata, body);
    }

    private String deriveType(Object data) {
        String name = data.getClass().getSimpleName();
        return name.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }
}
