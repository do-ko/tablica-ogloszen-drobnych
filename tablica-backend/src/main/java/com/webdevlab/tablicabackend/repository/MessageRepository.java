package com.webdevlab.tablicabackend.repository;

import com.webdevlab.tablicabackend.entity.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
}