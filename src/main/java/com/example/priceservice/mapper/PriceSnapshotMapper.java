package com.example.priceservice.mapper;

import com.example.priceservice.dto.PriceToUserResponseDto;
import com.example.priceservice.dto.PaginatedPriceResponseDto;
import com.example.priceservice.entity.PriceSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceSnapshotMapper {

    PriceToUserResponseDto toResponseDto(PriceSnapshot entity);

    List<PriceToUserResponseDto> toResponseDtoList(List<PriceSnapshot> entities);

    @Mapping(source = "content", target = "items")
    @Mapping(source = "number", target = "page")
    PaginatedPriceResponseDto toPaginatedResponseDto(Page<PriceSnapshot> page);
}