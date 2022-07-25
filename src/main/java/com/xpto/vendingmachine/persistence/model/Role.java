package com.xpto.vendingmachine.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ROLE")
@Builder
public class Role implements GrantedAuthority {

  public static final String ADMIN = "ADMIN";
  public static final String SELLER = "SELLER";
  public static final String BUYER = "BUYER";

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name= "ID", columnDefinition = "serial")
  @Id
  private Long id;

  private String authority;

}
