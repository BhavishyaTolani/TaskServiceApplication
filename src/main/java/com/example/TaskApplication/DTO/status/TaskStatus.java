package com.example.TaskApplication.DTO.status;

public enum TaskStatus {
    CREATED("created"),
    PARTIALLY_APPROVED("partiallyApproved"),
    APPROVED("approved"),
    REJECTED("rejected");

    private String taskStatus;

    TaskStatus(String status) {
        this.taskStatus = status;
    }

    @Override
    public String toString() {
        return taskStatus;
    }
}

