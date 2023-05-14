package io.satoripop.time.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimeOffRequestMapperTest {

    private TimeOffRequestMapper timeOffRequestMapper;

    @BeforeEach
    public void setUp() {
        timeOffRequestMapper = new TimeOffRequestMapperImpl();
    }
}
