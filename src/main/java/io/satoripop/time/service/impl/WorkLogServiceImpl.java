package io.satoripop.time.service.impl;

import io.satoripop.time.domain.WorkLog;
import io.satoripop.time.repository.WorkLogRepository;
import io.satoripop.time.service.UserService;
import io.satoripop.time.service.WorkLogService;
import io.satoripop.time.service.dto.UserDTO;
import io.satoripop.time.service.dto.WorkLogDTO;
import io.satoripop.time.service.mapper.WorkLogMapper;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link WorkLog}.
 */
@Service
@Transactional
public class WorkLogServiceImpl implements WorkLogService {

    private final Logger log = LoggerFactory.getLogger(WorkLogServiceImpl.class);

    private final WorkLogRepository workLogRepository;

    private final WorkLogMapper workLogMapper;
    private final UserService userService;

    public WorkLogServiceImpl(WorkLogRepository workLogRepository, WorkLogMapper workLogMapper, UserService userService) {
        this.workLogRepository = workLogRepository;
        this.workLogMapper = workLogMapper;
        this.userService = userService;
    }

    @Override
    public WorkLogDTO save(WorkLogDTO workLogDTO) {
        log.debug("Request to save WorkLog : {}", workLogDTO);
        WorkLog workLog = workLogMapper.toEntity(workLogDTO);
        if (workLog.getUserId() != null) {
            Optional<UserDTO> userById = userService.getUserById(workLog.getUserId());
            if (userById.isPresent()) {
                workLog.setUserName(userById.get().getLogin());
            }
        }
        workLog = workLogRepository.save(workLog);
        return workLogMapper.toDto(workLog);
    }

    @Override
    public WorkLogDTO update(WorkLogDTO workLogDTO) {
        log.debug("Request to update WorkLog : {}", workLogDTO);
        WorkLog workLog = workLogMapper.toEntity(workLogDTO);
        if (workLog.getUserId() != null) {
            Optional<UserDTO> userById = userService.getUserById(workLog.getUserId());
            if (userById.isPresent()) {
                workLog.setUserName(userById.get().getLogin());
            }
        }
        workLog = workLogRepository.save(workLog);
        return workLogMapper.toDto(workLog);
    }

    @Override
    public Optional<WorkLogDTO> partialUpdate(WorkLogDTO workLogDTO) {
        log.debug("Request to partially update WorkLog : {}", workLogDTO);

        return workLogRepository.findById(workLogDTO.getId()).map(existingWorkLog -> {
            workLogMapper.partialUpdate(existingWorkLog, workLogDTO);

            return existingWorkLog;
        }).map(workLogRepository::save).map(workLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkLogDTO> findAll(Pageable pageable) {
        log.debug("Request to get all WorkLogs");
        return workLogRepository.findAll(pageable).map(workLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkLogDTO> findOne(Long id) {
        log.debug("Request to get WorkLog : {}", id);
        return workLogRepository.findById(id).map(workLogMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete WorkLog : {}", id);
        workLogRepository.deleteById(id);
    }
}
