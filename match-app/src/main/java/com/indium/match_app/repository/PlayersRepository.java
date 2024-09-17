package com.indium.match_app.repository;

import com.indium.match_app.entity.Players;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlayersRepository extends CrudRepository<Players, Long> {
    // Adjusted to Long if ID is Long
    // Method to find a player by their name
    Optional<Players> findByPlayerName(String playerName);
}
