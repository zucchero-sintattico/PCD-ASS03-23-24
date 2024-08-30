package main

import (
	"fmt"
	"math/rand"
	"reflect"
)

func createGame(numPlayers, max int) {
	numberToGuess := rand.Intn(max)
	println("Number to guess: ", numberToGuess)
	playerChannels, fanIn := setupGame(numPlayers, max)

	fmt.Println("Game started")
	handleGame(playerChannels, fanIn, numberToGuess)
	fmt.Println("Game finished")
}

func setupPlayers(numPlayers, max int) []chan Message {
	var channels []chan Message
	for i := 0; i < numPlayers; i++ {
		playerId := fmt.Sprintf("player-%d", i)
		fmt.Println(playerId, "joined")
		ch := spawnPlayer(playerId, max)
		channels = append(channels, ch)
	}
	return channels
}

func setupGame(numPlayers, max int) ([]chan Message, []reflect.SelectCase) {
	channels := setupPlayers(numPlayers, max)
	fanIn := make([]reflect.SelectCase, len(channels))
	for i, ch := range channels {
		fanIn[i] = reflect.SelectCase{Dir: reflect.SelectRecv, Chan: reflect.ValueOf(ch)}
	}
	return channels, fanIn
}

func handleGame(playerChannels []chan Message, fanIn []reflect.SelectCase, numberToGuess int) {
	turnResponses := make(map[chan Message]ServerMessage)
	closedChannels := make(map[chan Message]bool)
	win := false
	for len(closedChannels) != len(playerChannels) {
		channelIndex, message, isOpen := reflect.Select(fanIn)
		ch := playerChannels[channelIndex]
		switch msg := message.Interface().(type) {
		case ClientMessage:
			turnResponses[ch] = handleClientMessage(msg, numberToGuess, &win)
		default:
			if !isOpen {
				closedChannels[ch] = true
			}
		}
		if len(turnResponses) == len(playerChannels) {
			for playerChannel, serverMessage := range turnResponses {
				playerChannel <- serverMessage
			}
			turnResponses = make(map[chan Message]ServerMessage)
		}
	}
}

func handleClientMessage(msg ClientMessage, numberToGuess int, win *bool) ServerMessage {
	guess := msg.guess
	fmt.Printf("%s guess: %d\n", msg.senderId, guess)
	switch {
	case *win:
		return ServerMessage{hint: Lose}
	case guess < numberToGuess:
		return ServerMessage{hint: Higher}
	case guess > numberToGuess:
		return ServerMessage{hint: Lower}
	default:
		*win = true
		return ServerMessage{hint: Win}
	}
}