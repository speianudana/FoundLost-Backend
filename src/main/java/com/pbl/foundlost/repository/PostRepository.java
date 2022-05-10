package com.pbl.foundlost.repository;

import com.pbl.foundlost.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long aLong);
    Optional<Post> findByUuid(UUID uuid);
}
