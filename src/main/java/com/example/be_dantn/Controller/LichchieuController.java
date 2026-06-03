package com.example.be_dantn.Controller;


import com.example.be_dantn.Dto.LichchieuRepose;
import com.example.be_dantn.Dto.LichchieuRequest;
import com.example.be_dantn.sevicer.LichchieuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/lichchieu")
public class LichchieuController {


    private final LichchieuService lichchieuService;

    @GetMapping
    public ResponseEntity<List<LichchieuRepose>> findAll() {
        return ResponseEntity.ok(lichchieuService.findAll());
    }


    @GetMapping("/search")
    public ResponseEntity<List<LichchieuRepose>> search(@RequestParam("name") String name) {
        return ResponseEntity.ok(lichchieuService.searchLichchieu(name));
    }


    @GetMapping("/sort-desc")
    public ResponseEntity<List<LichchieuRepose>> findAllSortDesc() {
        return ResponseEntity.ok(lichchieuService.findAllDesc());
    }


    @GetMapping("/{id}")
    public ResponseEntity<LichchieuRepose> findById(@PathVariable Long id) {
        return ResponseEntity.ok(lichchieuService.findById(id));
    }


    @PostMapping
    public ResponseEntity<LichchieuRepose> add(@Valid @RequestBody LichchieuRequest lichchieuRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lichchieuService.save(lichchieuRequest));
    }


    @PutMapping("/{id}")
    public ResponseEntity<LichchieuRepose> update(@Valid @RequestBody LichchieuRequest lichchieuRequest, @PathVariable Long id) {
        return ResponseEntity.ok(lichchieuService.update(lichchieuRequest, id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lichchieuService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
