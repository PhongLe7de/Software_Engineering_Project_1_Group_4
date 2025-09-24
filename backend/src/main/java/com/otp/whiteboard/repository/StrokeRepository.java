package com.otp.whiteboard.repository;

import com.otp.whiteboard.model.Stroke;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrokeRepository extends JpaRepository<Stroke, Long> {
    /**
     * Finds all strokes associated with a specific board ID.
     *
     * @param boardId the ID of the board.
     * @return a list of strokes associated with the given board ID.
     */
    public List<Stroke> findAllByBoardId(@NotNull Long boardId);
}
