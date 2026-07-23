package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Response.AllProductAttributesDTO;
import com.example.be_dantn.Dto.Response.AttributeSelectionDTO;
import com.example.be_dantn.Repository.*;
import com.example.be_dantn.sevicer.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ThuongHieuRepository thuongHieuRepository;
    private final ChatLieuRepository chatLieuRepository;
    private final XuatSuRepository xuatSuRepository;
    private final KieuDangRepository kieuDangRepository;
    private final LoaiSanPhamRepository loaiSanPhamRepository;
    private final CoAoRepository coAoRepository;
    private final TayAoRepository tayAoRepository;
    private final VaiAoRepository vaiAoRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;

    @Override
    public AllProductAttributesDTO getAllActiveAttributes() {
        return AllProductAttributesDTO.builder()
                .thuongHieuList(thuongHieuRepository.findAllByTrangThai(1).stream().map(th -> new AttributeSelectionDTO(th.getId(), th.getTenThuongHieu())).collect(Collectors.toList()))
                .chatLieuList(chatLieuRepository.findAllByTrangThai(1).stream().map(cl -> new AttributeSelectionDTO(cl.getId(), cl.getTenChatLieu())).collect(Collectors.toList()))
                .xuatSuList(xuatSuRepository.findAllByTrangThai(1).stream().map(xs -> new AttributeSelectionDTO(xs.getId(), xs.getTenXuatSu())).collect(Collectors.toList()))
                .kieuDangList(kieuDangRepository.findAllByTrangThai(1).stream().map(kd -> new AttributeSelectionDTO(kd.getId(), kd.getTenKieuDang())).collect(Collectors.toList()))
                .loaiSanPhamList(loaiSanPhamRepository.findAllByTrangThai(1).stream().map(lsp -> new AttributeSelectionDTO(lsp.getId(), lsp.getTenLoaiSanPham())).collect(Collectors.toList()))
                .coAoList(coAoRepository.findAllByTrangThai(1).stream().map(ca -> new AttributeSelectionDTO(ca.getId(), ca.getTenCoAo())).collect(Collectors.toList()))
                .tayAoList(tayAoRepository.findAllByTrangThai(1).stream().map(ta -> new AttributeSelectionDTO(ta.getId(), ta.getTenTayAo())).collect(Collectors.toList()))
                .vaiAoList(vaiAoRepository.findAllByTrangThai(1).stream().map(va -> new AttributeSelectionDTO(va.getId(), va.getTenVaiAo())).collect(Collectors.toList()))
                .mauSacList(mauSacRepository.findAllByTrangThai(1).stream().map(ms -> new AttributeSelectionDTO(ms.getId(), ms.getTenMauSac())).collect(Collectors.toList()))
                .kichThuocList(kichThuocRepository.findAllByTrangThai(1).stream().map(kt -> new AttributeSelectionDTO(kt.getId(), kt.getTenKichThuoc())).collect(Collectors.toList()))
                .build();
    }
}
