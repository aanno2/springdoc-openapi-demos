package org.springdoc.demo.app1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

/**
 * https://stackoverflow.com/questions/29412072/how-to-access-spring-boot-jmx-remotely
 */
@Configuration
public class ConfigureRMI {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigureRMI.class);

    @Value("${jmx.rmi.host:localhost}")
    private String rmiHost;

    @Value("${jmx.rmi.port:1099}")
    private Integer rmiPort;

    @Bean
    public RmiRegistryFactoryBean rmiRegistry() {
        final RmiRegistryFactoryBean rmiRegistryFactoryBean = new RmiRegistryFactoryBean();
        rmiRegistryFactoryBean.setPort(rmiPort);
        LOG.info("rmi: " + rmiHost + ":" + rmiPort);
        rmiRegistryFactoryBean.setAlwaysCreate(true);
        return rmiRegistryFactoryBean;
    }

    @Bean
    @DependsOn("rmiRegistry")
    public ConnectorServerFactoryBean connectorServerFactoryBean() throws Exception {
        final ConnectorServerFactoryBean connectorServerFactoryBean = new ConnectorServerFactoryBean();
        connectorServerFactoryBean.setObjectName("connector:name=rmi");
        String url = String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", rmiHost, rmiPort, rmiHost, rmiPort);
        LOG.info("service url: " + url);
        connectorServerFactoryBean.setServiceUrl(url);
        return connectorServerFactoryBean;
    }
}
