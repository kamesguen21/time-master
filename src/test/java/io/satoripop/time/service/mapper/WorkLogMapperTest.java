package io.satoripop.time.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkLogMapperTest {

    private WorkLogMapper workLogMapper;

    @BeforeEach
    public void setUp() {
        workLogMapper = new WorkLogMapperImpl();
    }
}
