package main

import (
	"fmt"
	"math/rand"
	"reflect"
)




func createGame(numPlayers, max int, masterGUIChannel chan Status) {

	numberToGuess := rand.Intn(max)
	playerBackendConfigurations, messageFanIn, eventFanIn := setupGame(numPlayers, max)

	println("Max: ", max)
	println("Number to guess: ", numberToGuess)
	

	for eventMsg := range masterGUIChannel {
		switch eventMsg {
		case Start:
			fmt.Println("Game started")
			startGame(playerBackendConfigurations, messageFanIn, eventFanIn, numberToGuess)
			masterGUIChannel <- End
		}
	}
	
	fmt.Println("Game finished")
	
}

func setupPlayers(numPlayers, max int) []PlayerBackendConfiguration {
	var channels []PlayerBackendConfiguration
	for i := 0; i < numPlayers; i++ {
		playerId := fmt.Sprintf("player-%d", i)
		fmt.Println(playerId, "joined")
		ch := spawnPlayer(playerId, max)
		channels = append(channels, ch)
	}
	return channels
}

func setupGame(numPlayers, max int) ([]PlayerBackendConfiguration, []reflect.SelectCase, []reflect.SelectCase) {
	channels := setupPlayers(numPlayers, max)
	messageFanIn := make([]reflect.SelectCase, len(channels))
	eventFanIn := make([]reflect.SelectCase, len(channels))
	for i, ch := range channels {
		messageFanIn[i] = reflect.SelectCase{Dir: reflect.SelectRecv, Chan: reflect.ValueOf(ch.playerChannel)}
		eventFanIn[i] = reflect.SelectCase{Dir: reflect.SelectRecv, Chan: reflect.ValueOf(ch.playerEventChannel)}
	}
	return channels, messageFanIn, eventFanIn
}



func startGame(playerBackendConfigurations []PlayerBackendConfiguration, messageFanIn, eventFanIn []reflect.SelectCase, numberToGuess int) {
	turnResponses := make(map[chan Message]ServerMessage)
	win := false
	for _, ch := range playerBackendConfigurations {
		ch.playerEventChannel <- Start
	}

	for {
		channelIndex, message, _ := reflect.Select(messageFanIn)
		playerChannel := playerBackendConfigurations[channelIndex].playerChannel
		switch msg := message.Interface().(type) {
		case ClientMessage:
			turnResponses[playerChannel] = handleClientMessage(msg, numberToGuess, &win)
		}
		if len(turnResponses) == len(playerBackendConfigurations) {
			for k, v := range turnResponses {
				k <- v
			}
			if win { 
				println("preBreak")
				break 
			}
			turnResponses = make(map[chan Message]ServerMessage)
		}
	}

	endNotification := 0
	for endNotification < len(playerBackendConfigurations) {
		_, message, _ := reflect.Select(eventFanIn)
		switch message.Interface(){
		case End: endNotification++
		}
	}
	fmt.Println("Game finished")
}

func handleClientMessage(msg ClientMessage, numberToGuess int, win *bool) ServerMessage {
	guess := msg.guess
	fmt.Printf("%s guess: %d\n", msg.senderId, guess)
	switch {
	case guess < numberToGuess:
		return ServerMessage{hint: Higher}
	case guess > numberToGuess:
		return ServerMessage{hint: Lower}
	default: //situation where guess == numberToGuess and game is already won could be handle better
		if *win {
			return ServerMessage{hint: Lose}
		} else {
			*win = true
			return ServerMessage{hint: Win}
		}
	}
}

