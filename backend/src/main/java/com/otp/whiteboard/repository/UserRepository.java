package com.otp.whiteboard.repository;

import com.otp.whiteboard.model.User;
import com.otp.whiteboard.enums.Status;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
    * Finds a user by their unique email.
    *
    * @param email the email of the user.
    * @return an {@link Optional} containing the user if found, or empty if not.
    */
    @Nonnull
    Optional<User> findByEmail(@Nonnull String email);

    /**
     * Checks if a user with the given email address exists.
     *
     * @param email the email address to check.
     * @return {@code true} if a user with the email address exists, {@code false} otherwise.
     */
    boolean existsByEmail(@Nonnull String email);

    /**
     * Finds users by their status.
     *
     * @param status the status to search for.
     * @return a list of users with the specified status.
     */
    List<User> findByStatus(@Nonnull Status status);


    /**
     * Finds a user by their display name.
     *
     * @param displayName the display name of the user.
     * @return an {@link Optional} containing the user if found, or empty if not.
     */
    @Nonnull
    Optional<User> findUserByDisplayName(@Nonnull String displayName);
}
