package main

import (	
	"fmt"
)

func playGame(playerChannel chan Message, id string, max int) {
	var min = 0
	var guess = playTurn(playerChannel, min, max, id)
	for msg := range playerChannel {
        switch m := msg.(type) {
       		case ServerMessage:
				switch m.hint {
				case Lower:
					max = guess
					guess = playTurn(playerChannel, min, max, id)
				case Higher:
					min = guess
					guess = playTurn(playerChannel, min, max, id)
				case Win:
					fmt.Println(id, "WIN")
					close(playerChannel)
				case Lose:
					fmt.Println(id, "LOSE")
					close(playerChannel)
				}
        }
    }
}

func playTurn(playerChannel chan Message, min, max int, id string) int {
	guess := calculateGuess(min, max)
	playerChannel <- ClientMessage{guess: guess, senderId: id}
	return guess
}

func calculateGuess(lowerBound, upperBound int) int {
	guess := (lowerBound + upperBound)/2
	return guess
}

func spawnPlayer(id string, max int) chan Message {
	playerChannel := make(chan Message)
	go playGame(playerChannel, id, max)
	return playerChannel
}