package com.example.contracts

import org.hibernate.boot.Metadata
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.integrator.spi.Integrator
import org.hibernate.service.spi.SessionFactoryServiceRegistry

class EventListenerRegistryIntegrator : Integrator {
    override fun integrate(
        metadata: Metadata?,
        sessionFactory: SessionFactoryImplementor,
        serviceRegistry: SessionFactoryServiceRegistry
    ) {
        val elr = serviceRegistry.getService(EventListenerRegistry::class.java)
        val el = EntityChangeEventListener()
        elr.appendListeners(EventType.PERSIST, el)
        elr.appendListeners(EventType.FLUSH_ENTITY, el)
        elr.appendListeners(EventType.DELETE, el)
    }

    override fun disintegrate(
        sessionFactory: SessionFactoryImplementor,
        serviceRegistry: SessionFactoryServiceRegistry
    ) {
    }
}
