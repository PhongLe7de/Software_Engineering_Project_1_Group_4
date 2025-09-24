package com.otp.whiteboard.repository;

import com.otp.whiteboard.model.Stroke;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrokeRepository extends JpaRepository<Stroke, Long> {
}
