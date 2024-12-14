package com.example.TaskApplication.entities;

import com.example.TaskApplication.DTO.status.ApprovalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "taskApproval",
        indexes = {@Index(name = "task_id_approver_id_idx", columnList = "id, taskId", unique = true)})
public class TaskApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "id"))
    private Task task;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "id"))
    private User approver;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    public TaskApproval(long taskId, long approverId, ApprovalStatus status) {
        this.task = Task.builder().id(taskId).build();
        this.approver = User.builder().id(approverId).build();
        this.status = status;
    }
}
