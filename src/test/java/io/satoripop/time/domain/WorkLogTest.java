package io.satoripop.time.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.satoripop.time.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkLog.class);
        WorkLog workLog1 = new WorkLog();
        workLog1.setId(1L);
        WorkLog workLog2 = new WorkLog();
        workLog2.setId(workLog1.getId());
        assertThat(workLog1).isEqualTo(workLog2);
        workLog2.setId(2L);
        assertThat(workLog1).isNotEqualTo(workLog2);
        workLog1.setId(null);
        assertThat(workLog1).isNotEqualTo(workLog2);
    }
}
