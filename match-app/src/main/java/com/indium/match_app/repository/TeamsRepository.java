package com.indium.match_app.repository;

import com.indium.match_app.entity.Teams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamsRepository extends JpaRepository<Teams, Long> {
    Optional<Teams> findByTeamName(String teamName);
}
