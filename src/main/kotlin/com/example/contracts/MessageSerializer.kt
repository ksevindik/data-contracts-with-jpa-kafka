package com.example.contracts

import com.google.protobuf.Message
import org.apache.kafka.common.serialization.Serializer

class MessageSerializer : Serializer<Any> {
   override fun serialize(topic: String?, data: Any?): ByteArray {
       if (data is Message) {
           return data.toByteArray()
       } else if (data is String) {
           return data.toByteArray()
       } else {
           // try to serialize non-recognized records as raw bytes
           return data as ByteArray
       }
   }
}

