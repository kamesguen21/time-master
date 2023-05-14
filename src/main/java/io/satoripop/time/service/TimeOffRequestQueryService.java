package io.satoripop.time.service;

import io.satoripop.time.domain.*; // for static metamodels
import io.satoripop.time.domain.TimeOffRequest;
import io.satoripop.time.repository.TimeOffRequestRepository;
import io.satoripop.time.service.criteria.TimeOffRequestCriteria;
import io.satoripop.time.service.dto.TimeOffRequestDTO;
import io.satoripop.time.service.mapper.TimeOffRequestMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TimeOffRequest} entities in the database.
 * The main input is a {@link TimeOffRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TimeOffRequestDTO} or a {@link Page} of {@link TimeOffRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TimeOffRequestQueryService extends QueryService<TimeOffRequest> {

    private final Logger log = LoggerFactory.getLogger(TimeOffRequestQueryService.class);

    private final TimeOffRequestRepository timeOffRequestRepository;

    private final TimeOffRequestMapper timeOffRequestMapper;

    public TimeOffRequestQueryService(TimeOffRequestRepository timeOffRequestRepository, TimeOffRequestMapper timeOffRequestMapper) {
        this.timeOffRequestRepository = timeOffRequestRepository;
        this.timeOffRequestMapper = timeOffRequestMapper;
    }

    /**
     * Return a {@link List} of {@link TimeOffRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TimeOffRequestDTO> findByCriteria(TimeOffRequestCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TimeOffRequest> specification = createSpecification(criteria);
        return timeOffRequestMapper.toDto(timeOffRequestRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TimeOffRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TimeOffRequestDTO> findByCriteria(TimeOffRequestCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TimeOffRequest> specification = createSpecification(criteria);
        return timeOffRequestRepository.findAll(specification, page).map(timeOffRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TimeOffRequestCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TimeOffRequest> specification = createSpecification(criteria);
        return timeOffRequestRepository.count(specification);
    }

    /**
     * Function to convert {@link TimeOffRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TimeOffRequest> createSpecification(TimeOffRequestCriteria criteria) {
        Specification<TimeOffRequest> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TimeOffRequest_.id));
            }
            if (criteria.getStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDate(), TimeOffRequest_.startDate));
            }
            if (criteria.getEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDate(), TimeOffRequest_.endDate));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), TimeOffRequest_.status));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUserId(), TimeOffRequest_.userId));
            }
            if (criteria.getLeaveReason() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLeaveReason(), TimeOffRequest_.leaveReason));
            }
        }
        return specification;
    }
}
