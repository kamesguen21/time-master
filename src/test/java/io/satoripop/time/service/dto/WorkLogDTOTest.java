package io.satoripop.time.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.satoripop.time.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkLogDTO.class);
        WorkLogDTO workLogDTO1 = new WorkLogDTO();
        workLogDTO1.setId(1L);
        WorkLogDTO workLogDTO2 = new WorkLogDTO();
        assertThat(workLogDTO1).isNotEqualTo(workLogDTO2);
        workLogDTO2.setId(workLogDTO1.getId());
        assertThat(workLogDTO1).isEqualTo(workLogDTO2);
        workLogDTO2.setId(2L);
        assertThat(workLogDTO1).isNotEqualTo(workLogDTO2);
        workLogDTO1.setId(null);
        assertThat(workLogDTO1).isNotEqualTo(workLogDTO2);
    }
}
