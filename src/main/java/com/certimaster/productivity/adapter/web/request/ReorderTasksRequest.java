package com.certimaster.productivity.adapter.web.request;

import java.util.List;

/** Drag-and-drop result: the new column + position for each affected task. */
public class ReorderTasksRequest {

    private List<Item> items;

    public ReorderTasksRequest() {
    }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public static class Item {
        private Long id;
        private String status;
        private Integer orderIndex;

        public Item() {
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Integer getOrderIndex() { return orderIndex; }
        public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    }
}
