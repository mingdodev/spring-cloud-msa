# Spring Cloud MSA

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.1-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.9-231F20?logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.0-FF6600?logo=rabbitmq&logoColor=white)](https://www.rabbitmq.com/)
[![MariaDB](https://img.shields.io/badge/MariaDB-12.0-003545?logo=mariadb&logoColor=white)](https://mariadb.org/)

인프런 Spring Cloud로 개발하는 마이크로서비스 애플리케이션(MSA)

<br>

> 강의에서는 `spring-cloud-starter-bootstrap`을 사용하지만, 본 프로젝트는 Spring Boot 3 최신 버전과의 호환성을 고려하여 `spring.config.import` 기반 설정 로딩을 사용합니다.

---

## Containerization

본 프로젝트는 로컬 환경에 바이너리를 직접 설치하는 대신 **Docker 컨테이너 환경**에서 필요한 인프라 컴포넌트를 실행합니다.

- Spring Cloud Bus의 MQ로서 **RabbitMQ**를 사용합니다.
- 분산 환경에서 데이터의 최종 일관성을 보장하기 위해 **Kafka + ZooKeeper**를 사용합니다.

<br>

### 컨테이너 실행

```bash
docker compose up -d
```

<br>

### Kafka

Kafka CLI는 Kafka 컨테이너 내부에 포함된 커맨드를 사용합니다.

```bash
# 컨테이너 접속
docker exec -it broker bash

# 토픽 생성
kafka-topics --create \
  --topic quickstart-events \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1

# 토픽 목록 확인
kafka-topics --bootstrap-server localhost:9092 --list

# 토픽 정보 확인
kafka-topics --describe \
  --topic quickstart-events \
  --bootstrap-server localhost:9092

# 메시지 생산
kafka-console-producer --bootstrap-server localhost:9092 --topic quickstart-events

# 메시지 소비
kafka-console-consumer --bootstrap-server localhost:9092 --topic quickstart-events --from-beginning
```

<br>

**Kafka Connect**를 활용해, 코드 변경 없이 설정만으로 외부 시스템과 카프카 간 데이터 이동(ETL)을 자동화할 수 있습니다.

- **Apache Kafka**: 3.9.x
- **Confluent Platform (Kafka Connect)**: 7.9.x
- **kafka-connect-jdbc**: 10.8.5
- **mariadb-java-client**: 3.5.7

본 구성은 `confluentinc/cp-kafka-connect:7.9` 이미지를 기준으로 하며,
위와 같은 버전 조합을 사용합니다.

<br>

- **Source Connect 생성**: `POST localhost:8083/connectors`
```json
{
	"name" : "my-source-connect",
	"config" : {
        "connector.class" : "io.confluent.connect.jdbc.JdbcSourceConnector",
        "connection.url":"jdbc:mariadb://mariadb:3306/test",
        "connection.user":"root",
        "connection.password":"test1357",
        "mode": "incrementing",
        "incrementing.column.name" : "id",
        "table.whitelist":"test.users",
        "topic.prefix" : "my_topic_",
        "tasks.max" : "1"
	}
}
```
> table.whitelist 설정 시 테이블명만 지정할 경우, 동일한 이름의 테이블이 다른 스키마에 존재하면 JDBC Source Connector의 메타데이터 조회 과정에서 대상 테이블을 식별하지 못해 오류가 발생할 수 있다.
> 이를 방지하기 위해 스키마를 포함한 형태(test.users)로 명시하였다.

<br>

- **Sink Connect 생성**: `POST localhost:8083/connectors`
```json
{
    "name":"my-sink-connect",
    "config":{
        "connector.class":"io.confluent.connect.jdbc.JdbcSinkConnector",
        "connection.url":"jdbc:mariadb://mariadb:3306/mydb",
        "connection.user":"root",
        "connection.password":"test1357",
        "auto.create":"true",
        "auto.evolve":"true",
        "delete.enabled":"false",
        "tasks.max":"1",
        "topics":"my_topic_users"
        }
}
```