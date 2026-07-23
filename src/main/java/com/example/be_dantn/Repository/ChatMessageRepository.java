package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatSessionSessionCodeOrderByNgayTaoAsc(String sessionCode);
}
