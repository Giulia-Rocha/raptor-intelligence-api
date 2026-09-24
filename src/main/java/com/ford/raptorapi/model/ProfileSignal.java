package com.ford.raptorapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "profile_signals")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProfileSignal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "profile_type", nullable = false, length = 30)
    private String profileType;

    @Column(nullable = false, length = 180)
    private String signal;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;
}