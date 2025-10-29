package mc.liu.polyrestructure.dto;

public class CompleteRequest {
    private String objectKey; // 唯一标识
    private String fileHash; // 二次校验

    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }
}
