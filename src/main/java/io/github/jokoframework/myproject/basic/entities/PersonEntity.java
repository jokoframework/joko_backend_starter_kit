package io.github.jokoframework.myproject.basic.entities;

import io.github.jokoframework.myproject.basic.enums.CivilStatusTypeEnum;
import io.github.jokoframework.myproject.basic.enums.SexTypeEnum;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author bsandoval
 *
 */
@Entity
@Table(name = "person", schema = "basic")
public class PersonEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2736822424446660507L;

    @GenericGenerator(
            name = "personSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "person_id_seq"),
                    @Parameter(name = "initial_value", value = "999999"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "personSequenceGenerator")
    @Basic(optional = false)
    @Column(name = "id")
    @NotNull
    private Long id;
    
    @Size(max = 250)
    @Column(name = "name")
    private String name;
    
    @Size(max = 255)
    @Column(name = "lastname")
    private String lastname;
    
    @NotNull
    @Basic(optional = false)
    @Column(name = "identification_number")
    @Size(min = 1, max = 100)
    private String identificationNumber;

    @Column(name = "birthdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthdate;

    @Column(name = "sex")
    @Enumerated(EnumType.STRING)
    private SexTypeEnum sex;

    @Column(name = "marital_status")
    @Enumerated(EnumType.STRING)
    private CivilStatusTypeEnum maritalStatus;
    
    @Size(min = 1, max = 100)
    @Column(name = "mobile_phone")
    private String mobilePhone;
    
    @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message="{email.invalid}")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 255)
    @Column(name = "email")
    private String email;
    
    @Size(min = 1, max = 100)
    @Column(name = "nationality")
    private String nationality;

    @OneToMany(mappedBy="person", cascade = CascadeType.MERGE, fetch = FetchType.EAGER, orphanRemoval=true)
    private List<AddressEntity> addressesList;

    public PersonEntity() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public SexTypeEnum getSex() {
        return sex;
    }

    public void setSex(SexTypeEnum sex) {
        this.sex = sex;
    }

    public CivilStatusTypeEnum getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(CivilStatusTypeEnum maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<AddressEntity> getAddressesList() {
        return addressesList;
    }

    public void setAddressesList(List<AddressEntity> addressesList) {
        this.addressesList = addressesList;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        PersonEntity that = (PersonEntity) o;

        return new EqualsBuilder().append(id, that.id).append(name, that.name).append(lastname, that.lastname)
                .append(identificationNumber, that.identificationNumber).append(birthdate, that.birthdate)
                .append(sex, that.sex).append(maritalStatus, that.maritalStatus).append(mobilePhone, that.mobilePhone)
                .append(email, that.email).append(nationality, that.nationality).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(name).append(lastname).append(identificationNumber)
                .append(birthdate).append(sex).append(maritalStatus).append(mobilePhone).append(email)
                .append(nationality).toHashCode();
    }

    @Override
    public String toString() {
        return "io.github.jokoframework.starterkit.entities.PersonEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", identificationNumber='" + identificationNumber + '\'' +
                ", birthdate=" + birthdate +
                ", sex=" + sex +
                ", maritalStatus=" + maritalStatus +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", email='" + email + '\'' +
                ", nationality='" + nationality + '\'' +
                ", addressesList=" + addressesList +
                '}';
    }
}
