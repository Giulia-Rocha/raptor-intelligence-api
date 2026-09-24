package com.ford.raptorapi.repository;

import com.ford.raptorapi.model.SalesArgument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesArgumentRepository extends JpaRepository<SalesArgument, Long> {

    List<SalesArgument> findByProfileTypeOrderByDisplayOrderAsc(String profileType);
}