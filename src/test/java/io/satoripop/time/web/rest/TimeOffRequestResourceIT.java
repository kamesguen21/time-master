package io.satoripop.time.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.satoripop.time.IntegrationTest;
import io.satoripop.time.domain.TimeOffRequest;
import io.satoripop.time.domain.enumeration.TimeOffRequestStatus;
import io.satoripop.time.repository.TimeOffRequestRepository;
import io.satoripop.time.service.criteria.TimeOffRequestCriteria;
import io.satoripop.time.service.dto.TimeOffRequestDTO;
import io.satoripop.time.service.mapper.TimeOffRequestMapper;
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

    private static final TimeOffRequestStatus DEFAULT_STATUS = TimeOffRequestStatus.PENDING;
    private static final TimeOffRequestStatus UPDATED_STATUS = TimeOffRequestStatus.APPROVED;

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;
    private static final Long SMALLER_USER_ID = 1L - 1L;

    private static final String DEFAULT_LEAVE_REASON = "AAAAAAAAAA";
    private static final String UPDATED_LEAVE_REASON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/time-off-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TimeOffRequestRepository timeOffRequestRepository;

    @Autowired
    private TimeOffRequestMapper timeOffRequestMapper;

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
        TimeOffRequest timeOffRequest = new TimeOffRequest()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .status(DEFAULT_STATUS)
            .userId(DEFAULT_USER_ID)
            .leaveReason(DEFAULT_LEAVE_REASON);
        return timeOffRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimeOffRequest createUpdatedEntity(EntityManager em) {
        TimeOffRequest timeOffRequest = new TimeOffRequest()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .userId(UPDATED_USER_ID)
            .leaveReason(UPDATED_LEAVE_REASON);
        return timeOffRequest;
    }

    @BeforeEach
    public void initTest() {
        timeOffRequest = createEntity(em);
    }

    @Test
    @Transactional
    void createTimeOffRequest() throws Exception {
        int databaseSizeBeforeCreate = timeOffRequestRepository.findAll().size();
        // Create the TimeOffRequest
        TimeOffRequestDTO timeOffRequestDTO = timeOffRequestMapper.toDto(timeOffRequest);
        restTimeOffRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeOffRequestDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeCreate + 1);
        TimeOffRequest testTimeOffRequest = timeOffRequestList.get(timeOffRequestList.size() - 1);
        assertThat(testTimeOffRequest.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testTimeOffRequest.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testTimeOffRequest.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTimeOffRequest.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testTimeOffRequest.getLeaveReason()).isEqualTo(DEFAULT_LEAVE_REASON);
    }

    @Test
    @Transactional
    void createTimeOffRequestWithExistingId() throws Exception {
        // Create the TimeOffRequest with an existing ID
        timeOffRequest.setId(1L);
        TimeOffRequestDTO timeOffRequestDTO = timeOffRequestMapper.toDto(timeOffRequest);

        int databaseSizeBeforeCreate = timeOffRequestRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimeOffRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeOffRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = timeOffRequestRepository.findAll().size();
        // set the field null
        timeOffRequest.setStartDate(null);

        // Create the TimeOffRequest, which fails.
        TimeOffRequestDTO timeOffRequestDTO = timeOffRequestMapper.toDto(timeOffRequest);

        restTimeOffRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeOffRequestDTO))
            )
            .andExpect(status().isBadRequest());

        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = timeOffRequestRepository.findAll().size();
        // set the field null
        timeOffRequest.setEndDate(null);

        // Create the TimeOffRequest, which fails.
        TimeOffRequestDTO timeOffRequestDTO = timeOffRequestMapper.toDto(timeOffRequest);

        restTimeOffRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeOffRequestDTO))
            )
            .andExpect(status().isBadRequest());

        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeTest);
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
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].leaveReason").value(hasItem(DEFAULT_LEAVE_REASON)));
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
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.leaveReason").value(DEFAULT_LEAVE_REASON));
    }

    @Test
    @Transactional
    void getTimeOffRequestsByIdFiltering() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        Long id = timeOffRequest.getId();

        defaultTimeOffRequestShouldBeFound("id.equals=" + id);
        defaultTimeOffRequestShouldNotBeFound("id.notEquals=" + id);

        defaultTimeOffRequestShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTimeOffRequestShouldNotBeFound("id.greaterThan=" + id);

        defaultTimeOffRequestShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTimeOffRequestShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where startDate equals to DEFAULT_START_DATE
        defaultTimeOffRequestShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the timeOffRequestList where startDate equals to UPDATED_START_DATE
        defaultTimeOffRequestShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultTimeOffRequestShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the timeOffRequestList where startDate equals to UPDATED_START_DATE
        defaultTimeOffRequestShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where startDate is not null
        defaultTimeOffRequestShouldBeFound("startDate.specified=true");

        // Get all the timeOffRequestList where startDate is null
        defaultTimeOffRequestShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where endDate equals to DEFAULT_END_DATE
        defaultTimeOffRequestShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the timeOffRequestList where endDate equals to UPDATED_END_DATE
        defaultTimeOffRequestShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultTimeOffRequestShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the timeOffRequestList where endDate equals to UPDATED_END_DATE
        defaultTimeOffRequestShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where endDate is not null
        defaultTimeOffRequestShouldBeFound("endDate.specified=true");

        // Get all the timeOffRequestList where endDate is null
        defaultTimeOffRequestShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where status equals to DEFAULT_STATUS
        defaultTimeOffRequestShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the timeOffRequestList where status equals to UPDATED_STATUS
        defaultTimeOffRequestShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultTimeOffRequestShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the timeOffRequestList where status equals to UPDATED_STATUS
        defaultTimeOffRequestShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where status is not null
        defaultTimeOffRequestShouldBeFound("status.specified=true");

        // Get all the timeOffRequestList where status is null
        defaultTimeOffRequestShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where userId equals to DEFAULT_USER_ID
        defaultTimeOffRequestShouldBeFound("userId.equals=" + DEFAULT_USER_ID);

        // Get all the timeOffRequestList where userId equals to UPDATED_USER_ID
        defaultTimeOffRequestShouldNotBeFound("userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where userId in DEFAULT_USER_ID or UPDATED_USER_ID
        defaultTimeOffRequestShouldBeFound("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID);

        // Get all the timeOffRequestList where userId equals to UPDATED_USER_ID
        defaultTimeOffRequestShouldNotBeFound("userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where userId is not null
        defaultTimeOffRequestShouldBeFound("userId.specified=true");

        // Get all the timeOffRequestList where userId is null
        defaultTimeOffRequestShouldNotBeFound("userId.specified=false");
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByUserIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where userId is greater than or equal to DEFAULT_USER_ID
        defaultTimeOffRequestShouldBeFound("userId.greaterThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the timeOffRequestList where userId is greater than or equal to UPDATED_USER_ID
        defaultTimeOffRequestShouldNotBeFound("userId.greaterThanOrEqual=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByUserIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where userId is less than or equal to DEFAULT_USER_ID
        defaultTimeOffRequestShouldBeFound("userId.lessThanOrEqual=" + DEFAULT_USER_ID);

        // Get all the timeOffRequestList where userId is less than or equal to SMALLER_USER_ID
        defaultTimeOffRequestShouldNotBeFound("userId.lessThanOrEqual=" + SMALLER_USER_ID);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByUserIdIsLessThanSomething() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where userId is less than DEFAULT_USER_ID
        defaultTimeOffRequestShouldNotBeFound("userId.lessThan=" + DEFAULT_USER_ID);

        // Get all the timeOffRequestList where userId is less than UPDATED_USER_ID
        defaultTimeOffRequestShouldBeFound("userId.lessThan=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByUserIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where userId is greater than DEFAULT_USER_ID
        defaultTimeOffRequestShouldNotBeFound("userId.greaterThan=" + DEFAULT_USER_ID);

        // Get all the timeOffRequestList where userId is greater than SMALLER_USER_ID
        defaultTimeOffRequestShouldBeFound("userId.greaterThan=" + SMALLER_USER_ID);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByLeaveReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where leaveReason equals to DEFAULT_LEAVE_REASON
        defaultTimeOffRequestShouldBeFound("leaveReason.equals=" + DEFAULT_LEAVE_REASON);

        // Get all the timeOffRequestList where leaveReason equals to UPDATED_LEAVE_REASON
        defaultTimeOffRequestShouldNotBeFound("leaveReason.equals=" + UPDATED_LEAVE_REASON);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByLeaveReasonIsInShouldWork() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where leaveReason in DEFAULT_LEAVE_REASON or UPDATED_LEAVE_REASON
        defaultTimeOffRequestShouldBeFound("leaveReason.in=" + DEFAULT_LEAVE_REASON + "," + UPDATED_LEAVE_REASON);

        // Get all the timeOffRequestList where leaveReason equals to UPDATED_LEAVE_REASON
        defaultTimeOffRequestShouldNotBeFound("leaveReason.in=" + UPDATED_LEAVE_REASON);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByLeaveReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where leaveReason is not null
        defaultTimeOffRequestShouldBeFound("leaveReason.specified=true");

        // Get all the timeOffRequestList where leaveReason is null
        defaultTimeOffRequestShouldNotBeFound("leaveReason.specified=false");
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByLeaveReasonContainsSomething() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where leaveReason contains DEFAULT_LEAVE_REASON
        defaultTimeOffRequestShouldBeFound("leaveReason.contains=" + DEFAULT_LEAVE_REASON);

        // Get all the timeOffRequestList where leaveReason contains UPDATED_LEAVE_REASON
        defaultTimeOffRequestShouldNotBeFound("leaveReason.contains=" + UPDATED_LEAVE_REASON);
    }

    @Test
    @Transactional
    void getAllTimeOffRequestsByLeaveReasonNotContainsSomething() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        // Get all the timeOffRequestList where leaveReason does not contain DEFAULT_LEAVE_REASON
        defaultTimeOffRequestShouldNotBeFound("leaveReason.doesNotContain=" + DEFAULT_LEAVE_REASON);

        // Get all the timeOffRequestList where leaveReason does not contain UPDATED_LEAVE_REASON
        defaultTimeOffRequestShouldBeFound("leaveReason.doesNotContain=" + UPDATED_LEAVE_REASON);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTimeOffRequestShouldBeFound(String filter) throws Exception {
        restTimeOffRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeOffRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].leaveReason").value(hasItem(DEFAULT_LEAVE_REASON)));

        // Check, that the count call also returns 1
        restTimeOffRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTimeOffRequestShouldNotBeFound(String filter) throws Exception {
        restTimeOffRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTimeOffRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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

        // Update the timeOffRequest
        TimeOffRequest updatedTimeOffRequest = timeOffRequestRepository.findById(timeOffRequest.getId()).get();
        // Disconnect from session so that the updates on updatedTimeOffRequest are not directly saved in db
        em.detach(updatedTimeOffRequest);
        updatedTimeOffRequest
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .userId(UPDATED_USER_ID)
            .leaveReason(UPDATED_LEAVE_REASON);
        TimeOffRequestDTO timeOffRequestDTO = timeOffRequestMapper.toDto(updatedTimeOffRequest);

        restTimeOffRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timeOffRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timeOffRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
        TimeOffRequest testTimeOffRequest = timeOffRequestList.get(timeOffRequestList.size() - 1);
        assertThat(testTimeOffRequest.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTimeOffRequest.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testTimeOffRequest.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTimeOffRequest.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testTimeOffRequest.getLeaveReason()).isEqualTo(UPDATED_LEAVE_REASON);
    }

    @Test
    @Transactional
    void putNonExistingTimeOffRequest() throws Exception {
        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();
        timeOffRequest.setId(count.incrementAndGet());

        // Create the TimeOffRequest
        TimeOffRequestDTO timeOffRequestDTO = timeOffRequestMapper.toDto(timeOffRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeOffRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timeOffRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timeOffRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimeOffRequest() throws Exception {
        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();
        timeOffRequest.setId(count.incrementAndGet());

        // Create the TimeOffRequest
        TimeOffRequestDTO timeOffRequestDTO = timeOffRequestMapper.toDto(timeOffRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeOffRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timeOffRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimeOffRequest() throws Exception {
        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();
        timeOffRequest.setId(count.incrementAndGet());

        // Create the TimeOffRequest
        TimeOffRequestDTO timeOffRequestDTO = timeOffRequestMapper.toDto(timeOffRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeOffRequestMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeOffRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
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

        partialUpdatedTimeOffRequest.leaveReason(UPDATED_LEAVE_REASON);

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
        assertThat(testTimeOffRequest.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testTimeOffRequest.getLeaveReason()).isEqualTo(UPDATED_LEAVE_REASON);
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

        partialUpdatedTimeOffRequest
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .userId(UPDATED_USER_ID)
            .leaveReason(UPDATED_LEAVE_REASON);

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
        assertThat(testTimeOffRequest.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testTimeOffRequest.getLeaveReason()).isEqualTo(UPDATED_LEAVE_REASON);
    }

    @Test
    @Transactional
    void patchNonExistingTimeOffRequest() throws Exception {
        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();
        timeOffRequest.setId(count.incrementAndGet());

        // Create the TimeOffRequest
        TimeOffRequestDTO timeOffRequestDTO = timeOffRequestMapper.toDto(timeOffRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeOffRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timeOffRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timeOffRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimeOffRequest() throws Exception {
        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();
        timeOffRequest.setId(count.incrementAndGet());

        // Create the TimeOffRequest
        TimeOffRequestDTO timeOffRequestDTO = timeOffRequestMapper.toDto(timeOffRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeOffRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timeOffRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimeOffRequest() throws Exception {
        int databaseSizeBeforeUpdate = timeOffRequestRepository.findAll().size();
        timeOffRequest.setId(count.incrementAndGet());

        // Create the TimeOffRequest
        TimeOffRequestDTO timeOffRequestDTO = timeOffRequestMapper.toDto(timeOffRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeOffRequestMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timeOffRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimeOffRequest in the database
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTimeOffRequest() throws Exception {
        // Initialize the database
        timeOffRequestRepository.saveAndFlush(timeOffRequest);

        int databaseSizeBeforeDelete = timeOffRequestRepository.findAll().size();

        // Delete the timeOffRequest
        restTimeOffRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, timeOffRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TimeOffRequest> timeOffRequestList = timeOffRequestRepository.findAll();
        assertThat(timeOffRequestList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
