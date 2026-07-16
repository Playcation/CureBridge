package com.example.memberservice.entity;

import com.example.commonmodule.base_entity.BaseEntityDeletedAt;
import com.example.commonmodule.dto.UserResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "`member`")
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntityDeletedAt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String email;

  private String password;

  private String name;

  @Enumerated(value = EnumType.STRING)
  private Role role;

  private String phoneNumber;

  private Date birth;

  private Long organizationId;

  public User(String email, String password, String name, Role role, String phoneNumber,
      Date birth) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.role = role;
    this.phoneNumber = phoneNumber;
    this.birth = birth;
    this.organizationId = null;
  }

  public UserResponseDto toDto() {
    return new UserResponseDto(
        this.id,
        this.name,
        this.email,
        this.birth,
        this.phoneNumber,
        this.getCreatedAt(),
        this.getUpdatedAt(),
        this.getDeletedAt()
    );
  }

  /**
   * 비밀번호 업데이트
   * @param encodedPassword {@link org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder} 사용한 새 비밀번호
   */
  public void updatePassword(String encodedPassword) {
    this.password = encodedPassword;
  }

  public void updateUser(String name, String phoneNumber, Date birth) {
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.birth = birth;
  }

  public void registrationOrg(Long organizationId) {
    this.organizationId = organizationId;
  }
}
