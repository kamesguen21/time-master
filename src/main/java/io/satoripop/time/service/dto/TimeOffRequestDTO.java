package io.satoripop.time.service.dto;

import io.satoripop.time.domain.enumeration.TimeOffRequestStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.satoripop.time.domain.TimeOffRequest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TimeOffRequestDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant startDate;

    @NotNull
    private Instant endDate;

    private TimeOffRequestStatus status;

    private Long userId;

    private String leaveReason;

    private String userName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public TimeOffRequestStatus getStatus() {
        return status;
    }

    public void setStatus(TimeOffRequestStatus status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeOffRequestDTO)) {
            return false;
        }

        TimeOffRequestDTO timeOffRequestDTO = (TimeOffRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, timeOffRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimeOffRequestDTO{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", userId=" + getUserId() +
            ", leaveReason='" + getLeaveReason() + "'" +
            ", userName='" + getUserName() + "'" +
            "}";
    }
}
