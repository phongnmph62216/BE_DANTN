package com.example.be_dantn.sevicer;



import com.example.be_dantn.Dto.CaLamViecRespon;
import com.example.be_dantn.Dto.CaLamViecRequest;

import java.util.List;

public interface CaLamViecService {

    List<CaLamViecRespon> findAll();

    CaLamViecRespon findById(long id);

    CaLamViecRespon add(CaLamViecRequest request);

    CaLamViecRespon update(CaLamViecRequest request, long id);

    CaLamViecRespon doiTrangThai(long id);
}
