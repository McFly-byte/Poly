// src/main/java/mc/liu/polyrestructure/service/ExcelParserService.java
package mc.liu.polyrestructure.service;

import mc.liu.polyrestructure.entity.ParsedRowEntity;
import mc.liu.polyrestructure.entity.UploadTaskEntity;
import mc.liu.polyrestructure.repository.ParsedRowRepository;
import mc.liu.polyrestructure.repository.UploadTaskRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

@Service
public class ExcelParserService {

    private final MinioService minioService;
    private final ParsedRowRepository parsedRowRepository;
    private final UploadTaskRepository uploadTaskRepository;
    private final ProgressPublisher progressPublisher;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.bucket}")
    private String minioBucket;

    public ExcelParserService(MinioService minioService,
                              ParsedRowRepository parsedRowRepository,
                              UploadTaskRepository uploadTaskRepository,
                              ProgressPublisher progressPublisher) {
        this.minioService = minioService;
        this.parsedRowRepository = parsedRowRepository;
        this.uploadTaskRepository = uploadTaskRepository;
        this.progressPublisher = progressPublisher;
    }

    /**
     * Demo parser: uses XSSFWorkbook (loads full file). Extract pictures by anchors,
     * uploads them to MinIO, and writes parsed_row entries for each row with first 3 image URLs.
     */
    public void parseAndSave(String taskId, InputStream excelStream) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook(excelStream);
        try {
            int totalRows = 0;
            // For simplicity assume first sheet
            XSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet == null) return;

            // build image map: row -> col -> imageUrl
            Map<Integer, Map<Integer, String>> imageUrlMap = new HashMap<>();

            XSSFDrawing drawing = sheet.getDrawingPatriarch();
            if (drawing != null) {
                for (XSSFShape shape : drawing.getShapes()) {
                    if (shape instanceof XSSFPicture) {
                        XSSFPicture pic = (XSSFPicture) shape;
                        XSSFClientAnchor anchor = (XSSFClientAnchor) pic.getClientAnchor();
                        int row = anchor.getRow1();
                        int col = anchor.getCol1();
                        XSSFPictureData picData = pic.getPictureData();
                        byte[] data = picData.getData();
                        String ext = picData.suggestFileExtension();
                        String mime = picData.getMimeType();
                        String imageObjectKey = String.format("images/%s/%d_%d.%s", taskId, row, col, ext);
                        // upload
                        minioService.putObjectStream(imageObjectKey, new ByteArrayInputStream(data), data.length, mime);
                        // build URL
                        String url = minioEndpoint + "/" + minioBucket + "/" + imageObjectKey;
                        imageUrlMap.computeIfAbsent(row, r -> new HashMap<>()).put(col, url);
                    }
                }
            }

            // count total rows
            totalRows = sheet.getLastRowNum() + 1;

            int processed = 0;
            int publishInterval = Math.max(1, totalRows / 100); // publish ~100 updates
            for (Row row : sheet) {
                int rowIndex = row.getRowNum();
                Map<Integer, String> rowPics = imageUrlMap.getOrDefault(rowIndex, Collections.emptyMap());
                String c1 = rowPics.getOrDefault(0, null);
                String c2 = rowPics.getOrDefault(1, null);
                String c3 = rowPics.getOrDefault(2, null);

                ParsedRowEntity pre = new ParsedRowEntity();
                pre.setTaskId(taskId);
                pre.setRowIndex(rowIndex);
                pre.setCol1Url(c1);
                pre.setCol2Url(c2);
                pre.setCol3Url(c3);
                parsedRowRepository.save(pre);

                processed++;
                if (processed % publishInterval == 0 || processed == totalRows) {
                    int percent = (int) ((processed / (double) totalRows) * 100);
                    progressPublisher.publish(taskId, percent, processed, totalRows);
                }
            }
        } finally {
            workbook.close();
        }
    }

    /**
     * wrapper: create or update upload_task entity status
     */
    @Transactional
    public void handleParseTask(String taskId, String objectKey, String fileHash) throws Exception {
        // 关键：如果已存在同 taskId 的记录，就拿出来“更新”；不存在才新建
        UploadTaskEntity t = uploadTaskRepository.findByTaskId(taskId)
                .orElseGet(UploadTaskEntity::new);
        t.setTaskId(taskId);
        t.setObjectKey(objectKey);
        t.setFileHash(fileHash);
        t.setStatus("RUNNING");
        uploadTaskRepository.save(t);

        try (InputStream in = minioService.getObjectStream(objectKey)) {
            parseAndSave(taskId, in);
            t.setStatus("SUCCEEDED");
        } catch (Exception e) {
            t.setStatus("FAILED");
            throw e;
        } finally {
            uploadTaskRepository.save(t);
        }
    }
}
