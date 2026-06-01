# CertiMaster API Testing Checklist

Generated from Spring MVC controllers in `src/main/java/com/certimaster`.

## Setup

- [ ] Start backend at `http://localhost:8080` or update `baseUrl` in Postman.
- [ ] Import `postman/certimaster-api.postman_collection.json`.
- [ ] Run `Identity / Auth > Login` and verify collection variables `accessToken` and `refreshToken` are populated.
- [ ] Use an ADMIN token for `/api/v1/admin/**` endpoints.
- [ ] Replace placeholder IDs/slugs in collection variables before running dependent endpoints.

## Endpoint Checklist

### Shared / Lookups

- [ ] `GET /api/v1/lookups` - Public (query: `includeInactive, certificationId`) - Returns centralized constants and catalog options for frontend combo boxes.

### Identity / Auth

- [ ] `POST /api/v1/auth/register` - Public (body: `register`) - Public registration endpoint.
- [ ] `POST /api/v1/auth/login` - Public (body: `login`) - Public login endpoint. Test script stores accessToken and refreshToken when present.
- [ ] `GET /api/v1/auth/oauth/authorize/{{provider}}` - Public (query: `redirectUri`) - Build OAuth authorization URL for google/github.
- [ ] `GET /api/v1/auth/oauth/callback/{{provider}}` - Public (query: `code, state, error`) - Provider callback endpoint. Usually called by Google/GitHub and redirects to frontend.
- [ ] `GET /api/v1/auth/current` - Bearer token - Requires authenticated user.
- [ ] `POST /api/v1/auth/refresh` - Public (body: `refresh`) - Uses refresh cookie or JSON body refreshToken.
- [ ] `POST /api/v1/auth/logout` - Bearer token - Requires Authorization header.
- [ ] `GET /api/v1/auth/verify-email` - Public (query: `token`)
- [ ] `POST /api/v1/auth/forgot-password` - Public (body: `forgotPassword`)
- [ ] `POST /api/v1/auth/reset-password` - Public (body: `resetPassword`)

### Identity / Profile

- [ ] `GET /api/v1/profile/me` - Bearer token
- [ ] `PUT /api/v1/profile/me` - Bearer token (body: `updateProfile`)
- [ ] `POST /api/v1/profile/me/avatar` - Bearer token (body: `multipart/form-data file`) - Multipart form-data. Choose a local image file in Postman.
- [ ] `GET /api/v1/profile/{{userId}}` - Bearer token - Admin only.

### Identity / Admin Users

- [ ] `GET /api/v1/admin/users` - Bearer token (query: `page, size, search, roleCode, status, registrationDateFrom, registrationDateTo`) - Admin only.
- [ ] `GET /api/v1/admin/users/{{userId}}` - Bearer token - Admin only.
- [ ] `PUT /api/v1/admin/users/{{userId}}` - Bearer token (body: `updateUser`) - Admin only.
- [ ] `PATCH /api/v1/admin/users/{{userId}}/activate` - Bearer token - Admin only.
- [ ] `PATCH /api/v1/admin/users/{{userId}}/deactivate` - Bearer token - Admin only.
- [ ] `DELETE /api/v1/admin/users/{{userId}}` - Bearer token - Admin only.
- [ ] `POST /api/v1/admin/users/bulk/activate` - Bearer token (body: `bulkUsers`) - Admin only.
- [ ] `POST /api/v1/admin/users/bulk/deactivate` - Bearer token (body: `bulkUsers`) - Admin only.
- [ ] `GET /api/v1/admin/users/{{userId}}/sessions/active` - Bearer token - Admin only.
- [ ] `DELETE /api/v1/admin/users/{{userId}}/sessions/{{authSessionId}}` - Bearer token - Admin only.
- [ ] `DELETE /api/v1/admin/users/{{userId}}/sessions` - Bearer token - Admin only.

### Identity / Admin Audit

- [ ] `GET /api/v1/admin/audit` - Bearer token (query: `page, size, sort, actorId, resourceType, from, to`) - Admin only.
- [ ] `GET /api/v1/admin/audit/{{auditLogId}}` - Bearer token - Admin only.

### Engagement / Posts

