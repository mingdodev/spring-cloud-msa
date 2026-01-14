# Spring Cloud MSA

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-6DB33F)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.1-6DB33F)](https://spring.io/projects/spring-cloud)
[![Java](https://img.shields.io/badge/Java-21-007396)](https://adoptium.net/)

인프런 Spring Cloud로 개발하는 마이크로서비스 애플리케이션(MSA)

<br>

> 강의에서는 `spring-cloud-starter-bootstrap`을 사용하지만, 본 프로젝트는 Spring Boot 3 최신 버전과의 호환성을 고려하여 `spring.config.import` 기반 설정 로딩을 사용합니다.

<br>

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

### Kafka 테스트

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