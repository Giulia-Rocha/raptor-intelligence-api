package com.ford.raptorapi.repository;

import com.ford.raptorapi.model.SpecCategoryMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecCategoryMapRepository extends JpaRepository<SpecCategoryMap, Integer> {
    List<SpecCategoryMap> findByCategoryKeyIn(List<String> categoryKeys);
    List<SpecCategoryMap> findByCategoryKeyOrderByDisplayOrder(String categoryKey);
}
