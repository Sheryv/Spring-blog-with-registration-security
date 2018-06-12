package com.sheryv.example.springblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sheryv.example.springblog.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Override
    void delete(Comment comment);

}
