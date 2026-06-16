package com.certimaster.productivity.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * A user-defined grouping for Resource Hub items (the successor to the old
 * "bookmark group"). Resources reference a collection by id; deleting a collection
 * detaches its resources rather than removing them.
 */
@Entity
@Table(name = "tracker_collections", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "name"})
})
public class TrackerCollection extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    public TrackerCollection() {
        super();
    }

    public TrackerCollection(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
