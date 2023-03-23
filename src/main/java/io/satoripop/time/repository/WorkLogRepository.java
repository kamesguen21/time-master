package io.satoripop.time.repository;

import io.satoripop.time.domain.WorkLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WorkLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {}
