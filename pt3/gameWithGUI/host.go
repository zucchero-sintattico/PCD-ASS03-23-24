package main

import (
	"fmt"
	"math/rand"
	"reflect"
)

func createGame(numPlayers, max int, masterGUIChannel chan Status) {
	playerBackendConfigurations, messageFanIn, eventFanIn, stats:= setupGame(numPlayers, max)

	logChannel := make(chan string)
	showGUI("Host", logChannel)

	for eventMsg := range masterGUIChannel {
		switch eventMsg {
		case Start:
			numberToGuess := rand.Intn(max)
			logChannel <- fmt.Sprintf("Game started, number to guess: %d", numberToGuess)
			winnerId := handleGame(playerBackendConfigurations, messageFanIn, eventFanIn, numberToGuess)
			stats[winnerId]++
			logStats(stats, logChannel)
			logChannel <- "Game finished"
			masterGUIChannel <- End
		}
	}	
}

func setupPlayers(numPlayers, max int) ([]PlayerBackendConfiguration, map[string]int) {
	var channels []PlayerBackendConfiguration
	stats := make(map[string]int)
	for i := 0; i < numPlayers; i++ {
		playerId := fmt.Sprintf("player-%d", i)
		stats[playerId] = 0
		ch := spawnPlayer(playerId, max)
		channels = append(channels, ch)
	}
	return channels, stats
}

func setupGame(numPlayers, max int) ([]PlayerBackendConfiguration, []reflect.SelectCase, []reflect.SelectCase, map[string]int) {
	channels, stats := setupPlayers(numPlayers, max)
	messageFanIn := make([]reflect.SelectCase, len(channels))
	eventFanIn := make([]reflect.SelectCase, len(channels))
	for i, ch := range channels {
		messageFanIn[i] = reflect.SelectCase{Dir: reflect.SelectRecv, Chan: reflect.ValueOf(ch.playerChannel)}
		eventFanIn[i] = reflect.SelectCase{Dir: reflect.SelectRecv, Chan: reflect.ValueOf(ch.playerEventChannel)}
	}
	return channels, messageFanIn, eventFanIn, stats
}

func handleGame(playerBackendConfigurations []PlayerBackendConfiguration, messageFanIn, eventFanIn []reflect.SelectCase, numberToGuess int) string {
	notifyClientToStart(playerBackendConfigurations)
	winnerID := handleClients(messageFanIn, playerBackendConfigurations, numberToGuess)
	waitForPlayersToFinish(playerBackendConfigurations, eventFanIn)
	return winnerID
}

func notifyClientToStart(playerBackendConfigurations []PlayerBackendConfiguration) {
	for _, ch := range playerBackendConfigurations {
		ch.playerEventChannel <- Start
	}
}

func handleClients(messageFanIn []reflect.SelectCase, playerBackendConfigurations []PlayerBackendConfiguration, numberToGuess int) string {
	turnResponses := make(map[chan Message]ServerMessage)
	win := false
	var winnerId string
	for {
		channelIndex, message, _ := reflect.Select(messageFanIn)
		playerChannel := playerBackendConfigurations[channelIndex].playerChannel
		switch msg := message.Interface().(type) {
		case ClientMessage:
			turnResponses[playerChannel] = handleClientMessage(msg, numberToGuess, &win, &winnerId)
		}
		if len(turnResponses) == len(playerBackendConfigurations) {
			for playerChannel, serverMessage := range turnResponses { playerChannel <- serverMessage }
			if win { break }
			turnResponses = make(map[chan Message]ServerMessage)
		}
	}
	return winnerId
}

func handleClientMessage(msg ClientMessage, numberToGuess int, win *bool, winnerId *string) ServerMessage {
	guess := msg.guess
	switch {
	case *win: //situation where guess == numberToGuess and game is already won could be handle better
		return ServerMessage{hint: Lose}
	case guess < numberToGuess:
		return ServerMessage{hint: Higher}
	case guess > numberToGuess:
		return ServerMessage{hint: Lower}
	default: 
		*win = true
		*winnerId = msg.senderId
		return ServerMessage{hint: Win}
	}
}

func waitForPlayersToFinish(playerBackendConfigurations []PlayerBackendConfiguration, eventFanIn []reflect.SelectCase) {
	endNotification := 0
	for endNotification < len(playerBackendConfigurations) {
		_, message, _ := reflect.Select(eventFanIn)
		switch message.Interface() {
		case End:
			endNotification++
		}
	}
}

func logStats(stats map[string]int, logChannel chan string) {
	for id, score := range stats {
		logChannel <- fmt.Sprintf("%s: %d", id, score)
	}
}