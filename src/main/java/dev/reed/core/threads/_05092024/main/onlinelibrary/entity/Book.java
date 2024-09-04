package dev.reed.core.threads._05092024.main.onlinelibrary.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Book {

    private Long id;
    private String title;
    private String author;
    private boolean available;
    private String rentedBy;
    private LocalDateTime rentedAt;
}
