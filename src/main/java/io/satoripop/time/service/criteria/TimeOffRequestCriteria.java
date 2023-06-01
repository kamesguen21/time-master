package io.satoripop.time.service.criteria;

import io.satoripop.time.domain.enumeration.TimeOffRequestStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.satoripop.time.domain.TimeOffRequest} entity. This class is used
 * in {@link io.satoripop.time.web.rest.TimeOffRequestResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /time-off-requests?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TimeOffRequestCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TimeOffRequestStatus
     */
    public static class TimeOffRequestStatusFilter extends Filter<TimeOffRequestStatus> {

        public TimeOffRequestStatusFilter() {}

        public TimeOffRequestStatusFilter(TimeOffRequestStatusFilter filter) {
            super(filter);
        }

        @Override
        public TimeOffRequestStatusFilter copy() {
            return new TimeOffRequestStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter startDate;

    private InstantFilter endDate;

    private TimeOffRequestStatusFilter status;

    private LongFilter userId;

    private StringFilter leaveReason;

    private StringFilter userName;

    private Boolean distinct;

    public TimeOffRequestCriteria() {}

    public TimeOffRequestCriteria(TimeOffRequestCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.startDate = other.startDate == null ? null : other.startDate.copy();
        this.endDate = other.endDate == null ? null : other.endDate.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.leaveReason = other.leaveReason == null ? null : other.leaveReason.copy();
        this.userName = other.userName == null ? null : other.userName.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TimeOffRequestCriteria copy() {
        return new TimeOffRequestCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getStartDate() {
        return startDate;
    }

    public InstantFilter startDate() {
        if (startDate == null) {
            startDate = new InstantFilter();
        }
        return startDate;
    }

    public void setStartDate(InstantFilter startDate) {
        this.startDate = startDate;
    }

    public InstantFilter getEndDate() {
        return endDate;
    }

    public InstantFilter endDate() {
        if (endDate == null) {
            endDate = new InstantFilter();
        }
        return endDate;
    }

    public void setEndDate(InstantFilter endDate) {
        this.endDate = endDate;
    }

    public TimeOffRequestStatusFilter getStatus() {
        return status;
    }

    public TimeOffRequestStatusFilter status() {
        if (status == null) {
            status = new TimeOffRequestStatusFilter();
        }
        return status;
    }

    public void setStatus(TimeOffRequestStatusFilter status) {
        this.status = status;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public StringFilter getLeaveReason() {
        return leaveReason;
    }

    public StringFilter leaveReason() {
        if (leaveReason == null) {
            leaveReason = new StringFilter();
        }
        return leaveReason;
    }

    public void setLeaveReason(StringFilter leaveReason) {
        this.leaveReason = leaveReason;
    }

    public StringFilter getUserName() {
        return userName;
    }

    public StringFilter userName() {
        if (userName == null) {
            userName = new StringFilter();
        }
        return userName;
    }

    public void setUserName(StringFilter userName) {
        this.userName = userName;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TimeOffRequestCriteria that = (TimeOffRequestCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(leaveReason, that.leaveReason) &&
            Objects.equals(userName, that.userName) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, endDate, status, userId, leaveReason, userName, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimeOffRequestCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (startDate != null ? "startDate=" + startDate + ", " : "") +
            (endDate != null ? "endDate=" + endDate + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (leaveReason != null ? "leaveReason=" + leaveReason + ", " : "") +
            (userName != null ? "userName=" + userName + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
