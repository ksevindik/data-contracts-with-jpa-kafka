package com.example.contracts

import org.hibernate.event.spi.DeleteContext
import org.hibernate.event.spi.DeleteEvent
import org.hibernate.event.spi.DeleteEventListener
import org.hibernate.event.spi.EventSource
import org.hibernate.event.spi.FlushEntityEvent
import org.hibernate.event.spi.FlushEntityEventListener
import org.hibernate.event.spi.PersistContext
import org.hibernate.event.spi.PersistEvent
import org.hibernate.event.spi.PersistEventListener

class EntityChangeEventListener : PersistEventListener, FlushEntityEventListener, DeleteEventListener {

    private fun saveDataContractMessage(
        entity: DataContractMessageAware,
        eventType: String,
        session: EventSource
    ) {
        val jsonPayload = entity.getMessagePayloadAsJSON()
        val payloadType = entity.getMessagePayloadType()
        val outboxMessage = DataContractMessageEntity(
            DataContractMessageType.ENTITY_CHANGE.name,
            eventType,
            payloadType.qualifiedName!!,
            jsonPayload,
            DataContractMessageTopicResolver().resolve(entity::class),
            entity.getMessageKey(),
            false
        )
        session.persist(outboxMessage)
    }

    override fun onPersist(event: PersistEvent) {
        doOnPersist(event)
    }

    override fun onPersist(event: PersistEvent, createdAlready: PersistContext?) {
        doOnPersist(event)
    }

    private fun doOnPersist(event: PersistEvent) {
        val entity = event.`object`
        val session = event.session
        if (entity is DataContractMessageAware) {
            saveDataContractMessage(entity, "INSERT", session)
        }
    }

    override fun onFlushEntity(event: FlushEntityEvent) {
        val entity = event.entity
        val session = event.session
        if (entity is DataContractMessageAware && (event.hasDirtyProperties() || event.hasDirtyCollection())) {
            saveDataContractMessage(entity, "UPDATE", session)
        }
    }

    override fun onDelete(event: DeleteEvent) {
        doOnDelete(event)
    }

    override fun onDelete(event: DeleteEvent, transientEntities: DeleteContext?) {
        doOnDelete(event)
    }

    private fun doOnDelete(event: DeleteEvent) {
        val entity = event.`object`
        val session = event.session
        if (entity is DataContractMessageAware) {
            saveDataContractMessage(entity, "DELETE", session)
        }
    }
}
