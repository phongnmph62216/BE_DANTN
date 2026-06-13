package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.ChatLieuDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatLieuService {

    Page<ChatLieuDTO> findAll(Pageable pageable, String keyword, Integer trangThai);

    ChatLieuDTO findById(Long id);

    ChatLieuDTO save(ChatLieuDTO chatLieuDTO);

    ChatLieuDTO update(Long id, ChatLieuDTO chatLieuDTO);

    ChatLieuDTO toggleStatus(Long id);
}

