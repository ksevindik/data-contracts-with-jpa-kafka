package com.example.contracts

import com.google.protobuf.Any
import com.google.protobuf.Message
import com.google.protobuf.MessageLite
import com.google.protobuf.Timestamp
import com.google.protobuf.util.JsonFormat
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.util.ReflectionUtils
import java.util.Date

@Component
class DataContractMessagePublisher(
    val kafkaTemplate: KafkaTemplate<String, MessageLite>,
    val dataContractMessageEntityRepository: DataContractMessageEntityRepository
) {

    @Scheduled(fixedDelay = 1000)
    fun publishMessages() {
        val unpublishedMessages = dataContractMessageEntityRepository.findByPublishedOrderByCreatedAtAsc(false)
        unpublishedMessages.forEach { messageEntity ->
            val payload = createPayload(messageEntity.payload, messageEntity.payloadType)
            val metadata = createMetadata(messageEntity)

            val messageProto = DataContractMessage.newBuilder()
                .setMetadata(metadata)
                .setPayload(Any.pack(payload)).build()

            val producerRecord = ProducerRecord<String, MessageLite>(
                messageEntity.messageTopic,
                messageEntity.messageKey,
                messageProto
            )

            kafkaTemplate.send(producerRecord).get()
            messageEntity.published = true
            messageEntity.updatedAt = Date()
            dataContractMessageEntityRepository.save(messageEntity)
        }
    }

    private fun createMetadata(messageEntity: DataContractMessageEntity): DataContractMessageMetadata? =
        DataContractMessageMetadata.newBuilder()
            .setMessageType(DataContractMessageType.valueOf(messageEntity.messageType))
            .setMessageId(messageEntity.id!!)
            .setEventType(messageEntity.eventType)
            .setPublishedAt(dateToTimestamp(Date()))
            .build()

    private fun createPayload(payload: String, payloadType: String): Message {
        val protoType = Class.forName(payloadType)
        val defaultInstance = ReflectionUtils.findMethod(protoType, "getDefaultInstance")!!.invoke(null)
        val msgBuilder = (defaultInstance as Message).newBuilderForType()
        val parser = JsonFormat.parser()
        parser.merge(payload, msgBuilder)
        return msgBuilder.build()
    }

    private fun dateToTimestamp(date: Date): Timestamp =
        Timestamp.newBuilder().setSeconds(date.time / 1000).setNanos((date.time % 1000).toInt() * 1000000).build()
}
