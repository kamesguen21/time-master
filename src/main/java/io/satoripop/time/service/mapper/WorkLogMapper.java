package io.satoripop.time.service.mapper;

import io.satoripop.time.domain.Ticket;
import io.satoripop.time.domain.WorkLog;
import io.satoripop.time.service.dto.TicketDTO;
import io.satoripop.time.service.dto.WorkLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WorkLog} and its DTO {@link WorkLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkLogMapper extends EntityMapper<WorkLogDTO, WorkLog> {
    @Mapping(target = "ticket", source = "ticket", qualifiedByName = "ticketId")
    WorkLogDTO toDto(WorkLog s);

    @Named("ticketId")
    @BeanMapping(ignoreByDefault = false)
    @Mapping(target = "id", source = "id")
    TicketDTO toDtoTicketId(Ticket ticket);
}
