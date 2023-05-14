package io.satoripop.time.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.satoripop.time.IntegrationTest;
import io.satoripop.time.domain.WorkLog;
import io.satoripop.time.repository.WorkLogRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WorkLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WorkLogResourceIT {

    private static final Integer DEFAULT_TIME_SPENT = 1;
    private static final Integer UPDATED_TIME_SPENT = 2;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String ENTITY_API_URL = "/api/work-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WorkLogRepository workLogRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkLogMockMvc;

    private WorkLog workLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkLog createEntity(EntityManager em) {
        WorkLog workLog = new WorkLog().timeSpent(DEFAULT_TIME_SPENT).date(DEFAULT_DATE).userId(DEFAULT_USER_ID);
        return workLog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkLog createUpdatedEntity(EntityManager em) {
        WorkLog workLog = new WorkLog().timeSpent(UPDATED_TIME_SPENT).date(UPDATED_DATE).userId(UPDATED_USER_ID);
        return workLog;
    }

    @BeforeEach
    public void initTest() {
        workLog = createEntity(em);
    }

    @Test
    @Transactional
    void createWorkLog() throws Exception {
        int databaseSizeBeforeCreate = workLogRepository.findAll().size();
        // Create the WorkLog
        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLog)))
            .andExpect(status().isCreated());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeCreate + 1);
        WorkLog testWorkLog = workLogList.get(workLogList.size() - 1);
        assertThat(testWorkLog.getTimeSpent()).isEqualTo(DEFAULT_TIME_SPENT);
        assertThat(testWorkLog.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testWorkLog.getUserId()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    @Transactional
    void createWorkLogWithExistingId() throws Exception {
        // Create the WorkLog with an existing ID
        workLog.setId(1L);

        int databaseSizeBeforeCreate = workLogRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLog)))
            .andExpect(status().isBadRequest());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTimeSpentIsRequired() throws Exception {
        int databaseSizeBeforeTest = workLogRepository.findAll().size();
        // set the field null
        workLog.setTimeSpent(null);

        // Create the WorkLog, which fails.

        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLog)))
            .andExpect(status().isBadRequest());

        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = workLogRepository.findAll().size();
        // set the field null
        workLog.setDate(null);

        // Create the WorkLog, which fails.

        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLog)))
            .andExpect(status().isBadRequest());

        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWorkLogs() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList
        restWorkLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].timeSpent").value(hasItem(DEFAULT_TIME_SPENT)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())));
    }

    @Test
    @Transactional
    void getWorkLog() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get the workLog
        restWorkLogMockMvc
            .perform(get(ENTITY_API_URL_ID, workLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(workLog.getId().intValue()))
            .andExpect(jsonPath("$.timeSpent").value(DEFAULT_TIME_SPENT))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingWorkLog() throws Exception {
        // Get the workLog
        restWorkLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWorkLog() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();

        // Update the workLog
        WorkLog updatedWorkLog = workLogRepository.findById(workLog.getId()).get();
        // Disconnect from session so that the updates on updatedWorkLog are not directly saved in db
        em.detach(updatedWorkLog);
        updatedWorkLog.timeSpent(UPDATED_TIME_SPENT).date(UPDATED_DATE).userId(UPDATED_USER_ID);

        restWorkLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWorkLog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedWorkLog))
            )
            .andExpect(status().isOk());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
        WorkLog testWorkLog = workLogList.get(workLogList.size() - 1);
        assertThat(testWorkLog.getTimeSpent()).isEqualTo(UPDATED_TIME_SPENT);
        assertThat(testWorkLog.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testWorkLog.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void putNonExistingWorkLog() throws Exception {
        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();
        workLog.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workLog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWorkLog() throws Exception {
        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();
        workLog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWorkLog() throws Exception {
        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();
        workLog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLog)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWorkLogWithPatch() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();

        // Update the workLog using partial update
        WorkLog partialUpdatedWorkLog = new WorkLog();
        partialUpdatedWorkLog.setId(workLog.getId());

        partialUpdatedWorkLog.timeSpent(UPDATED_TIME_SPENT).date(UPDATED_DATE).userId(UPDATED_USER_ID);

        restWorkLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorkLog))
            )
            .andExpect(status().isOk());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
        WorkLog testWorkLog = workLogList.get(workLogList.size() - 1);
        assertThat(testWorkLog.getTimeSpent()).isEqualTo(UPDATED_TIME_SPENT);
        assertThat(testWorkLog.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testWorkLog.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void fullUpdateWorkLogWithPatch() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();

        // Update the workLog using partial update
        WorkLog partialUpdatedWorkLog = new WorkLog();
        partialUpdatedWorkLog.setId(workLog.getId());

        partialUpdatedWorkLog.timeSpent(UPDATED_TIME_SPENT).date(UPDATED_DATE).userId(UPDATED_USER_ID);

        restWorkLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorkLog))
            )
            .andExpect(status().isOk());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
        WorkLog testWorkLog = workLogList.get(workLogList.size() - 1);
        assertThat(testWorkLog.getTimeSpent()).isEqualTo(UPDATED_TIME_SPENT);
        assertThat(testWorkLog.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testWorkLog.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void patchNonExistingWorkLog() throws Exception {
        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();
        workLog.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWorkLog() throws Exception {
        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();
        workLog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWorkLog() throws Exception {
        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();
        workLog.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(workLog)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkLog in the database
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWorkLog() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        int databaseSizeBeforeDelete = workLogRepository.findAll().size();

        // Delete the workLog
        restWorkLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, workLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WorkLog> workLogList = workLogRepository.findAll();
        assertThat(workLogList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
