package main

import (
	"fmt"
	"math/rand"
	"reflect"
)

func spawnHostAndCreateGame(numPlayers, max int) chan StatusMessage {
	gameStatusChannel := make(chan StatusMessage)
	go createGameAndWaitStart(numPlayers, max, gameStatusChannel)
	return gameStatusChannel
}

func createGameAndWaitStart(numPlayers, max int, gameStatusChannel chan StatusMessage) {
	playerChannels, fanIn, stats := setupGame(numPlayers, max)
	logChannel := createGUI("Host")
	for eventMsg := range gameStatusChannel {
		switch eventMsg {
		case Start:
			numberToGuess := rand.Intn(max)
			logChannel <- fmt.Sprintf("Game started, number to guess: %d", numberToGuess)
			winnerId := handleGame(playerChannels, fanIn, numberToGuess)
			stats[winnerId]++
			logStats(stats, logChannel)
			logChannel <- "Game finished"
			gameStatusChannel <- End
		}
	}
}
func setupPlayers(numPlayers, max int) ([]chan Message, map[string]int) {
	var channels []chan Message
	stats := make(map[string]int)
	for i := 0; i < numPlayers; i++ {
		playerId := fmt.Sprintf("player-%d", i)
		stats[playerId] = 0
		ch := spawnPlayer(playerId, max)
		channels = append(channels, ch)
	}
	return channels, stats
}

func setupGame(numPlayers, max int) ([]chan Message, []reflect.SelectCase, map[string]int) {
	channels, stats := setupPlayers(numPlayers, max)
	fanIn := make([]reflect.SelectCase, len(channels))
	for i, ch := range channels {
		fanIn[i] = reflect.SelectCase{Dir: reflect.SelectRecv, Chan: reflect.ValueOf(ch)}
	}
	return channels, fanIn, stats
}

func handleGame(playerChannels []chan Message, fanIn []reflect.SelectCase, numberToGuess int) string {
	notifyClientToStart(playerChannels)
	winnerID := handleClients(playerChannels, fanIn, numberToGuess)
	waitForPlayersToFinish(playerChannels, fanIn)
	return winnerID
}

func notifyClientToStart(playerChannels []chan Message) {
	for _, ch := range playerChannels {
		ch <- Start
	}
}

func handleClients(playerChannels []chan Message, fanIn []reflect.SelectCase, numberToGuess int) string {
	turnResponses := make(map[chan Message]ServerMessage)
	win := false
	var winnerId string
	for {
		channelIndex, message, _ := reflect.Select(fanIn)
		ch := playerChannels[channelIndex]
		switch msg := message.Interface().(type) {
		case ClientMessage:
			turnResponses[ch] = handleClientMessage(msg, numberToGuess, &win, &winnerId)
		}
		if len(turnResponses) == len(playerChannels) {
			for playerChannel, serverMessage := range turnResponses {
				playerChannel <- serverMessage
			}
			if win { break }
			turnResponses = make(map[chan Message]ServerMessage)
		}
	}
	return winnerId
}

func handleClientMessage(msg ClientMessage, numberToGuess int, win *bool, winnerId *string) ServerMessage {
	guess := msg.guess
	switch {
	case *win:
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

func waitForPlayersToFinish(playerChannels []chan Message, fanIn []reflect.SelectCase) {
	endNotification := 0
	for endNotification < len(playerChannels) {
		_, message, _ := reflect.Select(fanIn)
		switch message.Interface() {
		case End:
			endNotification++
		}
	}
}

func logStats(stats map[string]int, logChannel chan<- string) {
	for id, score := range stats {
		logChannel <- fmt.Sprintf("%s: %d", id, score)
	}
}