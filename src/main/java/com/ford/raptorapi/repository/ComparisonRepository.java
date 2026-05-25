package com.ford.raptorapi.repository;

import com.ford.raptorapi.model.Comparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComparisonRepository extends JpaRepository<Comparison, Integer> {
    List<Comparison> findByUserId(Integer userId);
}
