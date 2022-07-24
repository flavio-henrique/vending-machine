package com.xpto.vendingmachine.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ROLE")
public class Role implements GrantedAuthority {

  public static final String ADMIN = "ADMIN";
  public static final String SELLER = "SELLER";
  public static final String BUYER = "BUYER";

  @Id
  private String authority;

}
