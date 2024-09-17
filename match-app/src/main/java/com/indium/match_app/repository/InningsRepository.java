package com.indium.match_app.repository;

import com.indium.match_app.entity.Info;
import com.indium.match_app.entity.Innings;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InningsRepository extends CrudRepository<Innings, Integer> {

//    @Query("SELECT i.info.matchNumber, p.playerName FROM Innings i JOIN i.info info JOIN Players p ON p.team.teamName = i.team WHERE p.playerName = :playerName")
//    List<Object[]> findMatchNumberAndPlayerNameByPlayerName(@Param("playerName") String playerName);


    // Query to find match number and player name
    @Query("SELECT i.info.matchNumber, p.playerName FROM Innings i JOIN Players p ON p.team.teamName = i.team WHERE p.playerName = :playerName")
    List<Object[]> findMatchNumberAndPlayerNameByPlayerName(@Param("playerName") String playerName);
}
