package mc.liu.polyrestructure.dto;

public class InitResponse {
    private String objectKey; // 存到minio上的路径
    private String presignedUrl; // 前端直接将文件put到这个url，而不是后端

    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public String getPresignedUrl() { return presignedUrl; }
    public void setPresignedUrl(String presignedUrl) { this.presignedUrl = presignedUrl; }
}
