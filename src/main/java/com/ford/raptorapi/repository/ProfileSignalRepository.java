package com.ford.raptorapi.repository;

import com.ford.raptorapi.model.ProfileSignal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileSignalRepository extends JpaRepository<ProfileSignal, Long> {

    List<ProfileSignal> findByProfileTypeOrderByDisplayOrderAsc(String profileType);
}