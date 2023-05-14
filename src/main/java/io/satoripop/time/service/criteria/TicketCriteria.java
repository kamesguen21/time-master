package io.satoripop.time.service.criteria;

import io.satoripop.time.domain.enumeration.TicketStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.satoripop.time.domain.Ticket} entity. This class is used
 * in {@link io.satoripop.time.web.rest.TicketResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tickets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TicketStatus
     */
    public static class TicketStatusFilter extends Filter<TicketStatus> {

        public TicketStatusFilter() {}

        public TicketStatusFilter(TicketStatusFilter filter) {
            super(filter);
        }

        @Override
        public TicketStatusFilter copy() {
            return new TicketStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter jiraKey;

    private StringFilter summary;

    private StringFilter description;

    private LongFilter userId;

    private TicketStatusFilter status;

    private LongFilter workLogId;

    private Boolean distinct;

    public TicketCriteria() {}

    public TicketCriteria(TicketCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.jiraKey = other.jiraKey == null ? null : other.jiraKey.copy();
        this.summary = other.summary == null ? null : other.summary.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.workLogId = other.workLogId == null ? null : other.workLogId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TicketCriteria copy() {
        return new TicketCriteria(this);
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

    public StringFilter getJiraKey() {
        return jiraKey;
    }

    public StringFilter jiraKey() {
        if (jiraKey == null) {
            jiraKey = new StringFilter();
        }
        return jiraKey;
    }

    public void setJiraKey(StringFilter jiraKey) {
        this.jiraKey = jiraKey;
    }

    public StringFilter getSummary() {
        return summary;
    }

    public StringFilter summary() {
        if (summary == null) {
            summary = new StringFilter();
        }
        return summary;
    }

    public void setSummary(StringFilter summary) {
        this.summary = summary;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
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

    public TicketStatusFilter getStatus() {
        return status;
    }

    public TicketStatusFilter status() {
        if (status == null) {
            status = new TicketStatusFilter();
        }
        return status;
    }

    public void setStatus(TicketStatusFilter status) {
        this.status = status;
    }

    public LongFilter getWorkLogId() {
        return workLogId;
    }

    public LongFilter workLogId() {
        if (workLogId == null) {
            workLogId = new LongFilter();
        }
        return workLogId;
    }

    public void setWorkLogId(LongFilter workLogId) {
        this.workLogId = workLogId;
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
        final TicketCriteria that = (TicketCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(jiraKey, that.jiraKey) &&
            Objects.equals(summary, that.summary) &&
            Objects.equals(description, that.description) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(status, that.status) &&
            Objects.equals(workLogId, that.workLogId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jiraKey, summary, description, userId, status, workLogId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (jiraKey != null ? "jiraKey=" + jiraKey + ", " : "") +
            (summary != null ? "summary=" + summary + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (workLogId != null ? "workLogId=" + workLogId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
