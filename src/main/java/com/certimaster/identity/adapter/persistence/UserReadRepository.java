package com.certimaster.identity.adapter.persistence;

import com.certimaster.identity.domain.entity.User;
import com.certimaster.identity.adapter.persistence.projection.UserSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserReadRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsernameIgnoreCase(String username);

    /** Ids of every active (non-deleted) user — used for broadcast fan-out. */
    @Query("SELECT u.id FROM User u WHERE u.deletedAt IS NULL AND u.status = 'ACTIVE'")
    List<Long> findActiveUserIds();

    /** Ids of active users holding the given role code — used for targeted broadcasts. */
    @Query("SELECT DISTINCT u.id FROM User u JOIN u.roles r " +
           "WHERE u.deletedAt IS NULL AND u.status = 'ACTIVE' AND r.code = :roleCode")
    List<Long> findActiveUserIdsByRole(@Param("roleCode") String roleCode);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :userId")
    Optional<User> findByIdWithRoles(@Param("userId") Long userId);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    @EntityGraph(attributePaths = "roles")
    Page<User> findByDeletedAtIsNull(Pageable pageable);

    @Query(value = """
            SELECT u.id AS id,
                   u.email AS email,
                   u.full_name AS "fullName",
                   u.username AS username,
                   u.status AS status,
                   COALESCE(string_agg(DISTINCT r.code, ','), '') AS "roleCodes",
                   u.created_at AS "registrationDate",
                   u.last_login_date AS "lastLoginDate"
            FROM users u
            LEFT JOIN user_roles ur ON ur.user_id = u.id
            LEFT JOIN roles r ON r.id = ur.role_id
            WHERE u.deleted_at IS NULL
            GROUP BY u.id, u.email, u.full_name, u.username, u.status, u.created_at, u.last_login_date
            """,
            countQuery = "SELECT count(*) FROM users u WHERE u.deleted_at IS NULL",
            nativeQuery = true)
    Page<UserSummaryProjection> findSummaryByDeletedAtIsNull(Pageable pageable);

    @EntityGraph(attributePaths = "roles")
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND " +
           "(LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            @Param("search") String search, Pageable pageable);

    @Query(value = """
            SELECT u.id AS id,
                   u.email AS email,
                   u.full_name AS "fullName",
                   u.username AS username,
                   u.status AS status,
                   COALESCE(string_agg(DISTINCT r.code, ','), '') AS "roleCodes",
                   u.created_at AS "registrationDate",
                   u.last_login_date AS "lastLoginDate"
            FROM users u
            LEFT JOIN user_roles ur ON ur.user_id = u.id
            LEFT JOIN roles r ON r.id = ur.role_id
            WHERE u.deleted_at IS NULL
              AND (LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :search, '%')))
            GROUP BY u.id, u.email, u.full_name, u.username, u.status, u.created_at, u.last_login_date
            """,
            countQuery = """
                    SELECT count(*)
                    FROM users u
                    WHERE u.deleted_at IS NULL
                      AND (LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                           OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :search, '%')))
                    """,
            nativeQuery = true)
    Page<UserSummaryProjection> searchSummary(
            @Param("search") String search,
            Pageable pageable);
}
