package io.satoripop.time.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.satoripop.time.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimeOffRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeOffRequestDTO.class);
        TimeOffRequestDTO timeOffRequestDTO1 = new TimeOffRequestDTO();
        timeOffRequestDTO1.setId(1L);
        TimeOffRequestDTO timeOffRequestDTO2 = new TimeOffRequestDTO();
        assertThat(timeOffRequestDTO1).isNotEqualTo(timeOffRequestDTO2);
        timeOffRequestDTO2.setId(timeOffRequestDTO1.getId());
        assertThat(timeOffRequestDTO1).isEqualTo(timeOffRequestDTO2);
        timeOffRequestDTO2.setId(2L);
        assertThat(timeOffRequestDTO1).isNotEqualTo(timeOffRequestDTO2);
        timeOffRequestDTO1.setId(null);
        assertThat(timeOffRequestDTO1).isNotEqualTo(timeOffRequestDTO2);
    }
}
