package io.satoripop.time.repository;

import io.satoripop.time.domain.UserConfig;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserConfigRepository extends JpaRepository<UserConfig, Long> {}
