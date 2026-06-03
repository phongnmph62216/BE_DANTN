package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.ChatLieuDTO;
import com.example.be_dantn.Entity.ChatLieu;
import com.example.be_dantn.Repository.ChatLieuRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.ChatLieuService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ChatLieuServiceImpl implements ChatLieuService {

    private static final String MA_PREFIX = "TTCL";

    private final ChatLieuRepository chatLieuRepository;

    @Override
    public Page<ChatLieuDTO> findAll(Pageable pageable, String keyword, Integer trangThai) {
        Specification<ChatLieu> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("maChatLieu")), likeKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenChatLieu")), likeKeyword)
                );
                predicates.add(keywordPredicate);
            }

            if (trangThai != null) {
                predicates.add(criteriaBuilder.equal(root.get("trangThai"), trangThai));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return chatLieuRepository.findAll(specification, pageable).map(this::toDTO);
    }

    @Override
    public ChatLieuDTO findById(Long id) {
        ChatLieu entity = chatLieuRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("ChatLieu not found with id: " + id));
        return toDTO(entity);
    }

    @Override
    public ChatLieuDTO save(ChatLieuDTO chatLieuDTO) {
        ChatLieu entity = new ChatLieu();
        applyUpsertFields(entity, chatLieuDTO, true);
        entity.setMaChatLieu(resolveMaForCreate(chatLieuDTO.getMa()));
        entity.setNguoiTao(resolveNguoi(chatLieuDTO.getNguoiTao()));
        entity.setNguoiSua(resolveNguoi(chatLieuDTO.getNguoiSua()));
        entity.setTrangThai(chatLieuDTO.getTrangThai() != null ? chatLieuDTO.getTrangThai() : 1);

        return toDTO(chatLieuRepository.save(entity));
    }

    @Override
    public ChatLieuDTO update(Long id, ChatLieuDTO chatLieuDTO) {
        ChatLieu entity = chatLieuRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("ChatLieu not found with id: " + id));

        String updatedMa = StringUtils.hasText(chatLieuDTO.getMa()) ? chatLieuDTO.getMa().trim() : entity.getMaChatLieu();
        if (!updatedMa.equals(entity.getMaChatLieu()) && chatLieuRepository.existsByMaChatLieuAndIdNot(updatedMa, id)) {
            throw new IllegalArgumentException("Ma chat lieu da ton tai: " + updatedMa);
        }

        entity.setMaChatLieu(updatedMa);
        applyUpsertFields(entity, chatLieuDTO, false);

        if (StringUtils.hasText(chatLieuDTO.getNguoiSua())) {
            entity.setNguoiSua(chatLieuDTO.getNguoiSua().trim());
        }
        if (chatLieuDTO.getTrangThai() != null) {
            entity.setTrangThai(chatLieuDTO.getTrangThai());
        }

        entity.setNgaySua(LocalDateTime.now());
        return toDTO(chatLieuRepository.save(entity));
    }

    @Override
    public ChatLieuDTO toggleStatus(Long id) {
        ChatLieu entity = chatLieuRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("ChatLieu not found with id: " + id));

        entity.setTrangThai(entity.getTrangThai() != null && entity.getTrangThai() == 1 ? 0 : 1);
        entity.setNgaySua(LocalDateTime.now());
        return toDTO(chatLieuRepository.save(entity));
    }

    private void applyUpsertFields(ChatLieu entity, ChatLieuDTO dto, boolean isCreate) {
        if (StringUtils.hasText(dto.getTen())) {
            entity.setTenChatLieu(dto.getTen().trim());
        } else if (isCreate) {
            throw new IllegalArgumentException("Ten chat lieu khong duoc de trong");
        }

        if (StringUtils.hasText(dto.getNguoiTao()) && isCreate) {
            entity.setNguoiTao(dto.getNguoiTao().trim());
        }

        if (StringUtils.hasText(dto.getNguoiSua()) && !isCreate) {
            entity.setNguoiSua(dto.getNguoiSua().trim());
        }
    }

    private String resolveMaForCreate(String ma) {
        if (StringUtils.hasText(ma)) {
            String normalizedMa = ma.trim();
            if (chatLieuRepository.existsByMaChatLieu(normalizedMa)) {
                throw new IllegalArgumentException("Ma chat lieu da ton tai: " + normalizedMa);
            }
            return normalizedMa;
        }

        String generatedMa;
        do {
            generatedMa = MA_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        } while (chatLieuRepository.existsByMaChatLieu(generatedMa));

        return generatedMa;
    }

    private String resolveNguoi(String nguoi) {
        return StringUtils.hasText(nguoi) ? nguoi.trim() : "system";
    }

    private ChatLieuDTO toDTO(ChatLieu entity) {
        return ChatLieuDTO.builder()
                .id(entity.getId())
                .ma(entity.getMaChatLieu())
                .ten(entity.getTenChatLieu())
                .trangThai(entity.getTrangThai())
                .ngayTao(entity.getNgayTao())
                .ngaySua(entity.getNgaySua())
                .nguoiTao(entity.getNguoiTao())
                .nguoiSua(entity.getNguoiSua())
                .build();
    }
}

