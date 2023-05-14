package io.satoripop.time.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.satoripop.time.IntegrationTest;
import io.satoripop.time.domain.Ticket;
import io.satoripop.time.domain.WorkLog;
import io.satoripop.time.repository.WorkLogRepository;
import io.satoripop.time.service.criteria.WorkLogCriteria;
import io.satoripop.time.service.dto.WorkLogDTO;
import io.satoripop.time.service.mapper.WorkLogMapper;
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
    private static final Integer SMALLER_TIME_SPENT = 1 - 1;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;
    private static final Long SMALLER_USER_ID = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/work-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WorkLogRepository workLogRepository;

    @Autowired
    private WorkLogMapper workLogMapper;

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
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);
        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLogDTO)))
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
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        int databaseSizeBeforeCreate = workLogRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLogDTO)))
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
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLogDTO)))
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
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        restWorkLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLogDTO)))
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
    void getWorkLogsByIdFiltering() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        Long id = workLog.getId();

        defaultWorkLogShouldBeFound("id.equals=" + id);
        defaultWorkLogShouldNotBeFound("id.notEquals=" + id);

        defaultWorkLogShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWorkLogShouldNotBeFound("id.greaterThan=" + id);

        defaultWorkLogShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWorkLogShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWorkLogsByTimeSpentIsEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where timeSpent equals to DEFAULT_TIME_SPENT
        defaultWorkLogShouldBeFound("timeSpent.equals=" + DEFAULT_TIME_SPENT);

        // Get all the workLogList where timeSpent equals to UPDATED_TIME_SPENT
        defaultWorkLogShouldNotBeFound("timeSpent.equals=" + UPDATED_TIME_SPENT);
    }

    @Test
    @Transactional
    void getAllWorkLogsByTimeSpentIsInShouldWork() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where timeSpent in DEFAULT_TIME_SPENT or UPDATED_TIME_SPENT
        defaultWorkLogShouldBeFound("timeSpent.in=" + DEFAULT_TIME_SPENT + "," + UPDATED_TIME_SPENT);

        // Get all the workLogList where timeSpent equals to UPDATED_TIME_SPENT
        defaultWorkLogShouldNotBeFound("timeSpent.in=" + UPDATED_TIME_SPENT);
    }

    @Test
    @Transactional
    void getAllWorkLogsByTimeSpentIsNullOrNotNull() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where timeSpent is not null
        defaultWorkLogShouldBeFound("timeSpent.specified=true");

        // Get all the workLogList where timeSpent is null
        defaultWorkLogShouldNotBeFound("timeSpent.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkLogsByTimeSpentIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where timeSpent is greater than or equal to DEFAULT_TIME_SPENT
        defaultWorkLogShouldBeFound("timeSpent.greaterThanOrEqual=" + DEFAULT_TIME_SPENT);

        // Get all the workLogList where timeSpent is greater than or equal to UPDATED_TIME_SPENT
        defaultWorkLogShouldNotBeFound("timeSpent.greaterThanOrEqual=" + UPDATED_TIME_SPENT);
    }

    @Test
    @Transactional
    void getAllWorkLogsByTimeSpentIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where timeSpent is less than or equal to DEFAULT_TIME_SPENT
        defaultWorkLogShouldBeFound("timeSpent.lessThanOrEqual=" + DEFAULT_TIME_SPENT);

        // Get all the workLogList where timeSpent is less than or equal to SMALLER_TIME_SPENT
        defaultWorkLogShouldNotBeFound("timeSpent.lessThanOrEqual=" + SMALLER_TIME_SPENT);
    }

    @Test
    @Transactional
    void getAllWorkLogsByTimeSpentIsLessThanSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where timeSpent is less than DEFAULT_TIME_SPENT
        defaultWorkLogShouldNotBeFound("timeSpent.lessThan=" + DEFAULT_TIME_SPENT);

        // Get all the workLogList where timeSpent is less than UPDATED_TIME_SPENT
        defaultWorkLogShouldBeFound("timeSpent.lessThan=" + UPDATED_TIME_SPENT);
    }

    @Test
    @Transactional
    void getAllWorkLogsByTimeSpentIsGreaterThanSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where timeSpent is greater than DEFAULT_TIME_SPENT
        defaultWorkLogShouldNotBeFound("timeSpent.greaterThan=" + DEFAULT_TIME_SPENT);

        // Get all the workLogList where timeSpent is greater than SMALLER_TIME_SPENT
        defaultWorkLogShouldBeFound("timeSpent.greaterThan=" + SMALLER_TIME_SPENT);
    }

    @Test
    @Transactional
    void getAllWorkLogsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where date equals to DEFAULT_DATE
        defaultWorkLogShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the workLogList where date equals to UPDATED_DATE
        defaultWorkLogShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where date in DEFAULT_DATE or UPDATED_DATE
        defaultWorkLogShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the workLogList where date equals to UPDATED_DATE
        defaultWorkLogShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllWorkLogsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where date is not null
        defaultWorkLogShouldBeFound("date.specified=true");

        // Get all the workLogList where date is null
        defaultWorkLogShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkLogsByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where userId equals to DEFAULT_USER_ID
        defaultWorkLogShouldBeFound("userId.equals=" + DEFAULT_USER_ID);

        // Get all the workLogList where userId equals to UPDATED_USER_ID
        defaultWorkLogShouldNotBeFound("userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllWorkLogsByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where userId in DEFAULT_USER_ID or UPDATED_USER_ID
        defaultWorkLogShouldBeFound("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID);

        // Get all the workLogList where userId equals to UPDATED_USER_ID
        defaultWorkLogShouldNotBeFound("userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllWorkLogsByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where userId is not null
        defaultWorkLogShouldBeFound("userId.specified=true");

        // Get all the workLogList where userId is null
        defaultWorkLogShouldNotBeFound("userId.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkLogsByUserIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where userId is greater than or equal to DEFAULT_USER_ID
        defaultWorkLogShouldBeFound("userId.greaterThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the workLogList where userId is greater than or equal to UPDATED_USER_ID
        defaultWorkLogShouldNotBeFound("userId.greaterThanOrEqual=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllWorkLogsByUserIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where userId is less than or equal to DEFAULT_USER_ID
        defaultWorkLogShouldBeFound("userId.lessThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the workLogList where userId is less than or equal to SMALLER_USER_ID
        defaultWorkLogShouldNotBeFound("userId.lessThanOrEqual=" + SMALLER_USER_ID);
    }

    @Test
    @Transactional
    void getAllWorkLogsByUserIdIsLessThanSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where userId is less than DEFAULT_USER_ID
        defaultWorkLogShouldNotBeFound("userId.lessThan=" + DEFAULT_USER_ID);

        // Get all the workLogList where userId is less than UPDATED_USER_ID
        defaultWorkLogShouldBeFound("userId.lessThan=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllWorkLogsByUserIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogList where userId is greater than DEFAULT_USER_ID
        defaultWorkLogShouldNotBeFound("userId.greaterThan=" + DEFAULT_USER_ID);

        // Get all the workLogList where userId is greater than SMALLER_USER_ID
        defaultWorkLogShouldBeFound("userId.greaterThan=" + SMALLER_USER_ID);
    }

    @Test
    @Transactional
    void getAllWorkLogsByTicketIsEqualToSomething() throws Exception {
        Ticket ticket;
        if (TestUtil.findAll(em, Ticket.class).isEmpty()) {
            workLogRepository.saveAndFlush(workLog);
            ticket = TicketResourceIT.createEntity(em);
        } else {
            ticket = TestUtil.findAll(em, Ticket.class).get(0);
        }
        em.persist(ticket);
        em.flush();
        workLog.setTicket(ticket);
        workLogRepository.saveAndFlush(workLog);
        Long ticketId = ticket.getId();

        // Get all the workLogList where ticket equals to ticketId
        defaultWorkLogShouldBeFound("ticketId.equals=" + ticketId);

        // Get all the workLogList where ticket equals to (ticketId + 1)
        defaultWorkLogShouldNotBeFound("ticketId.equals=" + (ticketId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWorkLogShouldBeFound(String filter) throws Exception {
        restWorkLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].timeSpent").value(hasItem(DEFAULT_TIME_SPENT)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())));

        // Check, that the count call also returns 1
        restWorkLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWorkLogShouldNotBeFound(String filter) throws Exception {
        restWorkLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWorkLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
        WorkLogDTO workLogDTO = workLogMapper.toDto(updatedWorkLog);

        restWorkLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workLogDTO))
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

        // Create the WorkLog
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workLogDTO))
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

        // Create the WorkLog
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workLogDTO))
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

        // Create the WorkLog
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workLogDTO)))
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

        // Create the WorkLog
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workLogDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workLogDTO))
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

        // Create the WorkLog
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workLogDTO))
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

        // Create the WorkLog
        WorkLogDTO workLogDTO = workLogMapper.toDto(workLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkLogMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(workLogDTO))
            )
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
