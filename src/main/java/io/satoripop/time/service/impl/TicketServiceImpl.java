package io.satoripop.time.service.impl;

import io.satoripop.time.domain.Ticket;
import io.satoripop.time.repository.TicketRepository;
import io.satoripop.time.repository.WorkLogRepository;
import io.satoripop.time.service.TicketService;
import io.satoripop.time.service.UserService;
import io.satoripop.time.service.dto.UserDTO;
import io.satoripop.time.service.mapper.TicketMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Ticket}.
 */
@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final TicketRepository ticketRepository;
    private final WorkLogRepository workLogRepository;
    private final UserService userService;

    private final TicketMapper ticketMapper;

    public TicketServiceImpl(
        TicketRepository ticketRepository,
        WorkLogRepository workLogRepository,
        UserService userService,
        TicketMapper ticketMapper
    ) {
        this.ticketRepository = ticketRepository;
        this.workLogRepository = workLogRepository;
        this.userService = userService;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public Ticket save(Ticket ticket) {
        log.debug("Request to save Ticket : {}", ticket);
        if (ticket.getUserId() != null) {
            Optional<UserDTO> userById = userService.getUserById(ticket.getUserId());
            if (userById.isPresent()) {
                ticket.setUserName(userById.get().getLogin());
            }
        }
        ticket = ticketRepository.save(ticket);
        return ticket;
    }

    @Override
    public Ticket update(Ticket ticket) {
        log.debug("Request to update Ticket : {}", ticket);
        if (ticket.getUserId() != null) {
            Optional<UserDTO> userById = userService.getUserById(ticket.getUserId());
            userById.ifPresent(userDTO -> ticket.setUserName(userDTO.getLogin()));
        }
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Ticket> findAll(Pageable pageable) {
        log.debug("Request to get all Tickets");
        return ticketRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> findOne(Long id) {
        log.debug("Request to get Ticket : {}", id);
        return ticketRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Ticket : {}", id);
        Optional<Ticket> byId = ticketRepository.findById(id);
        if (byId.isPresent()) {
            Ticket ticket = byId.get();
            workLogRepository.deleteAll(ticket.getWorkLogs());
        }
        ticketRepository.deleteById(id);
    }
}
