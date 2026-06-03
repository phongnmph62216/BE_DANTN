package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.ChatLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChatLieuRepository extends JpaRepository<ChatLieu, Long>, JpaSpecificationExecutor<ChatLieu> {

    boolean existsByMaChatLieu(String maChatLieu);

    boolean existsByMaChatLieuAndIdNot(String maChatLieu, Long id);
}

