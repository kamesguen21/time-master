package io.satoripop.time.service.impl;

import io.satoripop.time.domain.TimeOffRequest;
import io.satoripop.time.repository.TimeOffRequestRepository;
import io.satoripop.time.service.TimeOffRequestService;
import io.satoripop.time.service.UserService;
import io.satoripop.time.service.dto.TimeOffRequestDTO;
import io.satoripop.time.service.dto.UserDTO;
import io.satoripop.time.service.mapper.TimeOffRequestMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TimeOffRequest}.
 */
@Service
@Transactional
public class TimeOffRequestServiceImpl implements TimeOffRequestService {

    private final Logger log = LoggerFactory.getLogger(TimeOffRequestServiceImpl.class);

    private final TimeOffRequestRepository timeOffRequestRepository;

    private final TimeOffRequestMapper timeOffRequestMapper;
    private final UserService userService;

    public TimeOffRequestServiceImpl(
        TimeOffRequestRepository timeOffRequestRepository,
        TimeOffRequestMapper timeOffRequestMapper,
        UserService userService
    ) {
        this.timeOffRequestRepository = timeOffRequestRepository;
        this.timeOffRequestMapper = timeOffRequestMapper;
        this.userService = userService;
    }

    @Override
    public TimeOffRequestDTO save(TimeOffRequestDTO timeOffRequestDTO) {
        log.debug("Request to save TimeOffRequest : {}", timeOffRequestDTO);
        TimeOffRequest timeOffRequest = timeOffRequestMapper.toEntity(timeOffRequestDTO);
        if (timeOffRequest.getUserId() != null) {
            Optional<UserDTO> userById = userService.getUserById(timeOffRequest.getUserId());
            if (userById.isPresent()) {
                timeOffRequest.setUserName(userById.get().getLogin());
            }
        }
        timeOffRequest = timeOffRequestRepository.save(timeOffRequest);
        return timeOffRequestMapper.toDto(timeOffRequest);
    }

    @Override
    public TimeOffRequestDTO update(TimeOffRequestDTO timeOffRequestDTO) {
        log.debug("Request to update TimeOffRequest : {}", timeOffRequestDTO);
        TimeOffRequest timeOffRequest = timeOffRequestMapper.toEntity(timeOffRequestDTO);
        if (timeOffRequest.getUserId() != null) {
            Optional<UserDTO> userById = userService.getUserById(timeOffRequest.getUserId());
            if (userById.isPresent()) {
                timeOffRequest.setUserName(userById.get().getLogin());
            }
        }
        timeOffRequest = timeOffRequestRepository.save(timeOffRequest);
        return timeOffRequestMapper.toDto(timeOffRequest);
    }

    @Override
    public Optional<TimeOffRequestDTO> partialUpdate(TimeOffRequestDTO timeOffRequestDTO) {
        log.debug("Request to partially update TimeOffRequest : {}", timeOffRequestDTO);

        return timeOffRequestRepository
            .findById(timeOffRequestDTO.getId())
            .map(existingTimeOffRequest -> {
                timeOffRequestMapper.partialUpdate(existingTimeOffRequest, timeOffRequestDTO);

                return existingTimeOffRequest;
            })
            .map(timeOffRequestRepository::save)
            .map(timeOffRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TimeOffRequestDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TimeOffRequests");
        return timeOffRequestRepository.findAll(pageable).map(timeOffRequestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TimeOffRequestDTO> findOne(Long id) {
        log.debug("Request to get TimeOffRequest : {}", id);
        return timeOffRequestRepository.findById(id).map(timeOffRequestMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TimeOffRequest : {}", id);
        timeOffRequestRepository.deleteById(id);
    }
}
