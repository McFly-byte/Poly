package mc.liu.polyrestructure.controller;

import mc.liu.polyrestructure.dto.*;
import mc.liu.polyrestructure.service.MinioService;
import mc.liu.polyrestructure.service.ParseTaskProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final MinioService minioService;
    private final ParseTaskProducer producer;

    public UploadController(MinioService minioService, ParseTaskProducer producer) {
        this.minioService = minioService;
        this.producer = producer;
    }

    /**
     * 前端调用/init，传来待上传文件的信息；
     * 后端告诉前端要把文件传到的url和minio上分配的路径
     */
    @PostMapping("/init")
    public ResponseEntity<InitResponse> init(@RequestBody InitRequest req) throws Exception {
        // 生成 objectKey: uploads/{uuid}_{filename}
        String objectKey = "uploads/" + UUID.randomUUID() + "_" + req.getFileName();
        String presignedUrl = minioService.presignPutObject(objectKey, Duration.ofMinutes(30));
        InitResponse resp = new InitResponse();
        resp.setObjectKey(objectKey);
        resp.setPresignedUrl(presignedUrl);
        return ResponseEntity.ok(resp);
    }

    /**
     * 前端传完之后，通知后端已上传对象的位置（objectKey)
     * complete: 前端在 PUT 完成后调用，后端将生成 taskId 并发消息入队（消费端后续实现）
     */
    @PostMapping("/complete")
    public ResponseEntity<ParseTaskDto> complete(@RequestBody CompleteRequest req) {
        String taskId = UUID.randomUUID().toString();
        ParseTaskDto dto = new ParseTaskDto();
        dto.setTaskId(taskId);
        dto.setObjectKey(req.getObjectKey());
        dto.setFileHash(req.getFileHash());
        // 发送到 RabbitMQ，消费者在后续阶段实现
        producer.sendParseTask(dto);
        return ResponseEntity.ok(dto);
    }
}
