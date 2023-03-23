package io.satoripop.time.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.satoripop.time.IntegrationTest;
import io.satoripop.time.domain.TimeOffRequest;
import io.satoripop.time.repository.TimeOffRequestRepository;
import io.satoripop.time.repository.search.TimeOffRequestSearchRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TimeOffRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TimeOffRequestResourceIT {

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/time-off-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/time-off-requests";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TimeOffRequestRepository timeOffRequestRepository;

    @Autowired
    private TimeOffRequestSearchRepository timeOffRequestSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTimeOffRequestMockMvc;

    private TimeOffRequest timeOffRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimeOffRequest createEntity(EntityManager em) {
        TimeOffRequest timeOffRequest = new TimeOffRequest().startDate(DEFAULT_START_DATE).endDate(DEFAULT_END_DATE).status(DEFAULT_STATUS);
        return timeOffRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimeOffRequest createUpdatedEntity(EntityManager em) {
        TimeOffRequest timeOffRequest = new TimeOffRequest().startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);
        return timeOffRequest;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        timeOffRequestSearchRepository.deleteAll();
        assertThat(timeOffRequestSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        timeOffRequest = createEntity(em);
    }

    @Test
    @Transactional
    void createTimeOffRequest() throws Exception {
        int databaseSizeBeforeCreate = timeOffRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        // Create the TimeOffRequest
        restTimeOffRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeOffRequest))
            )
            .andExpect(status().isCreated());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TimeOffRequest testTimeOffRequest = timeOffRequestList.get(timeOffRequestList.size() - 1);
        assertThat(testTimeOffRequest.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testTimeOffRequest.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testTimeOffRequest.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createTimeOffRequestWithExistingId() throws Exception {
        // Create the TimeOffRequest with an existing ID
        timeOffRequest.setId(1L);

        int databaseSizeBeforeCreate = timeOffRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimeOffRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeOffRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = timeOffRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        // set the field null
        timeOffRequest.setStartDate(null);

        // Create the TimeOffRequest, which fails.

        restTimeOffRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeOffRequest))
            )
            .andExpect(status().isBadRequest());

        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = timeOffRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        // set the field null
        timeOffRequest.setEndDate(null);

        // Create the TimeOffRequest, which fails.

        restTimeOffRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeOffRequest))
            )
            .andExpect(status().isBadRequest());

        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTimeOffRequests() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList
        restTimeOffRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeOffRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getTimeOffRequest() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get the timeOffRequest
        restTimeOffRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, timeOffRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timeOffRequest.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingTimeOffRequest() throws Exception {
        // Get the timeOffRequest
        restTimeOffRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTimeOffRequest() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();
        timeOffRequestSearchRepository.save(timeOffRequest);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());

        // Update the timeOffRequest
        TimeOffRequest updatedTimeOffRequest = timeOffRequestRepository.findById(timeOffRequest.getId()).get();
        // Disconnect from session so that the updates on updatedTimeOffRequest are not directly saved in db
        em.detach(updatedTimeOffRequest);
        updatedTimeOffRequest.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);

        restTimeOffRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTimeOffRequest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTimeOffRequest))
            )
            .andExpect(status().isOk());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
        TimeOffRequest testTimeOffRequest = timeOffRequestList.get(timeOffRequestList.size() - 1);
        assertThat(testTimeOffRequest.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTimeOffRequest.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testTimeOffRequest.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TimeOffRequest> timeOffRequestSearchList = IterableUtils.toList(timeOffRequestSearchRepository.findAll());
                TimeOffRequest testTimeOffRequestSearch = timeOffRequestSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTimeOffRequestSearch.getStartDate()).isEqualTo(UPDATED_START_DATE);
                assertThat(testTimeOffRequestSearch.getEndDate()).isEqualTo(UPDATED_END_DATE);
                assertThat(testTimeOffRequestSearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingTimeOffRequest() throws Exception {
        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        timeOffRequest.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeOffRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timeOffRequest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timeOffRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimeOffRequest() throws Exception {
        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        timeOffRequest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeOffRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timeOffRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimeOffRequest() throws Exception {
        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        timeOffRequest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeOffRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeOffRequest)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTimeOffRequestWithPatch() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();

        // Update the timeOffRequest using partial update
        TimeOffRequest partialUpdatedTimeOffRequest = new TimeOffRequest();
        partialUpdatedTimeOffRequest.setId(timeOffRequest.getId());

        restTimeOffRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimeOffRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimeOffRequest))
            )
            .andExpect(status().isOk());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
        TimeOffRequest testTimeOffRequest = timeOffRequestList.get(timeOffRequestList.size() - 1);
        assertThat(testTimeOffRequest.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testTimeOffRequest.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testTimeOffRequest.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateTimeOffRequestWithPatch() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();

        // Update the timeOffRequest using partial update
        TimeOffRequest partialUpdatedTimeOffRequest = new TimeOffRequest();
        partialUpdatedTimeOffRequest.setId(timeOffRequest.getId());

        partialUpdatedTimeOffRequest.startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);

        restTimeOffRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimeOffRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimeOffRequest))
            )
            .andExpect(status().isOk());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
        TimeOffRequest testTimeOffRequest = timeOffRequestList.get(timeOffRequestList.size() - 1);
        assertThat(testTimeOffRequest.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTimeOffRequest.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testTimeOffRequest.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingTimeOffRequest() throws Exception {
        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        timeOffRequest.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeOffRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timeOffRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timeOffRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimeOffRequest() throws Exception {
        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        timeOffRequest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeOffRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timeOffRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimeOffRequest() throws Exception {
        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        timeOffRequest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeOffRequestMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(timeOffRequest))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTimeOffRequest() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);
        timeOffRequestRepository.save(timeOffRequest);
        timeOffRequestSearchRepository.save(timeOffRequest);

        int databaseSizeBeforeDelete = timeOffRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the timeOffRequest
        restTimeOffRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, timeOffRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeOffRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTimeOffRequest() throws Exception {
        // Initialize the database
        timeOffRequest = timeOffRequestRepository.saveAndFlush(timeOffRequest);
        timeOffRequestSearchRepository.save(timeOffRequest);

        // Search the timeOffRequest
        restTimeOffRequestMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + timeOffRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeOffRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }
}
