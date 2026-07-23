package com.example.be_dantn.Repository;

import com.example.be_dantn.Entity.TayAo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TayAoRepository extends JpaRepository<TayAo, Long>, JpaSpecificationExecutor<TayAo> {
    List<TayAo> findAllByTrangThai(Integer trangThai);

    boolean existsByMaTayAo(String maTayAo);

    boolean existsByMaTayAoAndIdNot(String maTayAo, Long id);
}
