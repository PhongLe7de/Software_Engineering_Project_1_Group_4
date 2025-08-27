package com.otp.whiteboard.repository;


import com.otp.whiteboard.model.User;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
     * Finds a user by their Firebase UID.
     *
     * @param uid the Firebase UID of the user.
     * @return an {@link Optional} containing the user if found, or empty if not.
     */
    @Nonnull
    Optional<User> findByUid(@Nonnull String uid);

    /**
     * Checks if a user with the given Firebase UID exists.
     *
     * @param uid the Firebase UID to check.
     * @return {@code true} if a user with the Firebase UID exists, {@code false} otherwise.
     */
    boolean existsByUid(@Nonnull String uid);
}
