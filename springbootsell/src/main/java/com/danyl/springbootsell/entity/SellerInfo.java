package com.danyl.springbootsell.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class SellerInfo {

    @Id
    private String id;

    private String openid;

    private String username;

    private String password;
}
