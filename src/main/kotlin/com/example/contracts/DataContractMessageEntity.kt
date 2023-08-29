package com.example.contracts

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import java.util.Date

@Entity
data class DataContractMessageEntity(
    var messageType: String,
    var eventType: String,
    var payloadType: String,
    @Lob var payload: String,
    var messageTopic: String,
    var messageKey: String,
    var published: Boolean = false
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id:Long? = null

    @Temporal(TemporalType.TIMESTAMP)
    var createdAt: Date = Date()

    @Temporal(TemporalType.TIMESTAMP)
    var updatedAt: Date = Date()
}