- [ ] `GET /api/v1/posts` - Public (query: `page, size, sortBy, sortDirection, keyword, status, authorId, tagId, contentType, difficultyLevel, publishedFrom, publishedTo`)
- [ ] `GET /api/v1/posts/{{postSlug}}` - Public
- [ ] `POST /api/v1/posts` - Bearer token (body: `post`) - Requires BLOG_CREATE authority.
- [ ] `PUT /api/v1/posts/{{postId}}` - Bearer token (body: `post`) - Requires BLOG_UPDATE or BLOG_CREATE authority.
- [ ] `DELETE /api/v1/posts/{{postId}}` - Bearer token - Requires BLOG_DELETE or BLOG_CREATE authority.
- [ ] `POST /api/v1/posts/{{postId}}/publish` - Bearer token - Requires BLOG_PUBLISH authority.
- [ ] `POST /api/v1/posts/{{postId}}/archive` - Bearer token - Requires BLOG_PUBLISH authority.
- [ ] `POST /api/v1/posts/{{postId}}/schedule` - Bearer token (query: `scheduledAt`) - Requires BLOG_PUBLISH authority.
- [ ] `POST /api/v1/posts/{{postId}}/submit-review` - Bearer token - Requires BLOG_CREATE authority.
- [ ] `GET /api/v1/posts/trending` - Public (query: `page, size`)
- [ ] `GET /api/v1/posts/featured` - Public (query: `page, size`)
- [ ] `GET /api/v1/posts/author/{{userId}}` - Public (query: `page, size`)

### Engagement / Admin Posts

- [ ] `GET /api/v1/admin/posts` - Bearer token (query: `page, size, sortBy, sortDirection, status`) - Admin only.
- [ ] `POST /api/v1/admin/posts/{{postId}}/feature` - Bearer token (query: `featured`) - Admin only.
- [ ] `POST /api/v1/admin/posts/{{postId}}/pin` - Bearer token (query: `pinned`) - Admin only.

### Engagement / Tags

- [ ] `GET /api/v1/tags` - Public (query: `page, size, sortBy, sortDirection, keyword`)
- [ ] `GET /api/v1/tags/{{tagSlug}}` - Public
- [ ] `GET /api/v1/tags/{{tagSlug}}/posts` - Public (query: `page, size`)
- [ ] `POST /api/v1/tags` - Bearer token (body: `engagementTag`)
- [ ] `PUT /api/v1/tags/{{tagId}}` - Bearer token (body: `engagementTag`) - Requires BLOG_ADMIN or ADMIN.
- [ ] `DELETE /api/v1/tags/{{tagId}}` - Bearer token - Requires BLOG_ADMIN or ADMIN.

### Engagement / Bookmarks

- [ ] `POST /api/v1/bookmarks/posts/{{postId}}` - Bearer token (body: `bookmark`)
- [ ] `GET /api/v1/bookmarks` - Bearer token (query: `page, size`)

### Learning / Certifications

- [ ] `GET /api/v1/certifications` - Public (query: `page, size, sortBy, sortDirection, keyword, provider, level, status, providerId, difficultyLevel`)
- [ ] `GET /api/v1/certifications/{{certificationId}}` - Public
- [ ] `GET /api/v1/certifications/{{certificationSlug}}` - Public
- [ ] `GET /api/v1/certifications/{{certificationSlug}}/domains` - Public
- [ ] `GET /api/v1/certifications/search` - Public (query: `q, page, size`)

### Learning / Admin Certifications

- [ ] `POST /api/v1/admin/certifications` - Bearer token (body: `certification`) - Admin only.
- [ ] `PUT /api/v1/admin/certifications/{{certificationId}}` - Bearer token (body: `certification`) - Admin only.
- [ ] `PATCH /api/v1/admin/certifications/{{certificationId}}/status` - Bearer token (query: `status`) - Admin only.
- [ ] `DELETE /api/v1/admin/certifications/{{certificationId}}` - Bearer token - Admin only.

### Learning / Certification Domains

- [ ] `POST /api/v1/admin/certifications/{{certificationId}}/domains` - Bearer token (body: `domain`) - Admin only.
- [ ] `PUT /api/v1/admin/domains/{{domainId}}` - Bearer token (body: `domain`) - Admin only.
- [ ] `DELETE /api/v1/admin/domains/{{domainId}}` - Bearer token - Admin only.

### Learning / Exams

- [ ] `GET /api/v1/exams` - Public (query: `page, size, sortBy, sortDirection, keyword, certificationId, type, status`)
- [ ] `GET /api/v1/exams/{{examId}}` - Public
- [ ] `GET /api/v1/exams/certification/{{certificationId}}` - Public
- [ ] `POST /api/v1/exams` - Bearer token (body: `exam`) - Requires ADMIN or INSTRUCTOR.
- [ ] `PUT /api/v1/exams/{{examId}}` - Bearer token (body: `exam`) - Requires ADMIN or INSTRUCTOR.
- [ ] `DELETE /api/v1/exams/{{examId}}` - Bearer token - Admin only.

