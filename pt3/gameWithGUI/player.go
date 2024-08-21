package main

import (	
	"fmt"
)

type PlayerConfiguration struct {
	id string
	playerLogChannel chan string
	playerChannel chan Message
}

func gameHandler(playerConfiguration PlayerConfiguration, max int) {
	waitForStart(playerConfiguration, 0, max)
}

func waitForStart(playerConfiguration PlayerConfiguration, min, max int) {
	for eventMessage := range playerConfiguration.playerChannel {
		switch eventMessage {
		case Start:
			playerConfiguration.playerLogChannel <- "Game started"
			guess, turn := playTurn(playerConfiguration, 0, min, max)
			playGame(playerConfiguration, min, max, guess, turn)
		}
	}
}

func playGame(playerConfiguration PlayerConfiguration, min, max, guess, turn int) {
	for gameMessage := range playerConfiguration.playerChannel {
		switch m := gameMessage.(type) {
		case ServerMessage:
			switch m.hint {
			case Lower:
				max = guess
				guess, turn = playTurn(playerConfiguration, turn, min, max)
			case Higher:
				min = guess
				guess, turn = playTurn(playerConfiguration, turn, min, max)
			case Win:
				playerConfiguration.playerLogChannel <- fmt.Sprintf("%s: WIN", playerConfiguration.id)
				playerConfiguration.playerChannel <- End
				return
			case Lose:
				playerConfiguration.playerLogChannel <- fmt.Sprintf("%s: LOSE", playerConfiguration.id)
				playerConfiguration.playerChannel <- End
				return
			} 
		}
	}	
}

func playTurn(playerConfiguration PlayerConfiguration, turn, min, max int) (int, int) {
	playerConfiguration.playerLogChannel <- fmt.Sprintf("starting turn %d", turn)
	guess := calculateGuess(min, max)
	playerConfiguration.playerLogChannel <- fmt.Sprintf("%s guess: %d", playerConfiguration.id, guess)
	playerConfiguration.playerChannel <- ClientMessage{guess: guess, senderId: playerConfiguration.id}
	return guess, turn + 1
}
	
func calculateGuess(lowerBound, upperBound int) int {
	guess := (lowerBound + upperBound)/2
	return guess
}

func spawnPlayer(id string, max int) chan Message {
	playerConfiguration := PlayerConfiguration{
		id: id,
		playerLogChannel: createGUI(id),
		playerChannel: make(chan Message),
	}
	go gameHandler(playerConfiguration, max)
	return playerConfiguration.playerChannel
}