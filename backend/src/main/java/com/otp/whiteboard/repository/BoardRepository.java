package com.otp.whiteboard.repository;

import com.otp.whiteboard.model.Board;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository  extends JpaRepository<Board, Long> {
    /**
     * Finds a board by its name.
     *
     * @param name the name of the board.
     * @return an {@link Optional} containing the board if found, or empty if not.
     */
    @Nonnull Optional<Board> findBoardsByName(String name);

}
