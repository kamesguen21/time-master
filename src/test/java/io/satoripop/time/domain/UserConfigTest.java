package io.satoripop.time.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.satoripop.time.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserConfigTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserConfig.class);
        UserConfig userConfig1 = new UserConfig();
        userConfig1.setId(1L);
        UserConfig userConfig2 = new UserConfig();
        userConfig2.setId(userConfig1.getId());
        assertThat(userConfig1).isEqualTo(userConfig2);
        userConfig2.setId(2L);
        assertThat(userConfig1).isNotEqualTo(userConfig2);
        userConfig1.setId(null);
        assertThat(userConfig1).isNotEqualTo(userConfig2);
    }
}
