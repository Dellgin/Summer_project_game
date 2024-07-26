package com.game.bullsAndCows.Services;

import com.game.bullsAndCows.Entity.Games;
import com.game.bullsAndCows.Entity.Users;
import com.game.bullsAndCows.GameConfig;
import com.game.bullsAndCows.Repositories.GameRepository;
import com.game.bullsAndCows.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameConfig gameConfig;

    public Games startNewGame(String nickname) {
        Users user = userRepository.findByNickname(nickname)
                .orElseGet(() -> {
                    Users newUser = new Users();
                    newUser.setNickname(nickname);
                    return userRepository.save(newUser);
                });

        Games game = new Games();
        game.setIdUsers(user);
        game.setNumberToGuess(generateNumber());
        game.setStartTime(LocalDateTime.now());
        game.setAttempts(0);
        game.setEndTime(null);
        game.setResult(null);
        gameRepository.save(game);
        return game;
    }

    private String generateNumber() {
        List<Integer> digits = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            digits.add(i);
        }
        Collections.shuffle(digits);
        return digits.subList(0, 4).stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public String makeGuess(Long gameId, String guess) {
        if (!areDigitsUnique(guess)) {
            return "Цифры не должны повторяться.";
        }

        Games game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        if (!gameConfig.isUnlimited() && game.getAttempts() >= gameConfig.getMaxAttempts()) {
            game.setEndTime(LocalDateTime.now());
            game.setResult("Проигрыш");
            gameRepository.save(game);
            return "Игра окончена. Достигнуто максимальное количество попыток.";
        }

        if (gameConfig.getTimeLimit() > 0 && LocalDateTime.now().isAfter(game.getStartTime().plusSeconds(gameConfig.getTimeLimit()))) {
            game.setEndTime(LocalDateTime.now());
            game.setResult("Проигрыш");
            gameRepository.save(game);
            return "Игра окончена. Истек лимит времени.";
        }

        game.setAttempts(game.getAttempts() + 1);
        String result = calculateBullsAndCows(game.getNumberToGuess(), guess);
        if (result.equals("4Б0К")) {
            game.setEndTime(LocalDateTime.now());
            game.setResult("Победа");
        }
        gameRepository.save(game);
        return result;
    }

    private boolean areDigitsUnique(String guess) {
        Set<Character> digits = new HashSet<>();
        for (char c : guess.toCharArray()) {
            if (!digits.add(c)) {
                return false;
            }
        }
        return true;
    }

    private String calculateBullsAndCows(String numberToGuess, String guess) {
        int bulls = 0;
        int cows = 0;

        for (int i = 0; i < 4; i++) {
            if (numberToGuess.charAt(i) == guess.charAt(i)) {
                bulls++;
            } else if (numberToGuess.contains(String.valueOf(guess.charAt(i)))) {
                cows++;
            }
        }
        return String.format("%dБ%dК", bulls, cows);
    }

    public List<Games> getUserGames(String nickname) {
        Users user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return gameRepository.findByIdUsersOrderByStartTimeDesc(user);
    }

    public Map<String, Object> getGameStatus(Long gameId) {
        Games game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        Map<String, Object> status = new HashMap<>();

        if (game.getEndTime() != null) {
            status.put("message", "Игра окончена.");
            status.put("result", game.getResult());
            return status;
        }

        if (!gameConfig.isUnlimited() && game.getAttempts() >= gameConfig.getMaxAttempts()) {
            game.setEndTime(LocalDateTime.now());
            game.setResult("lost");
            gameRepository.save(game);
            status.put("message", "Игра окончена. Достигнуто максимальное количество попыток.");
            return status;
        }

        if (gameConfig.getTimeLimit() > 0 && LocalDateTime.now().isAfter(game.getStartTime().plusSeconds(gameConfig.getTimeLimit()))) {
            game.setEndTime(LocalDateTime.now());
            game.setResult("lost");
            gameRepository.save(game);
            status.put("message", "Игра окончена. Истек лимит времени.");
            return status;
        }

        status.put("attempts", game.getAttempts());

        if (gameConfig.getTimeLimit() > 0) {
            long timeRemaining = LocalDateTime.now().until(game.getStartTime().plusSeconds(gameConfig.getTimeLimit()), SECONDS);
            status.put("timeRemaining", timeRemaining > 0 ? timeRemaining : 0);
        }

        return status;
    }
}
