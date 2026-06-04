package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.ChatLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatLieuRepository extends JpaRepository<ChatLieu, Long>, JpaSpecificationExecutor<ChatLieu> {
    List<ChatLieu> findAllByTrangThai(Integer trangThai);

    boolean existsByMaChatLieu(String maChatLieu);

    boolean existsByMaChatLieuAndIdNot(String maChatLieu, Long id);
}
