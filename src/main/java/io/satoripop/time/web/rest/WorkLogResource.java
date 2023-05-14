package io.satoripop.time.web.rest;

import io.satoripop.time.repository.WorkLogRepository;
import io.satoripop.time.service.WorkLogQueryService;
import io.satoripop.time.service.WorkLogService;
import io.satoripop.time.service.criteria.WorkLogCriteria;
import io.satoripop.time.service.dto.WorkLogDTO;
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
 * REST controller for managing {@link io.satoripop.time.domain.WorkLog}.
 */
@RestController
@RequestMapping("/api")
public class WorkLogResource {

    private final Logger log = LoggerFactory.getLogger(WorkLogResource.class);

    private static final String ENTITY_NAME = "workLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkLogService workLogService;

    private final WorkLogRepository workLogRepository;

    private final WorkLogQueryService workLogQueryService;

    public WorkLogResource(WorkLogService workLogService, WorkLogRepository workLogRepository, WorkLogQueryService workLogQueryService) {
        this.workLogService = workLogService;
        this.workLogRepository = workLogRepository;
        this.workLogQueryService = workLogQueryService;
    }

    /**
     * {@code POST  /work-logs} : Create a new workLog.
     *
     * @param workLogDTO the workLogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workLogDTO, or with status {@code 400 (Bad Request)} if the workLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/work-logs")
    public ResponseEntity<WorkLogDTO> createWorkLog(@Valid @RequestBody WorkLogDTO workLogDTO) throws URISyntaxException {
        log.debug("REST request to save WorkLog : {}", workLogDTO);
        if (workLogDTO.getId() != null) {
            throw new BadRequestAlertException("A new workLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WorkLogDTO result = workLogService.save(workLogDTO);
        return ResponseEntity
            .created(new URI("/api/work-logs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /work-logs/:id} : Updates an existing workLog.
     *
     * @param id the id of the workLogDTO to save.
     * @param workLogDTO the workLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workLogDTO,
     * or with status {@code 400 (Bad Request)} if the workLogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/work-logs/{id}")
    public ResponseEntity<WorkLogDTO> updateWorkLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WorkLogDTO workLogDTO
    ) throws URISyntaxException {
        log.debug("REST request to update WorkLog : {}, {}", id, workLogDTO);
        if (workLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WorkLogDTO result = workLogService.update(workLogDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workLogDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /work-logs/:id} : Partial updates given fields of an existing workLog, field will ignore if it is null
     *
     * @param id the id of the workLogDTO to save.
     * @param workLogDTO the workLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workLogDTO,
     * or with status {@code 400 (Bad Request)} if the workLogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the workLogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the workLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/work-logs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WorkLogDTO> partialUpdateWorkLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WorkLogDTO workLogDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update WorkLog partially : {}, {}", id, workLogDTO);
        if (workLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WorkLogDTO> result = workLogService.partialUpdate(workLogDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workLogDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /work-logs} : get all the workLogs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workLogs in body.
     */
    @GetMapping("/work-logs")
    public ResponseEntity<List<WorkLogDTO>> getAllWorkLogs(
        WorkLogCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get WorkLogs by criteria: {}", criteria);
        Page<WorkLogDTO> page = workLogQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /work-logs/count} : count all the workLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/work-logs/count")
    public ResponseEntity<Long> countWorkLogs(WorkLogCriteria criteria) {
        log.debug("REST request to count WorkLogs by criteria: {}", criteria);
        return ResponseEntity.ok().body(workLogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /work-logs/:id} : get the "id" workLog.
     *
     * @param id the id of the workLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/work-logs/{id}")
    public ResponseEntity<WorkLogDTO> getWorkLog(@PathVariable Long id) {
        log.debug("REST request to get WorkLog : {}", id);
        Optional<WorkLogDTO> workLogDTO = workLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workLogDTO);
    }

    /**
     * {@code DELETE  /work-logs/:id} : delete the "id" workLog.
     *
     * @param id the id of the workLogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/work-logs/{id}")
    public ResponseEntity<Void> deleteWorkLog(@PathVariable Long id) {
        log.debug("REST request to delete WorkLog : {}", id);
        workLogService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
