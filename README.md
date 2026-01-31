# Spring Cloud MSA

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.1-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.9-231F20?logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.0-FF6600?logo=rabbitmq&logoColor=white)](https://www.rabbitmq.com/)
[![MariaDB](https://img.shields.io/badge/MariaDB-12.0-003545?logo=mariadb&logoColor=white)](https://mariadb.org/)

> 인프런 Spring Cloud로 개발하는 마이크로서비스 애플리케이션(MSA)

<br>

본 프로젝트는 Spring Cloud 기반 MSA 환경을 구성하고, Config Server, API Gateway, 메시지 브로커(RabbitMQ, Kafka)를 중심으로 서비스 간 설정 관리 및 이벤트 기반 통신을 실습합니다.

<br>

#### ✏️ TO DO

- [ ] Microservice Architecture 패턴 학습
- [ ] Kubernetes 환경 구성

<br>

---

## Containerization

- Spring Cloud Bus의 MQ로서 **RabbitMQ**를 사용합니다.
- 분산 환경에서 데이터의 최종 일관성을 보장하기 위해 **Kafka + ZooKeeper**를 사용합니다.

<br>

### 네트워크 생성

```bash
docker network create ecommerce-network
```

### 컨테이너 실행

```bash
docker compose up -d
```

<br>

---

## Config Service

본 프로젝트는 **Spring Cloud Config Server**를 사용하여 애플리케이션 설정을 외부에서 관리하고, **서비스 재빌드 없이 설정 변경을 적용**할 수 있도록 구성하였습니다.

강의 예제에서는 `spring-cloud-starter-bootstrap`과 `bootstrap.yml`을 사용하여 Config Server 및 암호화 관련 설정을 부트스트랩 단계에서 로딩합니다. 그러나 본 프로젝트는 Spring Boot 3 최신 버전과의 호환성을 고려하여 `bootstrap.yml` 대신 `spring.config.import` 기반 설정 로딩 방식을 사용합니다.

<br>

### bootstrap.yml 미사용으로 인한 이슈

`bootstrap.yml`을 사용하지 않고 `application.yml` 기반으로 설정을 구성하면서,
Config Server 기동 초기에 필요한 설정(`encrypt.key-store.location`)이 **아직 로딩되지 않은 상태에서 사용되는 문제**가 발생했습니다.

그 결과, 컨테이너 내부에 keystore 파일이 존재함에도 불구하고 다음과 같은 오류가 발생했습니다.
```
IllegalStateException: Invalid keystore location
```
이는 Config Server가 암호화된 설정(`{cipher}...`)을 처리하기 위해 `TextEncryptor`를 생성하는 시점이 application.yml 로딩보다 더 이르기 때문입니다.
따라서 Spring 설정 우선순위 상 가장 먼저 로딩되는 컨테이너 환경 변수 주입을 통해 해당 문제를 해결했습니다.

```yaml
environment:
  ENCRYPT_KEY_STORE_LOCATION: file:///apiEncryptionKey.jks
```

<br>

---

## Kafka

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

### Order–Catalog 서비스 간 이벤트 기반 통신

`example-catalog-topic`을 통해 Order 서비스가 이벤트를 발행하고, Catalog 서비스가 이를 소비하여 주문 시 상품 재고를 동기화합니다.

- **spring-kafka**: 3.3.11