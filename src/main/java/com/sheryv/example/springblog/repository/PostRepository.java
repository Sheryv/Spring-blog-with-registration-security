package com.sheryv.example.springblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sheryv.example.springblog.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Override
    void delete(Post post);

}
