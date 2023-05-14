package io.satoripop.time.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.satoripop.time.domain.WorkLog} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkLogDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer timeSpent;

    @NotNull
    private Instant date;

    private Long userId;

    private TicketDTO ticket;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(Integer timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TicketDTO getTicket() {
        return ticket;
    }

    public void setTicket(TicketDTO ticket) {
        this.ticket = ticket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkLogDTO)) {
            return false;
        }

        WorkLogDTO workLogDTO = (WorkLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, workLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkLogDTO{" +
            "id=" + getId() +
            ", timeSpent=" + getTimeSpent() +
            ", date='" + getDate() + "'" +
            ", userId=" + getUserId() +
            ", ticket=" + getTicket() +
            "}";
    }
}
