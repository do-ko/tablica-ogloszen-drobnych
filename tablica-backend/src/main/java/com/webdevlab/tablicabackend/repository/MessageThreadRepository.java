package com.webdevlab.tablicabackend.repository;

import com.webdevlab.tablicabackend.entity.message.MessageThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageThreadRepository extends JpaRepository<MessageThread, String> {

    @Query("SELECT mt FROM MessageThread mt WHERE :userId MEMBER OF mt.participants ORDER BY mt.updatedAt DESC")
    List<MessageThread> findThreadsByUserId(String userId);
}