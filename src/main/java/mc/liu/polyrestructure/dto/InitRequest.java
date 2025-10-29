package mc.liu.polyrestructure.dto;

public class InitRequest {
    private String fileName;
    private long fileSize; // 计划文件大小
    private long partSize; // 计划单片大小
    private String fileHash; // 文件hash

    // getters / setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public long getPartSize() { return partSize; }
    public void setPartSize(long partSize) { this.partSize = partSize; }
    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }
}
