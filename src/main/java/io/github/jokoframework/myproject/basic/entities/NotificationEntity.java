package io.github.jokoframework.myproject.basic.entities;

import io.github.jokoframework.myproject.profile.entities.UserEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Entidad para manejar las notificaciones del sistema
 * 
 * @author FedeTraversi
 */
@Entity
@Table(name = "notification", schema = "basic")
public class NotificationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notificationSequenceGenerator")
    @SequenceGenerator(name = "notificationSequenceGenerator", sequenceName = "notification_id_seq",
            schema = "basic", allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    @NotNull
    private Long id;

    @Size(max = 255)
    @Column(name = "title")
    @NotNull
    private String title;

    @Size(max = 1000)
    @Column(name = "message")
    @NotNull
    private String message;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date createdDate;

    @Column(name = "read_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date readDate;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "user_id")
    @NotNull
    private Long userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserEntity user;

    @Size(max = 50)
    @Column(name = "category")
    @NotNull
    private String category;

    @Size(max = 50)
    @Column(name = "channel")
    @NotNull
    private String channel;

    public NotificationEntity() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationEntity that = (NotificationEntity) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(title, that.title)
                .append(message, that.message)
                .append(createdDate, that.createdDate)
                .append(readDate, that.readDate)
                .append(isRead, that.isRead)
                .append(userId, that.userId)
                .append(category, that.category)
                .append(channel, that.channel)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(title)
                .append(message)
                .append(createdDate)
                .append(readDate)
                .append(isRead)
                .append(userId)
                .append(category)
                .append(channel)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "NotificationEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", createdDate=" + createdDate +
                ", readDate=" + readDate +
                ", isRead=" + isRead +
                ", userId=" + userId +
                ", category='" + category + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}