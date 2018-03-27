package io.github.jokoframework.myproject.profile.entities;

import io.github.jokoframework.myproject.basic.entities.PersonEntity;
import io.github.jokoframework.myproject.basic.enums.AccessLevelEnum;
import io.github.jokoframework.myproject.profile.dto.UserDTO;
import io.github.jokoframework.utils.dto_mapping.BaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 
 * @author bsandoval
 *
 */
@Entity
@Table(name = "user", schema = "profile")
public class UserEntity extends BaseEntity<UserDTO> {

    private static final long serialVersionUID = 1L;
    
    @GenericGenerator(
            name = "userSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "user_id_seq"),
                    @Parameter(name = "initial_value", value = "999999"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "userSequenceGenerator")
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "username")
    private String username;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password")
    private String password;

    @Basic(optional = false)
    @NotNull
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "last_access_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAccessDate;
    
    @Column(name = "profile")
    @Enumerated(EnumType.STRING)
    private AccessLevelEnum profile;

    @Column(name = "person_id")
    private Long personId;

    @JoinColumn(name = "person_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private PersonEntity person;
    
    public UserEntity() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(Date lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }
    
    public AccessLevelEnum getProfile() {
        return profile;
    }

    public void setProfile(AccessLevelEnum profile) {
        this.profile = profile;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public PersonEntity getPerson() {
        return person;
    }

    public void setPerson(PersonEntity person) {
        this.person = person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserEntity that = (UserEntity) o;

        return new EqualsBuilder().append(id, that.id).append(username, that.username).append(password, that.password)
                .append(created, that.created).append(lastAccessDate, that.lastAccessDate)
                .append(profile, that.profile).append(personId, that.personId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(username).append(password).append(created)
                .append(profile).append(lastAccessDate).append(personId).toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserEntity{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append(", created=").append(created);
        sb.append(", lastAccessDate=").append(lastAccessDate);
        sb.append(", personId=").append(personId);
        sb.append(", profile=").append(profile);
        sb.append(", person=").append(person);
        sb.append('}');
        return sb.toString();
    }
}