### Learning / Exam Sessions

- [ ] `POST /api/v1/exam-sessions/exam/{{examId}}/start` - Bearer token (body: `startExam`)
- [ ] `POST /api/v1/exam-sessions/practice/start` - Bearer token (body: `startPractice`)
- [ ] `GET /api/v1/exam-sessions/{{examSessionId}}` - Bearer token
- [ ] `GET /api/v1/exam-sessions/{{examSessionId}}/resume` - Bearer token
- [ ] `GET /api/v1/exam-sessions/active` - Bearer token
- [ ] `POST /api/v1/exam-sessions/{{examSessionId}}/answer` - Bearer token (body: `answer`)
- [ ] `POST /api/v1/exam-sessions/{{examSessionId}}/complete` - Bearer token
- [ ] `POST /api/v1/exam-sessions/{{examSessionId}}/timeout` - Bearer token
- [ ] `POST /api/v1/exam-sessions/{{examSessionId}}/abandon` - Bearer token
- [ ] `GET /api/v1/exam-sessions/{{examSessionId}}/review` - Bearer token

### Learning / Results

- [ ] `GET /api/v1/results/{{resultId}}` - Bearer token
- [ ] `GET /api/v1/results/{{resultId}}/questions` - Bearer token (query: `page, size, filter, topicId, difficulty`)
- [ ] `GET /api/v1/results/user/{{userId}}/history` - Bearer token (query: `page, size, sort, certificationId, examId, status, startDate, endDate`) - Owner or ADMIN.

### Learning / Questions

- [ ] `GET /api/v1/questions` - Bearer token (query: `examId`)
- [ ] `GET /api/v1/questions/{{questionId}}` - Bearer token
- [ ] `GET /api/v1/questions/{{questionId}}/exam` - Bearer token
- [ ] `GET /api/v1/questions/domain/{{domainId}}` - Bearer token
- [ ] `POST /api/v1/questions` - Bearer token (body: `question`)
- [ ] `PUT /api/v1/questions/{{questionId}}` - Bearer token (body: `question`)
- [ ] `DELETE /api/v1/questions/{{questionId}}` - Bearer token
- [ ] `POST /api/v1/questions/bulk-import` - Bearer token (query: `certificationId, domainId`; body: `bulkQuestions`) - Requires ADMIN or INSTRUCTOR.

### Learning / Admin Questions

- [ ] `GET /api/v1/admin/questions` - Bearer token (query: `page, size, sortBy, sortDirection, keyword, domainId, certificationId, type, difficulty, status`) - Admin only.
- [ ] `GET /api/v1/admin/questions/{{questionId}}` - Bearer token - Admin only.
- [ ] `POST /api/v1/admin/questions` - Bearer token (body: `question`) - Admin only.
- [ ] `PUT /api/v1/admin/questions/{{questionId}}` - Bearer token (body: `question`) - Admin only.
- [ ] `PATCH /api/v1/admin/questions/{{questionId}}/status` - Bearer token (query: `status`) - Admin only.
- [ ] `DELETE /api/v1/admin/questions/{{questionId}}` - Bearer token - Admin only.

### Learning / Question Tags

- [ ] `GET /api/v1/question-tags` - Bearer token (query: `page, size, sortBy, sortDirection, keyword, status`)
- [ ] `GET /api/v1/question-tags/all` - Bearer token
- [ ] `GET /api/v1/question-tags/{{questionTagId}}` - Bearer token
- [ ] `GET /api/v1/question-tags/question/{{questionId}}` - Bearer token
- [ ] `POST /api/v1/question-tags` - Bearer token (body: `questionTag`)
- [ ] `PUT /api/v1/question-tags/{{questionTagId}}` - Bearer token (body: `questionTag`)
- [ ] `DELETE /api/v1/question-tags/{{questionTagId}}` - Bearer token
- [ ] `POST /api/v1/question-tags/question/{{questionId}}` - Bearer token (body: `tagIds`)
- [ ] `DELETE /api/v1/question-tags/question/{{questionId}}` - Bearer token (body: `tagIds`)

## Notes

- Public endpoints in the checklist are based on `SecurityConfig` and method-level annotations.
- Some endpoints without `@PreAuthorize` are still protected by `anyRequest().authenticated()` in Spring Security.
- OAuth callback endpoints are normally called by external providers and may redirect instead of returning JSON.
- Delete/update endpoints can mutate data; duplicate the collection or use a disposable database for destructive test runs.
