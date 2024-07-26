import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Game.css';

const Game = () => {
    const [nickname, setNickname] = useState('');
    const [game, setGame] = useState(null);
    const [guess, setGuess] = useState('');
    const [message, setMessage] = useState('');
    const [statistics, setStatistics] = useState([]);
    const [isGameOver, setIsGameOver] = useState(false);
    const [attempts, setAttempts] = useState(0);
    const [timeRemaining, setTimeRemaining] = useState(null);

    const startGame = async () => {
        try {
            const response = await axios.post('/api/start', null, {
                params: { nickname }
            });
            setGame(response.data);
            setGuess('');
            setMessage('');
            setIsGameOver(false);
            setAttempts(0);
            setTimeRemaining(null);
            if (response.data.timeLimit > 0) {
                setTimeRemaining(response.data.timeLimit);
            }
        } catch (error) {
            setMessage('Ошибка при запуске игры: ' + error.response.data);
        }
    };

    const makeGuess = async () => {
        if (!areDigitsUnique(guess)) {
            setMessage('Цифры не должны повторяться');
            return;
        }

        try {
            const response = await axios.post('/api/guess', null, {
                params: { gameId: game.idGame, guess }
            });
            setMessage(response.data);
            if (response.data === '4Б0К' || response.data.startsWith('Игра окончена')) {
                setIsGameOver(true);
                getStatistics();
            }
        } catch (error) {
            setMessage('Ошибочное предположение: ' + error.response.data);
        }
        getGameStatus();
    };

    useEffect(() => {
        if (game) {
            getGameStatus();
            const interval = setInterval(getGameStatus, 1000);
            return () => clearInterval(interval);
        }
    }, [game]);

    const areDigitsUnique = (guess) => {
        const digits = new Set();
        for (let char of guess) {
            if (digits.has(char)) {
                return false;
            }
            digits.add(char);
        }
        return true;
    };

    const getGameStatus = async () => {
        try {
            const response = await axios.get('/api/status', {
                params: { gameId: game.idGame }
            });
            if (response.data.message) {
                setMessage(response.data.message);
                setIsGameOver(true);
                getStatistics();
            } else {
                setAttempts(response.data.attempts);
                if (response.data.timeRemaining !== undefined) {
                    setTimeRemaining(response.data.timeRemaining);
                }
            }
        } catch (error) {
            setMessage('Ошибка при получении статуса игры: ' + error.response.data);
        }
    };

    const getStatistics = async () => {
        try {
            const response = await axios.get('/api/statistics', {
                params: { nickname }
            });
            setStatistics(response.data);
        } catch (error) {
            setMessage('Ошибка при получении статистики: ' + error.response.data);
        }
    };

    useEffect(() => {
        if (isGameOver) {
            getStatistics();
        }
        if (timeRemaining !== null) {
            const timer = setInterval(() => {
                setTimeRemaining(prevTime => {
                    if (prevTime <= 1) {
                        setIsGameOver(true);
                        clearInterval(timer);
                        return 0;
                    }
                    return prevTime - 1;
                });
            }, 1000);
            return () => clearInterval(timer);
        }
    }, [isGameOver, timeRemaining]);

    return (
        <div className="container">
            <h1>Быки-Коровы</h1>
            {!game ? (
                <>
                    <input type="text" value={nickname} onChange={e => setNickname(e.target.value)} placeholder="Введите имя пользователя" />
                    <button onClick={startGame}>Начать игру</button>
                </>
            ) : (
                <>
                    {!isGameOver && <p>Какое число было загадано</p>}
                    <input type="text" value={guess} onChange={e => setGuess(e.target.value)} placeholder="Введите свое предположение" />
                    {!isGameOver && <button onClick={makeGuess}>Submit Guess</button>}
                    <p>{message}</p>
                    {!isGameOver && <p>Попытки: {attempts}</p>}
                    {timeRemaining !== null && !isGameOver && <p>Осталось времени: {timeRemaining} секунд</p>}
                    {isGameOver && <button onClick={startGame}>Начать новую игру</button>}
                </>
            )}
            <button onClick={getStatistics}>Статистика игр</button>
            <ul>
                {statistics.map((stat, index) => (
                    <li key={index}>
                        Игра {statistics.length - index}: {stat.attempts} попыток, {new Date(stat.startTime).toLocaleString()} - {stat.endTime ? new Date(stat.endTime).toLocaleString() : 'В процессе'} - {stat.result}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default Game;
