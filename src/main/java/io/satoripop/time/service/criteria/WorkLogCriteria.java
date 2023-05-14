package io.satoripop.time.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.satoripop.time.domain.WorkLog} entity. This class is used
 * in {@link io.satoripop.time.web.rest.WorkLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /work-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkLogCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter timeSpent;

    private InstantFilter date;

    private LongFilter userId;

    private LongFilter ticketId;

    private Boolean distinct;

    public WorkLogCriteria() {}

    public WorkLogCriteria(WorkLogCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.timeSpent = other.timeSpent == null ? null : other.timeSpent.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.ticketId = other.ticketId == null ? null : other.ticketId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public WorkLogCriteria copy() {
        return new WorkLogCriteria(this);
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

    public IntegerFilter getTimeSpent() {
        return timeSpent;
    }

    public IntegerFilter timeSpent() {
        if (timeSpent == null) {
            timeSpent = new IntegerFilter();
        }
        return timeSpent;
    }

    public void setTimeSpent(IntegerFilter timeSpent) {
        this.timeSpent = timeSpent;
    }

    public InstantFilter getDate() {
        return date;
    }

    public InstantFilter date() {
        if (date == null) {
            date = new InstantFilter();
        }
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
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

    public LongFilter getTicketId() {
        return ticketId;
    }

    public LongFilter ticketId() {
        if (ticketId == null) {
            ticketId = new LongFilter();
        }
        return ticketId;
    }

    public void setTicketId(LongFilter ticketId) {
        this.ticketId = ticketId;
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
        final WorkLogCriteria that = (WorkLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(timeSpent, that.timeSpent) &&
            Objects.equals(date, that.date) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(ticketId, that.ticketId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeSpent, date, userId, ticketId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkLogCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (timeSpent != null ? "timeSpent=" + timeSpent + ", " : "") +
            (date != null ? "date=" + date + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (ticketId != null ? "ticketId=" + ticketId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
