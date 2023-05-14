package io.satoripop.time.repository;

import io.satoripop.time.domain.TimeOffRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TimeOffRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimeOffRequestRepository extends JpaRepository<TimeOffRequest, Long>, JpaSpecificationExecutor<TimeOffRequest> {}
