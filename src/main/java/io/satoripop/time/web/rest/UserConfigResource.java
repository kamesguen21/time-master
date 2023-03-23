package io.satoripop.time.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.satoripop.time.domain.UserConfig;
import io.satoripop.time.repository.UserConfigRepository;
import io.satoripop.time.repository.search.UserConfigSearchRepository;
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
 * REST controller for managing {@link io.satoripop.time.domain.UserConfig}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UserConfigResource {

    private final Logger log = LoggerFactory.getLogger(UserConfigResource.class);

    private static final String ENTITY_NAME = "userConfig";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserConfigRepository userConfigRepository;

    private final UserConfigSearchRepository userConfigSearchRepository;

    public UserConfigResource(UserConfigRepository userConfigRepository, UserConfigSearchRepository userConfigSearchRepository) {
        this.userConfigRepository = userConfigRepository;
        this.userConfigSearchRepository = userConfigSearchRepository;
    }

    /**
     * {@code POST  /user-configs} : Create a new userConfig.
     *
     * @param userConfig the userConfig to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userConfig, or with status {@code 400 (Bad Request)} if the userConfig has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-configs")
    public ResponseEntity<UserConfig> createUserConfig(@Valid @RequestBody UserConfig userConfig) throws URISyntaxException {
        log.debug("REST request to save UserConfig : {}", userConfig);
        if (userConfig.getId() != null) {
            throw new BadRequestAlertException("A new userConfig cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserConfig result = userConfigRepository.save(userConfig);
        userConfigSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/user-configs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-configs/:id} : Updates an existing userConfig.
     *
     * @param id the id of the userConfig to save.
     * @param userConfig the userConfig to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userConfig,
     * or with status {@code 400 (Bad Request)} if the userConfig is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userConfig couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-configs/{id}")
    public ResponseEntity<UserConfig> updateUserConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserConfig userConfig
    ) throws URISyntaxException {
        log.debug("REST request to update UserConfig : {}, {}", id, userConfig);
        if (userConfig.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userConfig.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userConfigRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserConfig result = userConfigRepository.save(userConfig);
        userConfigSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userConfig.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-configs/:id} : Partial updates given fields of an existing userConfig, field will ignore if it is null
     *
     * @param id the id of the userConfig to save.
     * @param userConfig the userConfig to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userConfig,
     * or with status {@code 400 (Bad Request)} if the userConfig is not valid,
     * or with status {@code 404 (Not Found)} if the userConfig is not found,
     * or with status {@code 500 (Internal Server Error)} if the userConfig couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-configs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserConfig> partialUpdateUserConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserConfig userConfig
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserConfig partially : {}, {}", id, userConfig);
        if (userConfig.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userConfig.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userConfigRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserConfig> result = userConfigRepository
            .findById(userConfig.getId())
            .map(existingUserConfig -> {
                if (userConfig.getLogin() != null) {
                    existingUserConfig.setLogin(userConfig.getLogin());
                }
                if (userConfig.getFirstName() != null) {
                    existingUserConfig.setFirstName(userConfig.getFirstName());
                }
                if (userConfig.getLastName() != null) {
                    existingUserConfig.setLastName(userConfig.getLastName());
                }
                if (userConfig.getEmail() != null) {
                    existingUserConfig.setEmail(userConfig.getEmail());
                }
                if (userConfig.getPhoneNumber() != null) {
                    existingUserConfig.setPhoneNumber(userConfig.getPhoneNumber());
                }

                return existingUserConfig;
            })
            .map(userConfigRepository::save)
            .map(savedUserConfig -> {
                userConfigSearchRepository.save(savedUserConfig);

                return savedUserConfig;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userConfig.getId().toString())
        );
    }

    /**
     * {@code GET  /user-configs} : get all the userConfigs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userConfigs in body.
     */
    @GetMapping("/user-configs")
    public List<UserConfig> getAllUserConfigs() {
        log.debug("REST request to get all UserConfigs");
        return userConfigRepository.findAll();
    }

    /**
     * {@code GET  /user-configs/:id} : get the "id" userConfig.
     *
     * @param id the id of the userConfig to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userConfig, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-configs/{id}")
    public ResponseEntity<UserConfig> getUserConfig(@PathVariable Long id) {
        log.debug("REST request to get UserConfig : {}", id);
        Optional<UserConfig> userConfig = userConfigRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(userConfig);
    }

    /**
     * {@code DELETE  /user-configs/:id} : delete the "id" userConfig.
     *
     * @param id the id of the userConfig to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-configs/{id}")
    public ResponseEntity<Void> deleteUserConfig(@PathVariable Long id) {
        log.debug("REST request to delete UserConfig : {}", id);
        userConfigRepository.deleteById(id);
        userConfigSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/user-configs?query=:query} : search for the userConfig corresponding
     * to the query.
     *
     * @param query the query of the userConfig search.
     * @return the result of the search.
     */
    @GetMapping("/_search/user-configs")
    public List<UserConfig> searchUserConfigs(@RequestParam String query) {
        log.debug("REST request to search UserConfigs for query {}", query);
        return StreamSupport.stream(userConfigSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
