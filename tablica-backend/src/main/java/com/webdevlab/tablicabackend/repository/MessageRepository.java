package com.webdevlab.tablicabackend.repository;

import com.webdevlab.tablicabackend.entity.message.Message;
import com.webdevlab.tablicabackend.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    Page<Message> findByReceiverOrderBySentAtDesc(User receiver, Pageable pageable);

    Page<Message> findBySenderOrderBySentAtDesc(User sender, Pageable pageable);
}
