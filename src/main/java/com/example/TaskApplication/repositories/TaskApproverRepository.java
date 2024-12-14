package com.example.TaskApplication.repositories;

import com.example.TaskApplication.entities.TaskApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskApproverRepository extends JpaRepository<TaskApproval, Long> {

    Optional<TaskApproval> findByTaskIdAndApproverId(long taskId, long approverId);

    List<TaskApproval> findAllByTaskId(long taskId);
}
