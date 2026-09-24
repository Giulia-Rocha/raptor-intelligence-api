package com.ford.raptorapi.repository;

import com.ford.raptorapi.model.ProfileDetectionKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileDetectionKeywordRepository extends JpaRepository<ProfileDetectionKeyword, Long> {

    List<ProfileDetectionKeyword> findAllByOrderByIdAsc();
}