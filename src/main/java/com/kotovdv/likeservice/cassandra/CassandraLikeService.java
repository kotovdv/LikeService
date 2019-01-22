package com.kotovdv.likeservice.cassandra;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.kotovdv.likeservice.LikeService;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Реализация LikeService на основе Apache Cassandra.
 * <p>
 * Для хранения кол-ва лайков взят counter column
 *
 * @see <a href="https://docs.datastax.com/en/cql/3.3/cql/cql_using/useCountersConcept.html"/>
 */
public class CassandraLikeService implements LikeService {

    private final Session session;
    private final ConsistencyLevel readLevel;
    private final ConsistencyLevel writeLevel;

    private PreparedStatement getLikesPreparedStatement;
    private PreparedStatement incrementLikesPreparedStatement;

    public CassandraLikeService(Session session,
                                ConsistencyLevel readLevel,
                                ConsistencyLevel writeLevel) {
        this.session = session;
        this.readLevel = readLevel;
        this.writeLevel = writeLevel;
    }

    public void init() {
        this.getLikesPreparedStatement = session
            .prepare("SELECT likes FROM player_likes WHERE player_id = ?");
        this.incrementLikesPreparedStatement = session
            .prepare("UPDATE player_likes SET likes = likes+1 WHERE player_id = ?");
    }

    @Override
    public void like(@Nonnull String playerId) {
        Statement statement = incrementLikesPreparedStatement
            .bind(playerId)
            .setConsistencyLevel(writeLevel);

        session.execute(statement);
    }

    @Override
    public long getLikes(@Nonnull String playerId) {
        Statement statement = getLikesPreparedStatement.bind(playerId).setConsistencyLevel(readLevel);

        ResultSet resultSet = session.execute(statement);

        return Optional.ofNullable(resultSet.one())
            .map(row -> row.get("likes", Long.class))
            .orElse(0L);
    }
}
