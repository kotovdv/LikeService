CREATE KEYSPACE like_service WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};
CREATE TABLE like_service.player_likes(player_id VARCHAR PRIMARY KEY, likes counter);