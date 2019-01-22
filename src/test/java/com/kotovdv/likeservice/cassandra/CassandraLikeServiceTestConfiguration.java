package com.kotovdv.likeservice.cassandra;


import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Session;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

import static org.cassandraunit.utils.EmbeddedCassandraServerHelper.CASSANDRA_RNDPORT_YML_FILE;

@TestConfiguration
public class CassandraLikeServiceTestConfiguration {

    private static final String KEYSPACE_QUERY = "CREATE KEYSPACE like_service" +
        " WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";

    private static final String TABLE_QUERY = "CREATE TABLE like_service.player_likes(" +
        "player_id VARCHAR PRIMARY KEY," +
        " likes counter" +
        ")";

    @Bean(initMethod = "init")
    public CassandraLikeService cassandraLikeService(Session session) {
        return new CassandraLikeService(
            session,
            ConsistencyLevel.ONE,
            ConsistencyLevel.ONE
        );
    }

    @Bean
    public Session likeServiceSession() throws IOException, TTransportException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra(CASSANDRA_RNDPORT_YML_FILE);
        Cluster cluster = EmbeddedCassandraServerHelper.getCluster();

        try (Session session = cluster.connect()) {
            session.execute(KEYSPACE_QUERY);
            session.execute(TABLE_QUERY);
        }

        return cluster.connect("like_service");
    }
}
