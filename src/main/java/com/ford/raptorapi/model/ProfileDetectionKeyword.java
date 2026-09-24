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
@Table(name = "profile_detection_keywords")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProfileDetectionKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "profile_type", nullable = false, length = 30)
    private String profileType;

    @Column(nullable = false, length = 60)
    private String keyword;

    @Column(nullable = false)
    private Integer weight = 1;
}