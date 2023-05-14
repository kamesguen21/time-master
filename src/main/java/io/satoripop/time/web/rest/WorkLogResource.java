package io.satoripop.time.web.rest;

import io.satoripop.time.domain.WorkLog;
import io.satoripop.time.repository.WorkLogRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.satoripop.time.domain.WorkLog}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class WorkLogResource {

    private final Logger log = LoggerFactory.getLogger(WorkLogResource.class);

    private static final String ENTITY_NAME = "workLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkLogRepository workLogRepository;

    public WorkLogResource(WorkLogRepository workLogRepository) {
        this.workLogRepository = workLogRepository;
    }

    /**
     * {@code POST  /work-logs} : Create a new workLog.
     *
     * @param workLog the workLog to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workLog, or with status {@code 400 (Bad Request)} if the workLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/work-logs")
    public ResponseEntity<WorkLog> createWorkLog(@Valid @RequestBody WorkLog workLog) throws URISyntaxException {
        log.debug("REST request to save WorkLog : {}", workLog);
        if (workLog.getId() != null) {
            throw new BadRequestAlertException("A new workLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WorkLog result = workLogRepository.save(workLog);
        return ResponseEntity
            .created(new URI("/api/work-logs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /work-logs/:id} : Updates an existing workLog.
     *
     * @param id the id of the workLog to save.
     * @param workLog the workLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workLog,
     * or with status {@code 400 (Bad Request)} if the workLog is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/work-logs/{id}")
    public ResponseEntity<WorkLog> updateWorkLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WorkLog workLog
    ) throws URISyntaxException {
        log.debug("REST request to update WorkLog : {}, {}", id, workLog);
        if (workLog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workLog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WorkLog result = workLogRepository.save(workLog);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workLog.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /work-logs/:id} : Partial updates given fields of an existing workLog, field will ignore if it is null
     *
     * @param id the id of the workLog to save.
     * @param workLog the workLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workLog,
     * or with status {@code 400 (Bad Request)} if the workLog is not valid,
     * or with status {@code 404 (Not Found)} if the workLog is not found,
     * or with status {@code 500 (Internal Server Error)} if the workLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/work-logs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WorkLog> partialUpdateWorkLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WorkLog workLog
    ) throws URISyntaxException {
        log.debug("REST request to partial update WorkLog partially : {}, {}", id, workLog);
        if (workLog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workLog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WorkLog> result = workLogRepository
            .findById(workLog.getId())
            .map(existingWorkLog -> {
                if (workLog.getTimeSpent() != null) {
                    existingWorkLog.setTimeSpent(workLog.getTimeSpent());
                }
                if (workLog.getDate() != null) {
                    existingWorkLog.setDate(workLog.getDate());
                }
                if (workLog.getUserId() != null) {
                    existingWorkLog.setUserId(workLog.getUserId());
                }

                return existingWorkLog;
            })
            .map(workLogRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workLog.getId().toString())
        );
    }

    /**
     * {@code GET  /work-logs} : get all the workLogs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workLogs in body.
     */
    @GetMapping("/work-logs")
    public List<WorkLog> getAllWorkLogs() {
        log.debug("REST request to get all WorkLogs");
        return workLogRepository.findAll();
    }

    /**
     * {@code GET  /work-logs/:id} : get the "id" workLog.
     *
     * @param id the id of the workLog to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workLog, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/work-logs/{id}")
    public ResponseEntity<WorkLog> getWorkLog(@PathVariable Long id) {
        log.debug("REST request to get WorkLog : {}", id);
        Optional<WorkLog> workLog = workLogRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(workLog);
    }

    /**
     * {@code DELETE  /work-logs/:id} : delete the "id" workLog.
     *
     * @param id the id of the workLog to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/work-logs/{id}")
    public ResponseEntity<Void> deleteWorkLog(@PathVariable Long id) {
        log.debug("REST request to delete WorkLog : {}", id);
        workLogRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
