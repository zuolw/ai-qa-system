package com.ai.qa.user.domain.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table
public class User {

    private  String userName;
     private  String password;
     private Long id;
     private LocalDateTime createTime;
    private LocalDateTime updateTime;


}
