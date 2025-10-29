# Poly
前端在上传前先调用/init 取直传URL，之后直接将xlsx文件上传到MinIO。 
上传结束后，调用/complete通知后端。 
后端由ParseTaskProducer发给 RabbitMQ 的 excel.parse.queue 中。
ExcelParseConsumer一直监听这个队列，收到消息就调用ExcelParserService.handleParseTask开始解析。 
解析前先在数据库中创建条目，创建minio输入流minioService.getObjectStream(objectKey)，然后调用ExcelParserService.parseAndSave开始解析。
解析中周期性调用 ProgressPublisher.publish 将进度信息推送至Redis（通过Redis的Pub/Sub发布功能）。
所有订阅了progress:*的客户端都会收到（其实只有RedisMessageListenerContainer）。 
RedisProgressSubscriber收到message，继续调用WebSocketSessionRegistry.sendToTaskSessions将消息广播给所有订阅了此taskId的WebSocketSession。
前端通过 WebSocketConfig 注册的 /ws/progress 建立ws连接，更新进度条。
