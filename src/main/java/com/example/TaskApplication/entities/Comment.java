package com.example.TaskApplication.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "comment",
        indexes = {@Index(name = "task_id_comment_id_idx", columnList = "id, taskId", unique = true)})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "id"))
    private Task task;

    private String content;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "id"))
    private User user;

    @CreationTimestamp
    private Timestamp createdDate;

    public Comment(long taskId, String content, long userId) {
        this.task = Task.builder().id(taskId).build();
        this.user = User.builder().id(userId).build();
        this.content = content;
    }

}
