# LikeService

## Задание:

Написать и покрыть тестами класс, который считает Like'и, которые ставят игроки друг другу, и сохраняет их количество в базу данных (интерфейс ниже). Базу данных выбрать самостоятельно с учетом того, что система должна работать под большой нагрузкой. Реализовать на Java.

```
public interface LikeService {

    void like(String playerId);

    long getLikes(String playerId);
}
```

## Выбор БД

В качестве БД была выбрана Apache Cassandra.

* Официальный сайт http://cassandra.apache.org
* Википедия https://en.wikipedia.org/wiki/Apache_Cassandra

Мотивация по выбору именной этой БД:
* В соответствии с публичными докладами и benchmark'ами и успешно используется многими крупными конторами для построения высоконагруженных и отказоустойчивых решений. 
  * <a href="https://youtu.be/k2efjgRxMp8">Доклад Олега Анастасьева (Одноклассники)</a>
  * <a href="https://events.yandex.ru/lib/talks/2370/">Доклад Владимира Волкова (Yandex)</a>
  * <a href="https://www.youtube.com/watch?v=SAyClLjN6Sk"/>Доклад Андрея Смирнова (Virtustream)</a>
  
* **Eventual consistency** является приемлимой моделью для поставленной задачи. Это означает, что при выборе решения можно сконцентрироваться на таких аспектах как **производительность**,  **доступность** и **отказоустойчивость**. Cassandra заточена под каждый из них.   
   
* Наличие приемлимой официальной  документации и живого сообщества.

* Удобное Java API. 


## Решение

<a href="https://github.com/kotovdv/LikeService/blob/master/src/main/java/com/kotovdv/likeservice/LikeService.java">Интерфейс LikeService</a>

<a href="https://github.com/kotovdv/LikeService/blob/master/src/main/java/com/kotovdv/likeservice/cassandra/CassandraLikeService.java">Реализация CassandraLikeService</a>

Перед запуском убедиться, что на машине установлен Docker, соответствующий следующим требованиям:
(https://www.testcontainers.org/supported_docker_environment/)

В корне проекта исполнить
``` 
./gradlew clean test --info
```


