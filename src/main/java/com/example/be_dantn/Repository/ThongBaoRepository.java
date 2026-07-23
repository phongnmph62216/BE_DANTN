package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {
    List<ThongBao> findAllByOrderByNgayTaoDesc();
    long countByTrangThai(Integer trangThai);
}
