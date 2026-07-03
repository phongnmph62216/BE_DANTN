package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.TayAoDTO;
import com.example.be_dantn.Entity.TayAo;
import com.example.be_dantn.Repository.TayAoRepository;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.sevicer.TayAoService;
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
public class TayAoServiceImpl implements TayAoService {

	private static final String MA_PREFIX = "TTTA";

	private final TayAoRepository tayAoRepository;
	private final com.example.be_dantn.Config.CodeGenerator codeGenerator;

	@Override
	public Page<TayAoDTO> findAll(Pageable pageable, String keyword, Integer trangThai) {
		Specification<TayAo> specification = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (StringUtils.hasText(keyword)) {
				String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
				Predicate keywordPredicate = criteriaBuilder.or(
						criteriaBuilder.like(criteriaBuilder.lower(root.get("maTayAo")), likeKeyword),
						criteriaBuilder.like(criteriaBuilder.lower(root.get("tenTayAo")), likeKeyword)
				);
				predicates.add(keywordPredicate);
			}

			if (trangThai != null) {
				predicates.add(criteriaBuilder.equal(root.get("trangThai"), trangThai));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

		return tayAoRepository.findAll(specification, pageable).map(this::toDTO);
	}

	@Override
	public TayAoDTO findById(Long id) {
		TayAo entity = tayAoRepository.findById(id)
				.orElseThrow(() -> new CustomResourceotFoundException("TayAo not found with id: " + id));
		return toDTO(entity);
	}

	@Override
	public TayAoDTO save(TayAoDTO tayAoDTO) {
		TayAo entity = new TayAo();
		applyUpsertFields(entity, tayAoDTO, true);
		entity.setMaTayAo(resolveMaForCreate(tayAoDTO.getMa()));
		entity.setNguoiTao(resolveNguoi(tayAoDTO.getNguoiTao()));
		entity.setNguoiSua(resolveNguoi(tayAoDTO.getNguoiSua()));
		entity.setTrangThai(tayAoDTO.getTrangThai() != null ? tayAoDTO.getTrangThai() : 1);

		return toDTO(tayAoRepository.save(entity));
	}

	@Override
	public TayAoDTO update(Long id, TayAoDTO tayAoDTO) {
		TayAo entity = tayAoRepository.findById(id)
				.orElseThrow(() -> new CustomResourceotFoundException("TayAo not found with id: " + id));

		String updatedMa = StringUtils.hasText(tayAoDTO.getMa()) ? tayAoDTO.getMa().trim() : entity.getMaTayAo();
		if (!updatedMa.equals(entity.getMaTayAo()) && tayAoRepository.existsByMaTayAoAndIdNot(updatedMa, id)) {
			throw new IllegalArgumentException("Ma tay ao da ton tai: " + updatedMa);
		}

		entity.setMaTayAo(updatedMa);
		applyUpsertFields(entity, tayAoDTO, false);

		if (StringUtils.hasText(tayAoDTO.getNguoiSua())) {
			entity.setNguoiSua(tayAoDTO.getNguoiSua().trim());
		}
		if (tayAoDTO.getTrangThai() != null) {
			entity.setTrangThai(tayAoDTO.getTrangThai());
		}

		entity.setNgaySua(LocalDateTime.now());
		return toDTO(tayAoRepository.save(entity));
	}

	@Override
	public TayAoDTO toggleStatus(Long id) {
		TayAo entity = tayAoRepository.findById(id)
				.orElseThrow(() -> new CustomResourceotFoundException("TayAo not found with id: " + id));

		entity.setTrangThai(entity.getTrangThai() != null && entity.getTrangThai() == 1 ? 0 : 1);
		entity.setNgaySua(LocalDateTime.now());
		return toDTO(tayAoRepository.save(entity));
	}

	private void applyUpsertFields(TayAo entity, TayAoDTO dto, boolean isCreate) {
		if (StringUtils.hasText(dto.getTen())) {
			entity.setTenTayAo(dto.getTen().trim());
		} else if (isCreate) {
			throw new IllegalArgumentException("Ten tay ao khong duoc de trong");
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
			if (tayAoRepository.existsByMaTayAo(normalizedMa)) {
				throw new IllegalArgumentException("Ma tay ao da ton tai: " + normalizedMa);
			}
			return normalizedMa;
		}
		return codeGenerator.generateCode("tay_ao", "ma_tay_ao", "TA");
	}

	private String resolveNguoi(String nguoi) {
		return StringUtils.hasText(nguoi) ? nguoi.trim() : "system";
	}

	private TayAoDTO toDTO(TayAo entity) {
		return TayAoDTO.builder()
				.id(entity.getId())
				.ma(entity.getMaTayAo())
				.ten(entity.getTenTayAo())
				.trangThai(entity.getTrangThai())
				.ngayTao(entity.getNgayTao())
				.ngaySua(entity.getNgaySua())
				.nguoiTao(entity.getNguoiTao())
				.nguoiSua(entity.getNguoiSua())
				.build();
	}
}

