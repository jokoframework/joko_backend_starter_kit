package io.github.jokoframework.myproject.basic.entities;

import io.github.jokoframework.myproject.basic.enums.AddressTypeEnum;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addressSequenceGenerator")
    @SequenceGenerator(name = "addressSequenceGenerator", sequenceName = "address_id_seq",
            schema = "basic", allocationSize = 1)
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
