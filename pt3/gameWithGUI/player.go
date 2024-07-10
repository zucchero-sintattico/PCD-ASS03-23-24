package main

import (	
	"fmt"
)

type PlayerBackendConfiguration struct {
	playerChannel chan Message
	playerEventChannel chan Status
}

type PlayerConfiguration struct {
	id string
	playerLogChannel chan string
	playerBackendConfiguration PlayerBackendConfiguration 
}

func gameHandler(playerConfiguration PlayerConfiguration, max int) {
	waitForStart(playerConfiguration, 0, max)
}

func waitForStart(playerConfiguration PlayerConfiguration, min, max int) {
	for eventMessage := range playerConfiguration.playerBackendConfiguration.playerEventChannel {
		switch eventMessage {
		case Start:
			playerConfiguration.playerLogChannel <- "Game started"
			guess, turn := playTurn(playerConfiguration, 0, min, max)
			playGame(playerConfiguration, min, max, guess, turn)
		}
	}
}

func playGame(playerConfiguration PlayerConfiguration, min, max, guess, turn int) {
	for gameMessage := range playerConfiguration.playerBackendConfiguration.playerChannel {
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
				playerConfiguration.playerLogChannel <- fmt.Sprintf("Player %s: WIN", playerConfiguration.id)
				playerConfiguration.playerBackendConfiguration.playerEventChannel <- End
				return
			case Lose:
				playerConfiguration.playerLogChannel <- fmt.Sprintf("Player %s: LOSE", playerConfiguration.id)
				playerConfiguration.playerBackendConfiguration.playerEventChannel <- End
				return
			} 
		}
	}	
}

func playTurn(playerConfiguration PlayerConfiguration, turn, min, max int) (int, int) {
	playerConfiguration.playerLogChannel <- fmt.Sprintf("starting turn %d", turn)
	guess := calculateGuess(min, max)
	playerConfiguration.playerLogChannel <- fmt.Sprintf("Player %s: %d", playerConfiguration.id, guess)
	playerConfiguration.playerBackendConfiguration.playerChannel <- ClientMessage{guess: guess, senderId: playerConfiguration.id}
	return guess, turn + 1
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