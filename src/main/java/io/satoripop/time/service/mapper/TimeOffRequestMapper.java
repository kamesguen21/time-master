package io.satoripop.time.service.mapper;

import io.satoripop.time.domain.TimeOffRequest;
import io.satoripop.time.service.dto.TimeOffRequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TimeOffRequest} and its DTO {@link TimeOffRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface TimeOffRequestMapper extends EntityMapper<TimeOffRequestDTO, TimeOffRequest> {}
