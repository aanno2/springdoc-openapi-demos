package org.springdoc.demo.app2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

import javax.management.MalformedObjectNameException;
import java.net.MalformedURLException;

/**
 * https://stackoverflow.com/questions/29412072/how-to-access-spring-boot-jmx-remotely
 */
// @Configuration
public class ConfigureRMI {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigureRMI.class);

    @Value("${jmx.rmi.host:localhost}")
    private String rmiHost;

    @Value("${jmx.rmi.port:1099}")
    private Integer rmiPort;

    private String getObjectName() {
        return "connector:name=rmi";
    }

    private String getServiceUrl() {
        String url = String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", rmiHost, rmiPort, rmiHost, rmiPort);
        // String url = "service:jmx:jmxmp://" + rmiHost + ":" + rmiPort;
        return url;
    }

    @Bean
    public RmiRegistryFactoryBean rmiRegistry() {
        final RmiRegistryFactoryBean rmiRegistryFactoryBean = new RmiRegistryFactoryBean();
        rmiRegistryFactoryBean.setPort(rmiPort);
        LOG.info("rmi: " + rmiHost + ":" + rmiPort);
        // Must be false if fork=true and true if fork=false
        // fork=true leads to java.io.InvalidClassException: filter status: REJECTED
        rmiRegistryFactoryBean.setAlwaysCreate(true);
        return rmiRegistryFactoryBean;
    }

    @Qualifier("serverMBeanConnection")
    @Bean
    @DependsOn("rmiRegistry")
    public ConnectorServerFactoryBean connectorServerFactoryBean()
            throws MalformedObjectNameException
    {
        final ConnectorServerFactoryBean connectorServerFactoryBean = new ConnectorServerFactoryBean();
        connectorServerFactoryBean.setObjectName(getObjectName());
        String url = getServiceUrl();
        LOG.info("service url server: " + url);
        connectorServerFactoryBean.setServiceUrl(url);
        return connectorServerFactoryBean;
    }

    @Qualifier("clientMBeanConnection")
    @Bean
    @DependsOn("connectorServerFactoryBean")
    public MBeanServerConnectionFactoryBean mBeanServerConnectionFactoryBean()
            throws MalformedURLException
    {
        MBeanServerConnectionFactoryBean result = new MBeanServerConnectionFactoryBean();
        String url = getServiceUrl();
        LOG.info("service url client: " + url);
        result.setServiceUrl(url);
        return result;
    }

    /*
    @Bean
    public MBeanProxyFactoryBean mBeanProxyFactoryBean(
            @Qualifier("clientMBeanConnection") MBeanServerConnection connection)
            throws MalformedURLException, MalformedObjectNameException
    {
        MBeanProxyFactoryBean result = new MBeanProxyFactoryBean();
        result.setObjectName(getObjectName());
        result.setServiceUrl(getServiceUrl());
        result.setServer(connection);
        // result.setManagementInterface();
        // result.setProxyInterface();
        return result;
    }
     */
}