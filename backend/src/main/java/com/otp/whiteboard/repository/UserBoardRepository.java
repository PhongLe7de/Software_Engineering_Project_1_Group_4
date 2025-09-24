package com.otp.whiteboard.repository;

import com.otp.whiteboard.model.UserBoard;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBoardRepository extends JpaRepository<UserBoard, Long> {
    @Nonnull
    UserBoard findUserBoardByBoardIdAndUserId(@Nonnull Long boardId, @Nonnull Long userId);
}
