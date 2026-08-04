package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.ChatLieuDTO;
import com.example.be_dantn.sevicer.ChatLieuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat-lieu")
public class ChatLieuController {

    private final ChatLieuService chatLieuService;

    @GetMapping
    public ResponseEntity<Page<ChatLieuDTO>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai) {
        return ResponseEntity.ok(chatLieuService.findAll(pageable, keyword, trangThai));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatLieuDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(chatLieuService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ChatLieuDTO> add(@Valid @RequestBody ChatLieuDTO chatLieuDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatLieuService.save(chatLieuDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChatLieuDTO> update(@PathVariable Long id, @Valid @RequestBody ChatLieuDTO chatLieuDTO) {
        return ResponseEntity.ok(chatLieuService.update(id, chatLieuDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ChatLieuDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(chatLieuService.toggleStatus(id));
    }
}

