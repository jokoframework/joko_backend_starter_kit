package io.github.jokoframework.myproject.basic.entities;

import io.github.jokoframework.myproject.basic.enums.AddressTypeEnum;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


/**
 * 
 * @author bsandoval
 *
 */
@Entity
@Table(name = "address", schema = "basic")
public class AddressEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @GenericGenerator(
            name = "addressSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "address_id_seq"),
                    @Parameter(name = "initial_value", value = "999999"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "addressSequenceGenerator")
    @Basic(optional = false)
    @Column(name = "id")
    @NotNull
    private Long id;
    
    @Column(name="address")
    @Size(min=1, max = 255)
    @Basic(optional = false)
    @NotNull
    private String address;
    
    @Column(name="neighborhood")
    @Size(min=1, max = 100)
    @Basic(optional = true)
    private String neighborhood;
    
    @Column(name="city")
    @Size(min=1, max = 100)
    @Basic(optional = false)
    @NotNull
    private String city;
    
    @Column(name="country_code")
    @Size(max=3)
    @Basic(optional = false)
    @NotNull
    private String countryCode;
    
    @Column(name="type")
    @Basic(optional = false)
    @NotNull
    private AddressTypeEnum type;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "person_id")
    private Long personId;
    
    @ManyToOne(optional=false, cascade=CascadeType.MERGE)
    @JoinColumn(name="person_id", referencedColumnName="id", insertable=false, updatable=false)
    private PersonEntity person;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public AddressTypeEnum getType() {
        return type;
    }

    public void setType(AddressTypeEnum type) {
        this.type = type;
    }

    public PersonEntity getPerson() {
        return person;
    }

    public void setPerson(PersonEntity person) {
        this.person = person;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AddressEntity that = (AddressEntity) o;

        return new EqualsBuilder().append(id, that.id).append(address, that.address)
                .append(neighborhood, that.neighborhood).append(city, that.city).append(countryCode, that.countryCode)
                .append(type, that.type).append(personId, that.personId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(address).append(neighborhood).append(city)
                .append(countryCode).append(type).append(personId).toHashCode();
    }

    @Override public String toString() {
        return "io.github.jokoframework.starterkit.entities.AddressEntity{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", city='" + city + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", type=" + type +
                ", personId=" + personId +
                ", person=" + person +
                '}';
    }
}
