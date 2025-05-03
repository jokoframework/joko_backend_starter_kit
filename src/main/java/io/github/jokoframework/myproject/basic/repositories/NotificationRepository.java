package io.github.jokoframework.myproject.basic.repositories;

import io.github.jokoframework.myproject.basic.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Notification Data Access
 *
 * @author FedeTraversi
 */
@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    
    List<NotificationEntity> findByUserIdOrderByCreatedDateDesc(Long userId);
    
    List<NotificationEntity> findByUserIdAndIsReadOrderByCreatedDateDesc(Long userId, Boolean isRead);
    
    Optional<NotificationEntity> findByIdAndUserId(Long id, Long userId);
    
    void deleteByIdAndUserId(Long id, Long userId);
}