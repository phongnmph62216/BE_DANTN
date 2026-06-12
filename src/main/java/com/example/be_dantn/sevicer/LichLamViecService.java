package com.example.be_dantn.sevicer;


import com.example.be_dantn.Dto.LichLamViecRequest;
import com.example.be_dantn.Dto.LichLamViecResponse;

import java.util.List;

public interface LichLamViecService {

    List<LichLamViecResponse> findAll();

    LichLamViecResponse findById(long id);

    LichLamViecResponse add(LichLamViecRequest request);

    LichLamViecResponse update( LichLamViecRequest request,long id);

    LichLamViecResponse doiTrangThai(long id);
}