package com.likelion.trendithon.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.likelion.trendithon.domain.card.entity.UserCard;
import com.likelion.trendithon.global.common.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class User extends BaseTimeEntity {
  @Id
  @Column(name = "login_id", nullable = false, unique = true)
  private String loginId;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "nickname", nullable = false, unique = true)
  private String nickname;

  @Column(name = "state", nullable = false)
  private Boolean state;

  @Column(name = "refresh_token")
  private String refreshToken;

  @Column(name = "role")
  private String userRole;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserCard> userCardList = new ArrayList<>();
}
