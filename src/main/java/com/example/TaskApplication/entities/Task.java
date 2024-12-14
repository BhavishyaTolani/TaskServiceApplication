package com.example.TaskApplication.entities;

import com.example.TaskApplication.DTO.status.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "id"))
    private User createdBy;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private String description;

    private int approvalCount;

    public Task(long createdBy, TaskStatus status, String description) {
        this.createdBy = User.builder().id(createdBy).build();
        this.status = status;
        this.description = description;
        this.approvalCount = 0;
    }
}
