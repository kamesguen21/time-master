package io.satoripop.time.service;

import io.satoripop.time.service.dto.WorkLogDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.satoripop.time.domain.WorkLog}.
 */
public interface WorkLogService {
    /**
     * Save a workLog.
     *
     * @param workLogDTO the entity to save.
     * @return the persisted entity.
     */
    WorkLogDTO save(WorkLogDTO workLogDTO);

    /**
     * Updates a workLog.
     *
     * @param workLogDTO the entity to update.
     * @return the persisted entity.
     */
    WorkLogDTO update(WorkLogDTO workLogDTO);

    /**
     * Partially updates a workLog.
     *
     * @param workLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WorkLogDTO> partialUpdate(WorkLogDTO workLogDTO);

    /**
     * Get all the workLogs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<WorkLogDTO> findAll(Pageable pageable);

    /**
     * Get the "id" workLog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WorkLogDTO> findOne(Long id);

    /**
     * Delete the "id" workLog.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
