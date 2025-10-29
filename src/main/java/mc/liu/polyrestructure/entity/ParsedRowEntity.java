// src/main/java/mc/liu/polyrestructure/entity/ParsedRowEntity.java
package mc.liu.polyrestructure.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parsed_row")
public class ParsedRowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="task_id")
    private String taskId;

    @Column(name="row_index")
    private Integer rowIndex;

    @Column(name="col1_url", length = 1024)
    private String col1Url;

    @Column(name="col2_url", length = 1024)
    private String col2Url;

    @Column(name="col3_url", length = 1024)
    private String col3Url;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public Integer getRowIndex() { return rowIndex; }
    public void setRowIndex(Integer rowIndex) { this.rowIndex = rowIndex; }
    public String getCol1Url() { return col1Url; }
    public void setCol1Url(String col1Url) { this.col1Url = col1Url; }
    public String getCol2Url() { return col2Url; }
    public void setCol2Url(String col2Url) { this.col2Url = col2Url; }
    public String getCol3Url() { return col3Url; }
    public void setCol3Url(String col3Url) { this.col3Url = col3Url; }
}
