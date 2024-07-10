package main

import (	
	"fmt"
)

func gameHandler(playerConfiguration PlayerConfiguration, max int) {
	
	waitForStart(playerConfiguration, 0, max)
}
func waitForStart(playerConfiguration PlayerConfiguration, min, max int) {
	for eventMessage := range playerConfiguration.playerBackendConfiguration.playerEventChannel{
		switch eventMessage {
		case Start:
			playerConfiguration.playerLogChannel <- "Game started"
			guess := calculateGuess(min, max)
			clientMessage := ClientMessage{guess: guess, senderId: playerConfiguration.id}
			playerConfiguration.playerBackendConfiguration.playerChannel <- clientMessage
			playGame(playerConfiguration, min, max, guess)
		}
	}
}

func playGame(playerConfiguration PlayerConfiguration, min, max, guess int) {
	turn := 0
	for gameMessage := range playerConfiguration.playerBackendConfiguration.playerChannel{
		switch m := gameMessage.(type) {
		case ServerMessage:
			playerConfiguration.playerLogChannel <- fmt.Sprintf("starting turn %d", turn)
			turn++
			switch m.hint {
			case Lower:
				max = guess
				guess = calculateGuess(min, max)
				playerConfiguration.playerLogChannel <- fmt.Sprintf("Player %s: %d", playerConfiguration.id, guess)
				playerConfiguration.playerBackendConfiguration.playerChannel <- ClientMessage{guess: guess, senderId: playerConfiguration.id}
			case Higher:
				min = guess
				guess = calculateGuess(min, max)
				playerConfiguration.playerLogChannel <- fmt.Sprintf("Player %s: %d", playerConfiguration.id, guess)
				playerConfiguration.playerBackendConfiguration.playerChannel <- ClientMessage{guess: guess, senderId: playerConfiguration.id} 	
			case Win:
				fmt.Println(playerConfiguration.id, "WIN")
				playerConfiguration.playerLogChannel <- fmt.Sprintf("Player %s: WIN", playerConfiguration.id)
				playerConfiguration.playerBackendConfiguration.playerEventChannel <- End
				return
			case Lose:
				fmt.Println(playerConfiguration.id, "LOSE")
				playerConfiguration.playerLogChannel <- fmt.Sprintf("Player %s: LOSE", playerConfiguration.id)
				playerConfiguration.playerBackendConfiguration.playerEventChannel <- End
				return
			} 
		}
	}	
}
	

func calculateGuess(lowerBound, upperBound int) int {
	guess := (lowerBound + upperBound)/2
	return guess
}

func spawnPlayer(id string, max int) PlayerBackendConfiguration {
	playerConfiguration := PlayerConfiguration{
		id: id,
		playerLogChannel: createGUI(id),
		playerBackendConfiguration: PlayerBackendConfiguration{
			playerChannel: make(chan Message),
			playerEventChannel: make(chan Status),
		},
	}
	go gameHandler(playerConfiguration, max)
	return playerConfiguration.playerBackendConfiguration
}