package com.TODOsAPI.Model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepo extends JpaRepository<TodoNote, Long> {
}
