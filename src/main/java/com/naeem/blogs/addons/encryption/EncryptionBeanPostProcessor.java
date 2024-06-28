package com.naeem.blogs.addons.encryption;

import javax.persistence.EntityManagerFactory;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


/**
 *
 * 
 *         The BeanPostProcessors operate on bean (or object) instances, which
 *         means that the Spring IoC container instantiates a bean instance and
 *         then BeanPostProcessor interfaces do their work.
 *
 */
@Component
public class EncryptionBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private EncryptionListener encryptionListener;
 
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
 
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof EntityManagerFactory) {
            HibernateEntityManagerFactory hibernateEntityManagerFactory = (HibernateEntityManagerFactory) bean;
            SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) hibernateEntityManagerFactory.getSessionFactory();
            EventListenerRegistry registry = sessionFactoryImpl.getServiceRegistry().getService(EventListenerRegistry.class);
            registry.appendListeners(EventType.POST_LOAD, encryptionListener);
            registry.appendListeners(EventType.PRE_INSERT, encryptionListener);
            registry.appendListeners(EventType.PRE_UPDATE, encryptionListener);
    }
        return bean;
    }
}
