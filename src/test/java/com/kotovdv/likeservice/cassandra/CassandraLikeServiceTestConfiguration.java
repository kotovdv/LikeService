package com.kotovdv.likeservice.cassandra;


import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Session;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.CassandraContainer;

@TestConfiguration
public class CassandraLikeServiceTestConfiguration {

    @Bean(initMethod = "init")
    public CassandraLikeService cassandraLikeService(Session session) {
        return new CassandraLikeService(
            session,
            ConsistencyLevel.ONE,
            ConsistencyLevel.ONE
        );
    }

    @Bean
    public Session likeServiceSession(CassandraContainer container) {
        return container.getCluster().connect("like_service");
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public CassandraContainer cassandraContainer() {
        return new CassandraContainer<>("cassandra:3.0.15")
            .withInitScript("schema.cql");
    }
}
