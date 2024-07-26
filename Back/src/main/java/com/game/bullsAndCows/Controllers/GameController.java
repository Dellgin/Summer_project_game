package com.game.bullsAndCows.Controllers;

import com.game.bullsAndCows.Entity.Games;
import com.game.bullsAndCows.Services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GameController {
    @Autowired
    private GameService gameService;

    @PostMapping("/start")
    public ResponseEntity<Games> startGame(@RequestParam String nickname) {
        Games games = gameService.startNewGame(nickname);
        return ResponseEntity.ok(games);
    }

    @PostMapping("/guess")
    public ResponseEntity<String> makeGuess(@RequestParam Long gameId, @RequestParam String guess) {
        if (guess == null || guess.length() != 4 || !guess.matches("\\d+")) {
            return ResponseEntity.badRequest().body("Неверное предположение. Пожалуйста, укажите 4-значный номер.");
        }

        String result = gameService.makeGuess(gameId, guess);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    public Map<String, Object> getGameStatus(@RequestParam Long gameId) {
        return gameService.getGameStatus(gameId);
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<Games>> getStatistics(@RequestParam String nickname) {
        List<Games> games = gameService.getUserGames(nickname);
        return ResponseEntity.ok(games);
    }
}