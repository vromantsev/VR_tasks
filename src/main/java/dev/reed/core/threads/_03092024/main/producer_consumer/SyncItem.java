package dev.reed.core.threads._03092024.main.producer_consumer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SyncItem {

    private String description;
    private int quantity;
    private LocalDateTime createdAt;
}
