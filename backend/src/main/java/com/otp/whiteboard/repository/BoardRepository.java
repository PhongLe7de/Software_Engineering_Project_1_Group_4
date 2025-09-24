package com.otp.whiteboard.repository;

import com.otp.whiteboard.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository  extends JpaRepository<Board, Long> {
}
