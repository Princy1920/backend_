package com.indium.match_app.repository;

import com.indium.match_app.entity.Info;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface InfoRepository extends CrudRepository<Info, Integer> {

    @Query("SELECT i.info.matchNumber, p.playerName " +
            "FROM Innings i " +
            "JOIN i.info info " +
            "JOIN Players p ON p.team.teamName = i.team " +
            "WHERE p.playerName = :playerName")
    List<Object[]> findMatchNumberAndPlayerNameByPlayerName(@Param("playerName") String playerName);



}
