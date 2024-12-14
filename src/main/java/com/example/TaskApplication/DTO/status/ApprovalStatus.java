package com.example.TaskApplication.DTO.status;

public enum ApprovalStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected");

    private String approvalStatus;

    ApprovalStatus(String status) {
        this.approvalStatus = status;
    }

    @Override
    public String toString() {
        return approvalStatus;
    }
}
