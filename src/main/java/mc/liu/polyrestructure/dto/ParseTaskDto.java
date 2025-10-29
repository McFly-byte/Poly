package mc.liu.polyrestructure.dto;

public class ParseTaskDto {
    private String taskId; // 任务唯一id
    private String objectKey; // 文件在minio上的路径
    private String fileHash; // 用于校验
    private String userId; // 发起任务的用户标识

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
