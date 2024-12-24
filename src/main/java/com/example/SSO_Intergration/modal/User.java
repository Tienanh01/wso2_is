package com.example.SSO_Intergration.modal;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "user_")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long userId;

    @Column(name = "createDate")
    private Date createDate;

    @Column(name = "modifiedDate")
    private Date modifiedDate;

    @Column(name = "password")
    private String password;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "emailAddress", unique = true)
    private String emailAddress;

    @Column(name = "displayName")
    private String displayName;

    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "lastLoginDate")
    private Date lastLoginDate;

    @Column(name = "lastLoginIP")
    private String lastLoginIP;

    @Column(name = "status")
    private Integer status;

    @Column(name = "sso")
    private Boolean sso;

    // Getters and Setters remain the same


    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", createDate=" + createDate +
                ", modifiedDate=" + modifiedDate +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", displayName='" + displayName + '\'' +
                ", organizationId=" + organizationId +
                ", lastLoginDate=" + lastLoginDate +
                ", lastLoginIP='" + lastLoginIP + '\'' +
                ", status=" + status +
                ", sso=" + sso +
                '}';
    }
}
