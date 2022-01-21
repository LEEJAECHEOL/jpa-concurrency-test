package com.study.concurrency.optimistic.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String content;

  // Optimistic Lock 기능
  @Version
  private Integer version;

  public void setContent(String content) {
    this.content = content;
  }

}
