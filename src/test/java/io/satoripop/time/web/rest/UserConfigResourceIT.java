package io.satoripop.time.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.satoripop.time.IntegrationTest;
import io.satoripop.time.domain.UserConfig;
import io.satoripop.time.repository.UserConfigRepository;
import io.satoripop.time.repository.search.UserConfigSearchRepository;
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
 * Integration tests for the {@link UserConfigResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserConfigResourceIT {

    private static final String DEFAULT_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-configs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/user-configs";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserConfigRepository userConfigRepository;

    @Autowired
    private UserConfigSearchRepository userConfigSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserConfigMockMvc;

    private UserConfig userConfig;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserConfig createEntity(EntityManager em) {
        UserConfig userConfig = new UserConfig()
            .login(DEFAULT_LOGIN)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER);
        return userConfig;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserConfig createUpdatedEntity(EntityManager em) {
        UserConfig userConfig = new UserConfig()
            .login(UPDATED_LOGIN)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER);
        return userConfig;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        userConfigSearchRepository.deleteAll();
        assertThat(userConfigSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        userConfig = createEntity(em);
    }

    @Test
    @Transactional
    void createUserConfig() throws Exception {
        int databaseSizeBeforeCreate = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        // Create the UserConfig
        restUserConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userConfig)))
            .andExpect(status().isCreated());

        // Validate the UserConfig in the database
        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        UserConfig testUserConfig = userConfigList.get(userConfigList.size() - 1);
        assertThat(testUserConfig.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(testUserConfig.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testUserConfig.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testUserConfig.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUserConfig.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void createUserConfigWithExistingId() throws Exception {
        // Create the UserConfig with an existing ID
        userConfig.setId(1L);

        int databaseSizeBeforeCreate = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userConfig)))
            .andExpect(status().isBadRequest());

        // Validate the UserConfig in the database
        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLoginIsRequired() throws Exception {
        int databaseSizeBeforeTest = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        // set the field null
        userConfig.setLogin(null);

        // Create the UserConfig, which fails.

        restUserConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userConfig)))
            .andExpect(status().isBadRequest());

        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        // set the field null
        userConfig.setFirstName(null);

        // Create the UserConfig, which fails.

        restUserConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userConfig)))
            .andExpect(status().isBadRequest());

        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        // set the field null
        userConfig.setLastName(null);

        // Create the UserConfig, which fails.

        restUserConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userConfig)))
            .andExpect(status().isBadRequest());

        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        // set the field null
        userConfig.setEmail(null);

        // Create the UserConfig, which fails.

        restUserConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userConfig)))
            .andExpect(status().isBadRequest());

        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        // set the field null
        userConfig.setPhoneNumber(null);

        // Create the UserConfig, which fails.

        restUserConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userConfig)))
            .andExpect(status().isBadRequest());

        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllUserConfigs() throws Exception {
        // Initialize the database
        userConfigRepository.saveAndFlush(userConfig);

        // Get all the userConfigList
        restUserConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)));
    }

    @Test
    @Transactional
    void getUserConfig() throws Exception {
        // Initialize the database
        userConfigRepository.saveAndFlush(userConfig);

        // Get the userConfig
        restUserConfigMockMvc
            .perform(get(ENTITY_API_URL_ID, userConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userConfig.getId().intValue()))
            .andExpect(jsonPath("$.login").value(DEFAULT_LOGIN))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER));
    }

    @Test
    @Transactional
    void getNonExistingUserConfig() throws Exception {
        // Get the userConfig
        restUserConfigMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserConfig() throws Exception {
        // Initialize the database
        userConfigRepository.saveAndFlush(userConfig);

        int databaseSizeBeforeUpdate = userConfigRepository.findAll().size();
        userConfigSearchRepository.save(userConfig);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());

        // Update the userConfig
        UserConfig updatedUserConfig = userConfigRepository.findById(userConfig.getId()).get();
        // Disconnect from session so that the updates on updatedUserConfig are not directly saved in db
        em.detach(updatedUserConfig);
        updatedUserConfig
            .login(UPDATED_LOGIN)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER);

        restUserConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserConfig.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserConfig))
            )
            .andExpect(status().isOk());

        // Validate the UserConfig in the database
        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeUpdate);
        UserConfig testUserConfig = userConfigList.get(userConfigList.size() - 1);
        assertThat(testUserConfig.getLogin()).isEqualTo(UPDATED_LOGIN);
        assertThat(testUserConfig.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUserConfig.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testUserConfig.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUserConfig.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UserConfig> userConfigSearchList = IterableUtils.toList(userConfigSearchRepository.findAll());
                UserConfig testUserConfigSearch = userConfigSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testUserConfigSearch.getLogin()).isEqualTo(UPDATED_LOGIN);
                assertThat(testUserConfigSearch.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
                assertThat(testUserConfigSearch.getLastName()).isEqualTo(UPDATED_LAST_NAME);
                assertThat(testUserConfigSearch.getEmail()).isEqualTo(UPDATED_EMAIL);
                assertThat(testUserConfigSearch.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
            });
    }

    @Test
    @Transactional
    void putNonExistingUserConfig() throws Exception {
        int databaseSizeBeforeUpdate = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        userConfig.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userConfig.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userConfig))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserConfig in the database
        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserConfig() throws Exception {
        int databaseSizeBeforeUpdate = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        userConfig.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userConfig))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserConfig in the database
        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserConfig() throws Exception {
        int databaseSizeBeforeUpdate = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        userConfig.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserConfigMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userConfig)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserConfig in the database
        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateUserConfigWithPatch() throws Exception {
        // Initialize the database
        userConfigRepository.saveAndFlush(userConfig);

        int databaseSizeBeforeUpdate = userConfigRepository.findAll().size();

        // Update the userConfig using partial update
        UserConfig partialUpdatedUserConfig = new UserConfig();
        partialUpdatedUserConfig.setId(userConfig.getId());

        partialUpdatedUserConfig.lastName(UPDATED_LAST_NAME).phoneNumber(UPDATED_PHONE_NUMBER);

        restUserConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserConfig))
            )
            .andExpect(status().isOk());

        // Validate the UserConfig in the database
        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeUpdate);
        UserConfig testUserConfig = userConfigList.get(userConfigList.size() - 1);
        assertThat(testUserConfig.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(testUserConfig.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testUserConfig.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testUserConfig.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUserConfig.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void fullUpdateUserConfigWithPatch() throws Exception {
        // Initialize the database
        userConfigRepository.saveAndFlush(userConfig);

        int databaseSizeBeforeUpdate = userConfigRepository.findAll().size();

        // Update the userConfig using partial update
        UserConfig partialUpdatedUserConfig = new UserConfig();
        partialUpdatedUserConfig.setId(userConfig.getId());

        partialUpdatedUserConfig
            .login(UPDATED_LOGIN)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER);

        restUserConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserConfig))
            )
            .andExpect(status().isOk());

        // Validate the UserConfig in the database
        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeUpdate);
        UserConfig testUserConfig = userConfigList.get(userConfigList.size() - 1);
        assertThat(testUserConfig.getLogin()).isEqualTo(UPDATED_LOGIN);
        assertThat(testUserConfig.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUserConfig.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testUserConfig.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUserConfig.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void patchNonExistingUserConfig() throws Exception {
        int databaseSizeBeforeUpdate = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        userConfig.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userConfig))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserConfig in the database
        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserConfig() throws Exception {
        int databaseSizeBeforeUpdate = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        userConfig.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userConfig))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserConfig in the database
        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserConfig() throws Exception {
        int databaseSizeBeforeUpdate = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        userConfig.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserConfigMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userConfig))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserConfig in the database
        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteUserConfig() throws Exception {
        // Initialize the database
        userConfigRepository.saveAndFlush(userConfig);
        userConfigRepository.save(userConfig);
        userConfigSearchRepository.save(userConfig);

        int databaseSizeBeforeDelete = userConfigRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the userConfig
        restUserConfigMockMvc
            .perform(delete(ENTITY_API_URL_ID, userConfig.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserConfig> userConfigList = userConfigRepository.findAll();
        assertThat(userConfigList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userConfigSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchUserConfig() throws Exception {
        // Initialize the database
        userConfig = userConfigRepository.saveAndFlush(userConfig);
        userConfigSearchRepository.save(userConfig);

        // Search the userConfig
        restUserConfigMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + userConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)));
    }
}
