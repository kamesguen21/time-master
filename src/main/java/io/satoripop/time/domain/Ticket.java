package io.satoripop.time.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.satoripop.time.domain.enumeration.TicketStatus;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Ticket.
 */
@Entity
@Table(name = "ticket")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "jira_key", nullable = false)
    private String jiraKey;

    @NotNull
    @Column(name = "summary", nullable = false)
    private String summary;

    @Column(name = "description")
    private String description;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TicketStatus status;

    @Column(name = "user_name")
    private String userName;

    @OneToMany(mappedBy = "ticket", fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "ticket" }, allowSetters = true)
    private Set<WorkLog> workLogs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ticket id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJiraKey() {
        return this.jiraKey;
    }

    public Ticket jiraKey(String jiraKey) {
        this.setJiraKey(jiraKey);
        return this;
    }

    public void setJiraKey(String jiraKey) {
        this.jiraKey = jiraKey;
    }

    public String getSummary() {
        return this.summary;
    }

    public Ticket summary(String summary) {
        this.setSummary(summary);
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return this.description;
    }

    public Ticket description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Ticket userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TicketStatus getStatus() {
        return this.status;
    }

    public Ticket status(TicketStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getUserName() {
        return this.userName;
    }

    public Ticket userName(String userName) {
        this.setUserName(userName);
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<WorkLog> getWorkLogs() {
        return this.workLogs;
    }

    public void setWorkLogs(Set<WorkLog> workLogs) {
        if (this.workLogs != null) {
            this.workLogs.forEach(i -> i.setTicket(null));
        }
        if (workLogs != null) {
            workLogs.forEach(i -> i.setTicket(this));
        }
        this.workLogs = workLogs;
    }

    public Ticket workLogs(Set<WorkLog> workLogs) {
        this.setWorkLogs(workLogs);
        return this;
    }

    public Ticket addWorkLog(WorkLog workLog) {
        this.workLogs.add(workLog);
        workLog.setTicket(this);
        return this;
    }

    public Ticket removeWorkLog(WorkLog workLog) {
        this.workLogs.remove(workLog);
        workLog.setTicket(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ticket)) {
            return false;
        }
        return id != null && id.equals(((Ticket) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ticket{" +
            "id=" + getId() +
            ", jiraKey='" + getJiraKey() + "'" +
            ", summary='" + getSummary() + "'" +
            ", description='" + getDescription() + "'" +
            ", userId=" + getUserId() +
            ", status='" + getStatus() + "'" +
            ", userName='" + getUserName() + "'" +
            "}";
    }
}
