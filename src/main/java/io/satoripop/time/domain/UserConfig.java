package io.satoripop.time.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A UserConfig.
 */
@Entity
@Table(name = "user_config")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "userconfig")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @OneToMany(mappedBy = "user")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "ticket" }, allowSetters = true)
    private Set<WorkLog> workLogs = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Set<TimeOffRequest> timeOffRequests = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserConfig id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return this.login;
    }

    public UserConfig login(String login) {
        this.setLogin(login);
        return this;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public UserConfig firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public UserConfig lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public UserConfig email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public UserConfig phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<WorkLog> getWorkLogs() {
        return this.workLogs;
    }

    public void setWorkLogs(Set<WorkLog> workLogs) {
        if (this.workLogs != null) {
            this.workLogs.forEach(i -> i.setUser(null));
        }
        if (workLogs != null) {
            workLogs.forEach(i -> i.setUser(this));
        }
        this.workLogs = workLogs;
    }

    public UserConfig workLogs(Set<WorkLog> workLogs) {
        this.setWorkLogs(workLogs);
        return this;
    }

    public UserConfig addWorkLog(WorkLog workLog) {
        this.workLogs.add(workLog);
        workLog.setUser(this);
        return this;
    }

    public UserConfig removeWorkLog(WorkLog workLog) {
        this.workLogs.remove(workLog);
        workLog.setUser(null);
        return this;
    }

    public Set<TimeOffRequest> getTimeOffRequests() {
        return this.timeOffRequests;
    }

    public void setTimeOffRequests(Set<TimeOffRequest> timeOffRequests) {
        if (this.timeOffRequests != null) {
            this.timeOffRequests.forEach(i -> i.setUser(null));
        }
        if (timeOffRequests != null) {
            timeOffRequests.forEach(i -> i.setUser(this));
        }
        this.timeOffRequests = timeOffRequests;
    }

    public UserConfig timeOffRequests(Set<TimeOffRequest> timeOffRequests) {
        this.setTimeOffRequests(timeOffRequests);
        return this;
    }

    public UserConfig addTimeOffRequest(TimeOffRequest timeOffRequest) {
        this.timeOffRequests.add(timeOffRequest);
        timeOffRequest.setUser(this);
        return this;
    }

    public UserConfig removeTimeOffRequest(TimeOffRequest timeOffRequest) {
        this.timeOffRequests.remove(timeOffRequest);
        timeOffRequest.setUser(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserConfig)) {
            return false;
        }
        return id != null && id.equals(((UserConfig) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserConfig{" +
            "id=" + getId() +
            ", login='" + getLogin() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            "}";
    }
}
