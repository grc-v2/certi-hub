package com.certimaster.identity.adapter.web.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class BulkUserRequest {

    @NotNull(message = "User IDs list is required")
    @Size(min = 1, max = 500, message = "User IDs list must contain between 1 and 500 items")
    private List<Long> userIds;

    public BulkUserRequest() {
    }

    public BulkUserRequest(List<Long> userIds) {
        this.userIds = userIds;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
