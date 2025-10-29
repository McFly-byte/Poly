// src/main/java/mc/liu/polyrestructure/repository/ParsedRowRepository.java
package mc.liu.polyrestructure.repository;

import mc.liu.polyrestructure.entity.ParsedRowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParsedRowRepository extends JpaRepository<ParsedRowEntity, Long> {
    List<ParsedRowEntity> findByTaskIdOrderByRowIndex(String taskId);
}
