package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Response.AllProductAttributesDTO;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.sevicer.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attributes")
@RequiredArgsConstructor
public class ProductAttributeController {

    private final ProductAttributeService productAttributeService;

    @GetMapping("/all-active")
    public ResponseEntity<ResponseObject<AllProductAttributesDTO>> getAllActiveAttributes() {
        AllProductAttributesDTO data = productAttributeService.getAllActiveAttributes();
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy dữ liệu thuộc tính thành công", data));
    }
}
