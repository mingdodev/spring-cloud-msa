package com.example.orderservice.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/* Kafka Connect가 소비할 수 있는 포맷 */
@Data
@AllArgsConstructor
public class KafkaOrderRecordDto implements Serializable {
    private Schema schema;
    private Payload payload;
}