package io.satoripop.time.domain;

import io.satoripop.time.domain.enumeration.TimeOffRequestStatus;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A TimeOffRequest.
 */
@Entity
@Table(name = "time_off_request")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TimeOffRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TimeOffRequestStatus status;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "leave_reason")
    private String leaveReason;

    @Column(name = "user_name")
    private String userName;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TimeOffRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public TimeOffRequest startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public TimeOffRequest endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public TimeOffRequestStatus getStatus() {
        return this.status;
    }

    public TimeOffRequest status(TimeOffRequestStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TimeOffRequestStatus status) {
        this.status = status;
    }

    public Long getUserId() {
        return this.userId;
    }

    public TimeOffRequest userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLeaveReason() {
        return this.leaveReason;
    }

    public TimeOffRequest leaveReason(String leaveReason) {
        this.setLeaveReason(leaveReason);
        return this;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public String getUserName() {
        return this.userName;
    }

    public TimeOffRequest userName(String userName) {
        this.setUserName(userName);
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeOffRequest)) {
            return false;
        }
        return id != null && id.equals(((TimeOffRequest) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimeOffRequest{" +
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
