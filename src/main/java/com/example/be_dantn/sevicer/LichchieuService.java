package com.example.be_dantn.sevicer;


import com.example.be_dantn.Dto.LichchieuRepose;
import com.example.be_dantn.Dto.LichchieuRequest;

import java.util.List;

public interface LichchieuService {

    List<LichchieuRepose> findAll();


    List<LichchieuRepose> searchLichchieu(String keyword);

    // Code moi: sap xep danh sach nhan vien theo luong giam dan.
    List<LichchieuRepose> findAllDesc();

    LichchieuRepose findById(Long id);


    LichchieuRepose save(LichchieuRequest lichchieuRequest);

    LichchieuRepose update(LichchieuRequest lichchieuRequest, Long id);

    void deleteById(Long id);

}
