package com.game.bullsAndCows.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Games")
public class Games {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_game")
    private Long idGame;

    @Column(name = "number_to_guess")
    private String numberToGuess;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "attempts")
    private Integer attempts;

    @Column(name = "result")
    private String result;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private Users idUsers;
}