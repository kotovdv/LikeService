package com.kotovdv.likeservice;

import javax.annotation.Nonnull;

/**
 * Сервис по работе с лайками игроков.
 */
public interface LikeService {

    /**
     * Лайкает игрока с указанным playerId.
     *
     * @param playerId Идентификатор игрока.
     */
    void like(@Nonnull String playerId);

    /**
     * Получает кол-во лайков у игрока с указанным playerId.
     *
     * @param playerId Идентификатор игрока.
     * @return Кол-во лайков у указанного игрока.
     */
    long getLikes(@Nonnull String playerId);
}
