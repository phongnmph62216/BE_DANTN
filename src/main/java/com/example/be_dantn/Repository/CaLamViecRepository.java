package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.CaLamViec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaLamViecRepository extends JpaRepository<CaLamViec, Long> {
    boolean existsByMaCa(String maCa);

}
