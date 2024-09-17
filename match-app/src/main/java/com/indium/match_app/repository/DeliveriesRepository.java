package com.indium.match_app.repository;

import com.indium.match_app.entity.Deliveries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DeliveriesRepository extends CrudRepository<Deliveries, Long> {

    @Query("SELECT COUNT(d) FROM Deliveries d WHERE d.bowler = :playerName AND d.wicketKind IS NOT NULL")
    int getWicketCountByBowler(@Param("playerName") String playerName);


    @Query("SELECT SUM(d.runsTotal) FROM Deliveries d WHERE d.batter = :playerName")
    Integer findCumulativeScoreByPlayerName(@Param("playerName") String playerName);


    @Query("SELECT o.info.matchNumber, SUM(d.runsTotal) " +
            "FROM Deliveries d " +
            "JOIN d.over o " +
            "WHERE o.info.matchDate = :matchDate " +
            "GROUP BY o.info.matchNumber")
    List<Object[]> findScoresByMatchDate(LocalDate matchDate);


    @Query("SELECT d.batter " +
            "FROM Deliveries d " +
            "GROUP BY d.batter " +
            "ORDER BY SUM(d.runsBatter) ASC")
    Page<String> findTopBatsmen(Pageable pageable);

}
