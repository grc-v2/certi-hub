package com.certimaster.shared.utils;

import java.util.Map;
import java.util.HashMap;

public class LookupTranslator {

    private static final Map<String, String> LOCALES = new HashMap<>();

    static {
        // Common statuses
        LOCALES.put("ACTIVE", "Hoạt động");
        LOCALES.put("INACTIVE", "Ẩn");
        LOCALES.put("PENDING", "Chờ duyệt");
        LOCALES.put("COMPLETED", "Đã hoàn thành");
        LOCALES.put("FAILED", "Thất bại");
        LOCALES.put("DELETED", "Đã xóa");
        LOCALES.put("ARCHIVED", "Lưu trữ");
        LOCALES.put("IN_REVIEW", "Chờ duyệt");
        LOCALES.put("SCHEDULED", "Lên lịch");

        // Certification levels & difficulty levels
        LOCALES.put("BEGINNER", "Beginner");
        LOCALES.put("INTERMEDIATE", "Intermediate");
        LOCALES.put("ADVANCED", "Advanced");
        LOCALES.put("EXPERT", "Expert");

        LOCALES.put("FOUNDATIONAL", "Cơ bản");
        LOCALES.put("ASSOCIATE", "Thành viên");
        LOCALES.put("PROFESSIONAL", "Chuyên nghiệp");
        LOCALES.put("SPECIALTY", "Chuyên biệt");

        // Exam Types
        LOCALES.put("PRACTICE", "Luyện tập");
        LOCALES.put("MOCK", "Thi thử");
        LOCALES.put("FINAL", "Thi thật");
        LOCALES.put("DIAGNOSTIC", "Đánh giá năng lực");
        LOCALES.put("TOPIC_WISE", "Theo chủ đề");

        // Exam Statuses
        LOCALES.put("DRAFT", "Nháp");

        // Question Types
        LOCALES.put("SINGLE_CHOICE", "Một đáp án");
        LOCALES.put("MULTIPLE_CHOICE", "Nhiều đáp án");
        LOCALES.put("TRUE_FALSE", "Đúng/Sai");

        // Question difficulties
        LOCALES.put("EASY", "Dễ");
        LOCALES.put("MEDIUM", "Trung bình");
        LOCALES.put("HARD", "Khó");

        // Question statuses
        LOCALES.put("REVIEW", "Chờ duyệt");
        LOCALES.put("PUBLISHED", "Đã đăng");
        LOCALES.put("DEPRECATED", "Không dùng");
        LOCALES.put("REPORTED", "Bị báo cáo");

        // Session modes
        LOCALES.put("EXAM", "Thi thật");

        // Session statuses
        LOCALES.put("IN_PROGRESS", "Đang làm");
        LOCALES.put("TIMED_OUT", "Hết giờ");
        LOCALES.put("ABANDONED", "Bỏ qua");

        // Result statuses
        LOCALES.put("PASSED", "Đỗ");
    }

    public static String translate(String value, String defaultLabel) {
        return LOCALES.getOrDefault(value.toUpperCase(), defaultLabel);
    }
}
