package com.example.be_dantn.Dto.Response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AllProductAttributesDTO {
    private List<AttributeSelectionDTO> thuongHieuList;
    private List<AttributeSelectionDTO> chatLieuList;
    private List<AttributeSelectionDTO> xuatSuList;
    private List<AttributeSelectionDTO> kieuDangList;
    private List<AttributeSelectionDTO> loaiSanPhamList;
    private List<AttributeSelectionDTO> coAoList;
    private List<AttributeSelectionDTO> tayAoList;
    private List<AttributeSelectionDTO> vaiAoList;
    private List<AttributeSelectionDTO> mauSacList;
    private List<AttributeSelectionDTO> kichThuocList;
}
