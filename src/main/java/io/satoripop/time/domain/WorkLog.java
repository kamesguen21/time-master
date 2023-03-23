package io.satoripop.time.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A WorkLog.
 */
@Entity
@Table(name = "work_log")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "worklog")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "time_spent", nullable = false)
    private Integer timeSpent;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @ManyToOne
    @JsonIgnoreProperties(value = { "workLogs", "timeOffRequests" }, allowSetters = true)
    private UserConfig user;

    @ManyToOne
    @JsonIgnoreProperties(value = { "workLogs" }, allowSetters = true)
    private Ticket ticket;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WorkLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTimeSpent() {
        return this.timeSpent;
    }

    public WorkLog timeSpent(Integer timeSpent) {
        this.setTimeSpent(timeSpent);
        return this;
    }

    public void setTimeSpent(Integer timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Instant getDate() {
        return this.date;
    }

    public WorkLog date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public UserConfig getUser() {
        return this.user;
    }

    public void setUser(UserConfig userConfig) {
        this.user = userConfig;
    }

    public WorkLog user(UserConfig userConfig) {
        this.setUser(userConfig);
        return this;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public WorkLog ticket(Ticket ticket) {
        this.setTicket(ticket);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkLog)) {
            return false;
        }
        return id != null && id.equals(((WorkLog) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkLog{" +
            "id=" + getId() +
            ", timeSpent=" + getTimeSpent() +
            ", date='" + getDate() + "'" +
            "}";
    }
}