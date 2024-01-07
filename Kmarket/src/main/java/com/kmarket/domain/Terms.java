package com.kmarket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Entity
@Table(name = "km_member_terms")
public class Terms {
    @Id
    private int id;
    private String terms;
    private String privacy;
    private String location;
    private String finance;
    private String tax;
}
