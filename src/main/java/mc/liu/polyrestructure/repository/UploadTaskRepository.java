// src/main/java/mc/liu/polyrestructure/repository/UploadTaskRepository.java
package mc.liu.polyrestructure.repository;

import mc.liu.polyrestructure.entity.UploadTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UploadTaskRepository extends JpaRepository<UploadTaskEntity, Long> {
    Optional<UploadTaskEntity> findByTaskId(String taskId);
}
