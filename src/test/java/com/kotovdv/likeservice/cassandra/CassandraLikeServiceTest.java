package com.kotovdv.likeservice.cassandra;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.kotovdv.likeservice.LikeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@RunWith(SpringJUnit4ClassRunner.class)
@Import(CassandraLikeServiceTestConfiguration.class)
public class CassandraLikeServiceTest {

    @Autowired
    private Session session;

    @Autowired
    private LikeService likeService;

    @Before
    public void init() {
        session.execute("truncate table player_likes");
    }

    /**
     * Сценарий #1:
     * <p>
     * Хотим получить лайки неизвестного игрока (ранее информации о его лайках не существовало в БД).
     * <p>
     * Ожидаемое поведение:
     * В ответ должны получить значение 0.
     */
    @Test
    public void getLikesOfUnknownPlayer() {
        long likes = likeService.getLikes("john doe");

        assertThat(likes).isZero();
    }

    /**
     * Сценарий #2:
     * <p>
     * Хотим получить лайки у известного игрока.
     * <p>
     * Ожидаемое поведение:
     * В ответ должны получить кол-во его лайков.
     */
    @Test
    public void getLikesOfExistingPlayer() {
        int likes = 78;
        String playerId = "p1";

        initPlayerState(playerId, likes);

        long actualLikes = likeService.getLikes(playerId);

        assertThat(actualLikes).isEqualTo(likes);
    }

    /**
     * Сценарий #3:
     * <p>
     * Хотим проставить лайк неизвестному игроку (ранее информации о его лайках не существовало в БД).
     * <p>
     * Ожидаемое поведение:
     * В БД должна появиться запись о новом игроке с кол-во лайков = 1.
     */
    @Test
    public void likeUnknownPlayer() {
        String playerId = "john doe";

        likeService.like(playerId);

        assertPlayerState(playerId, 1);
    }

    /**
     * Сценарий #4:
     * <p>
     * Хотим проставить лайк известному игроку. На текущий момент у него 9 лайков.
     * <p>
     * Ожидаемое поведение:
     * Кол-во лайков должно увеличиться на 1 (с 9 до 10)
     */
    @Test
    public void likeExistingPlayer() {
        int initialLikes = 9;
        String playerId = "p1";

        initPlayerState(playerId, initialLikes);

        likeService.like(playerId);

        assertPlayerState(playerId, initialLikes + 1);
    }

    /**
     * Сценарий #5:
     * <p>
     * Хотим проставить лайк одному из существующих игроков.
     * При этом в БД существуют другие игроки, с отличными от целевого игрока player_id
     * <p>
     * Ожидаемое поведение:
     * Должно измениться только кол-во лайков целевого игрока (2->3).
     */
    @Test
    public void likeOnePlayerWhenMultipleExist() {
        initPlayerState("p1", 1);
        initPlayerState("p2", 2);
        initPlayerState("p3", 3);

        likeService.like("p2");

        assertPlayerState("p1", 1);
        assertPlayerState("p2", 3);
        assertPlayerState("p3", 3);
    }

    /**
     * Сценарий #6:
     * <p>
     * Хотим получить лайки одного из существующих игроков.
     * <p>
     * В ответ должны получить значение лайков именно целевого игрока.
     */
    @Test
    public void getPlayerLikesWhenMultipleExist() {
        initPlayerState("p1", 1);
        initPlayerState("p2", 2);
        initPlayerState("p3", 3);

        long likes = likeService.getLikes("p2");

        assertThat(likes).isEqualTo(2);
    }

    private void initPlayerState(String playerId, long expectedLikes) {
        session.execute("UPDATE player_likes SET likes = likes + ? WHERE player_id = ?",
            expectedLikes,
            playerId
        );
    }

    private void assertPlayerState(String playerId, long expectedLikesCount) {
        ResultSet resultSet = session.execute(
            "SELECT * FROM player_likes WHERE player_id = ?",
            playerId
        );

        assertSoftly(assertions -> {
            Row row = resultSet.one();

            assertions.assertThat(row).isNotNull();
            assertions.assertThat(row.get("player_id", String.class)).isEqualTo(playerId);
            assertions.assertThat(row.get("likes", Long.class)).isEqualTo(expectedLikesCount);
        });
    }
}
