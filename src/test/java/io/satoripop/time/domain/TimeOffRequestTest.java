package io.satoripop.time.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.satoripop.time.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimeOffRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeOffRequest.class);
        TimeOffRequest timeOffRequest1 = new TimeOffRequest();
        timeOffRequest1.setId(1L);
        TimeOffRequest timeOffRequest2 = new TimeOffRequest();
        timeOffRequest2.setId(timeOffRequest1.getId());
        assertThat(timeOffRequest1).isEqualTo(timeOffRequest2);
        timeOffRequest2.setId(2L);
        assertThat(timeOffRequest1).isNotEqualTo(timeOffRequest2);
        timeOffRequest1.setId(null);
        assertThat(timeOffRequest1).isNotEqualTo(timeOffRequest2);
    }
}
