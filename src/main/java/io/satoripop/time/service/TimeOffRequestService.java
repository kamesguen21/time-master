package io.satoripop.time.service;

import io.satoripop.time.service.dto.TimeOffRequestDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.satoripop.time.domain.TimeOffRequest}.
 */
public interface TimeOffRequestService {
    /**
     * Save a timeOffRequest.
     *
     * @param timeOffRequestDTO the entity to save.
     * @return the persisted entity.
     */
    TimeOffRequestDTO save(TimeOffRequestDTO timeOffRequestDTO);

    /**
     * Updates a timeOffRequest.
     *
     * @param timeOffRequestDTO the entity to update.
     * @return the persisted entity.
     */
    TimeOffRequestDTO update(TimeOffRequestDTO timeOffRequestDTO);

    /**
     * Partially updates a timeOffRequest.
     *
     * @param timeOffRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TimeOffRequestDTO> partialUpdate(TimeOffRequestDTO timeOffRequestDTO);

    /**
     * Get all the timeOffRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TimeOffRequestDTO> findAll(Pageable pageable);

    /**
     * Get the "id" timeOffRequest.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TimeOffRequestDTO> findOne(Long id);

    /**
     * Delete the "id" timeOffRequest.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
