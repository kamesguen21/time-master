package io.satoripop.time.web.rest;

import io.satoripop.time.repository.TimeOffRequestRepository;
import io.satoripop.time.service.TimeOffRequestQueryService;
import io.satoripop.time.service.TimeOffRequestService;
import io.satoripop.time.service.criteria.TimeOffRequestCriteria;
import io.satoripop.time.service.dto.TimeOffRequestDTO;
import io.satoripop.time.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.satoripop.time.domain.TimeOffRequest}.
 */
@RestController
@RequestMapping("/api")
public class TimeOffRequestResource {

    private final Logger log = LoggerFactory.getLogger(TimeOffRequestResource.class);

    private static final String ENTITY_NAME = "timeOffRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TimeOffRequestService timeOffRequestService;

    private final TimeOffRequestRepository timeOffRequestRepository;

    private final TimeOffRequestQueryService timeOffRequestQueryService;

    public TimeOffRequestResource(
        TimeOffRequestService timeOffRequestService,
        TimeOffRequestRepository timeOffRequestRepository,
        TimeOffRequestQueryService timeOffRequestQueryService
    ) {
        this.timeOffRequestService = timeOffRequestService;
        this.timeOffRequestRepository = timeOffRequestRepository;
        this.timeOffRequestQueryService = timeOffRequestQueryService;
    }

    /**
     * {@code POST  /time-off-requests} : Create a new timeOffRequest.
     *
     * @param timeOffRequestDTO the timeOffRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timeOffRequestDTO, or with status {@code 400 (Bad Request)} if the timeOffRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/time-off-requests")
    public ResponseEntity<TimeOffRequestDTO> createTimeOffRequest(@Valid @RequestBody TimeOffRequestDTO timeOffRequestDTO)
        throws URISyntaxException {
        log.debug("REST request to save TimeOffRequest : {}", timeOffRequestDTO);
        if (timeOffRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new timeOffRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TimeOffRequestDTO result = timeOffRequestService.save(timeOffRequestDTO);
        return ResponseEntity
            .created(new URI("/api/time-off-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /time-off-requests/:id} : Updates an existing timeOffRequest.
     *
     * @param id the id of the timeOffRequestDTO to save.
     * @param timeOffRequestDTO the timeOffRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timeOffRequestDTO,
     * or with status {@code 400 (Bad Request)} if the timeOffRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timeOffRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/time-off-requests/{id}")
    public ResponseEntity<TimeOffRequestDTO> updateTimeOffRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TimeOffRequestDTO timeOffRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TimeOffRequest : {}, {}", id, timeOffRequestDTO);
        if (timeOffRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timeOffRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timeOffRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TimeOffRequestDTO result = timeOffRequestService.update(timeOffRequestDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, timeOffRequestDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /time-off-requests/:id} : Partial updates given fields of an existing timeOffRequest, field will ignore if it is null
     *
     * @param id the id of the timeOffRequestDTO to save.
     * @param timeOffRequestDTO the timeOffRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timeOffRequestDTO,
     * or with status {@code 400 (Bad Request)} if the timeOffRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the timeOffRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the timeOffRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/time-off-requests/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TimeOffRequestDTO> partialUpdateTimeOffRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TimeOffRequestDTO timeOffRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TimeOffRequest partially : {}, {}", id, timeOffRequestDTO);
        if (timeOffRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timeOffRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timeOffRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TimeOffRequestDTO> result = timeOffRequestService.partialUpdate(timeOffRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, timeOffRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /time-off-requests} : get all the timeOffRequests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of timeOffRequests in body.
     */
    @GetMapping("/time-off-requests")
    public ResponseEntity<List<TimeOffRequestDTO>> getAllTimeOffRequests(
        TimeOffRequestCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TimeOffRequests by criteria: {}", criteria);
        Page<TimeOffRequestDTO> page = timeOffRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /time-off-requests/count} : count all the timeOffRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/time-off-requests/count")
    public ResponseEntity<Long> countTimeOffRequests(TimeOffRequestCriteria criteria) {
        log.debug("REST request to count TimeOffRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(timeOffRequestQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /time-off-requests/:id} : get the "id" timeOffRequest.
     *
     * @param id the id of the timeOffRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timeOffRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/time-off-requests/{id}")
    public ResponseEntity<TimeOffRequestDTO> getTimeOffRequest(@PathVariable Long id) {
        log.debug("REST request to get TimeOffRequest : {}", id);
        Optional<TimeOffRequestDTO> timeOffRequestDTO = timeOffRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(timeOffRequestDTO);
    }

    /**
     * {@code DELETE  /time-off-requests/:id} : delete the "id" timeOffRequest.
     *
     * @param id the id of the timeOffRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/time-off-requests/{id}")
    public ResponseEntity<Void> deleteTimeOffRequest(@PathVariable Long id) {
        log.debug("REST request to delete TimeOffRequest : {}", id);
        timeOffRequestService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
