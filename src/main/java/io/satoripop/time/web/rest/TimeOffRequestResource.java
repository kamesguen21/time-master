package io.satoripop.time.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.satoripop.time.domain.TimeOffRequest;
import io.satoripop.time.repository.TimeOffRequestRepository;
import io.satoripop.time.repository.search.TimeOffRequestSearchRepository;
import io.satoripop.time.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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
 * REST controller for managing {@link io.satoripop.time.domain.TimeOffRequest}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TimeOffRequestResource {

    private final Logger log = LoggerFactory.getLogger(TimeOffRequestResource.class);

    private static final String ENTITY_NAME = "timeOffRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TimeOffRequestRepository timeOffRequestRepository;

    private final TimeOffRequestSearchRepository timeOffRequestSearchRepository;

    public TimeOffRequestResource(
        TimeOffRequestRepository timeOffRequestRepository,
        TimeOffRequestSearchRepository timeOffRequestSearchRepository
    ) {
        this.timeOffRequestRepository = timeOffRequestRepository;
        this.timeOffRequestSearchRepository = timeOffRequestSearchRepository;
    }

    /**
     * {@code POST  /time-off-requests} : Create a new timeOffRequest.
     *
     * @param timeOffRequest the timeOffRequest to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timeOffRequest, or with status {@code 400 (Bad Request)} if the timeOffRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/time-off-requests")
    public ResponseEntity<TimeOffRequest> createTimeOffRequest(@Valid @RequestBody TimeOffRequest timeOffRequest)
        throws URISyntaxException {
        log.debug("REST request to save TimeOffRequest : {}", timeOffRequest);
        if (timeOffRequest.getId() != null) {
            throw new BadRequestAlertException("A new timeOffRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TimeOffRequest result = timeOffRequestRepository.save(timeOffRequest);
        timeOffRequestSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/time-off-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /time-off-requests/:id} : Updates an existing timeOffRequest.
     *
     * @param id the id of the timeOffRequest to save.
     * @param timeOffRequest the timeOffRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timeOffRequest,
     * or with status {@code 400 (Bad Request)} if the timeOffRequest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timeOffRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/time-off-requests/{id}")
    public ResponseEntity<TimeOffRequest> updateTimeOffRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TimeOffRequest timeOffRequest
    ) throws URISyntaxException {
        log.debug("REST request to update TimeOffRequest : {}, {}", id, timeOffRequest);
        if (timeOffRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timeOffRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timeOffRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TimeOffRequest result = timeOffRequestRepository.save(timeOffRequest);
        timeOffRequestSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, timeOffRequest.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /time-off-requests/:id} : Partial updates given fields of an existing timeOffRequest, field will ignore if it is null
     *
     * @param id the id of the timeOffRequest to save.
     * @param timeOffRequest the timeOffRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timeOffRequest,
     * or with status {@code 400 (Bad Request)} if the timeOffRequest is not valid,
     * or with status {@code 404 (Not Found)} if the timeOffRequest is not found,
     * or with status {@code 500 (Internal Server Error)} if the timeOffRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/time-off-requests/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TimeOffRequest> partialUpdateTimeOffRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TimeOffRequest timeOffRequest
    ) throws URISyntaxException {
        log.debug("REST request to partial update TimeOffRequest partially : {}, {}", id, timeOffRequest);
        if (timeOffRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timeOffRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timeOffRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TimeOffRequest> result = timeOffRequestRepository
            .findById(timeOffRequest.getId())
            .map(existingTimeOffRequest -> {
                if (timeOffRequest.getStartDate() != null) {
                    existingTimeOffRequest.setStartDate(timeOffRequest.getStartDate());
                }
                if (timeOffRequest.getEndDate() != null) {
                    existingTimeOffRequest.setEndDate(timeOffRequest.getEndDate());
                }
                if (timeOffRequest.getStatus() != null) {
                    existingTimeOffRequest.setStatus(timeOffRequest.getStatus());
                }

                return existingTimeOffRequest;
            })
            .map(timeOffRequestRepository::save)
            .map(savedTimeOffRequest -> {
                timeOffRequestSearchRepository.save(savedTimeOffRequest);

                return savedTimeOffRequest;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, timeOffRequest.getId().toString())
        );
    }

    /**
     * {@code GET  /time-off-requests} : get all the timeOffRequests.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of timeOffRequests in body.
     */
    @GetMapping("/time-off-requests")
    public List<TimeOffRequest> getAllTimeOffRequests() {
        log.debug("REST request to get all TimeOffRequests");
        return timeOffRequestRepository.findAll();
    }

    /**
     * {@code GET  /time-off-requests/:id} : get the "id" timeOffRequest.
     *
     * @param id the id of the timeOffRequest to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timeOffRequest, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/time-off-requests/{id}")
    public ResponseEntity<TimeOffRequest> getTimeOffRequest(@PathVariable Long id) {
        log.debug("REST request to get TimeOffRequest : {}", id);
        Optional<TimeOffRequest> timeOffRequest = timeOffRequestRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(timeOffRequest);
    }

    /**
     * {@code DELETE  /time-off-requests/:id} : delete the "id" timeOffRequest.
     *
     * @param id the id of the timeOffRequest to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/time-off-requests/{id}")
    public ResponseEntity<Void> deleteTimeOffRequest(@PathVariable Long id) {
        log.debug("REST request to delete TimeOffRequest : {}", id);
        timeOffRequestRepository.deleteById(id);
        timeOffRequestSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/time-off-requests?query=:query} : search for the timeOffRequest corresponding
     * to the query.
     *
     * @param query the query of the timeOffRequest search.
     * @return the result of the search.
     */
    @GetMapping("/_search/time-off-requests")
    public List<TimeOffRequest> searchTimeOffRequests(@RequestParam String query) {
        log.debug("REST request to search TimeOffRequests for query {}", query);
        return StreamSupport.stream(timeOffRequestSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
