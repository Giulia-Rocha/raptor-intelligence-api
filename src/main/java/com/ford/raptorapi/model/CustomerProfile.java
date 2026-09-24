package com.ford.raptorapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "customer_profiles")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CustomerProfile {

    @Id
    @Column(length = 30)
    private String type;

    @Column(nullable = false, length = 60)
    private String label;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
}