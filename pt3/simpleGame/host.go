package main

import (
	"fmt"
	"math/rand"
	"reflect"
)
	
func createGame(numPlayers int, configuration GameConfiguration) {
	
	numberToGuess := rand.Intn(configuration.upperBound)
	println("Number to guess: ", numberToGuess)
	channels, fanInCases := setupGame(
		numPlayers, 
		configuration,
	)

	fmt.Println("Game started")
	startGame(channels, fanInCases, numberToGuess, configuration.turns)
	fmt.Println("Game finished")
}

func setupGame(numPlayers int, configuration GameConfiguration) ([]chan Message, []reflect.SelectCase) {
	channels := setupChannels(numPlayers, configuration)
	cases := make([]reflect.SelectCase, len(channels))
	for i, ch := range channels {
		cases[i] = reflect.SelectCase{Dir: reflect.SelectRecv, Chan: reflect.ValueOf(ch)}
	}
	return channels, cases
}

func setupChannels(numPlayers int, configuration GameConfiguration) []chan Message {
	var channels []chan Message
	for i := 0; i < numPlayers; i++ {
		playerId := fmt.Sprintf("player-%d", i)
		fmt.Println(playerId, "joined")
		ch := spawnPlayer(playerId, configuration)
		channels = append(channels, ch)
	}
	return channels
}

func startGame(channels []chan Message, fanInCases []reflect.SelectCase, numberToGuess int, turns int) {
	turnResponses := make(map[chan Message]ServerMessage)
	closedChannels := make(map[chan Message]bool)
	currentTurn := 0
	win := false
	for len(closedChannels) != len(channels) {
		channelIndex, message, isOpen := reflect.Select(fanInCases)
		ch := channels[channelIndex]
		lastTurn := (turns - currentTurn) == 1
		switch msg := message.Interface().(type) {
			case ClientMessage:
				turnResponses[ch] = handleClientMessage(msg, numberToGuess, &win, lastTurn) 
				if len(turnResponses) == len(channels) {
					for k, v := range turnResponses {
						k <- v
					}
					turnResponses = make(map[chan Message]ServerMessage)
					currentTurn++
				}
			default:
				//handle channels closures done by clients
				if !isOpen {
					closedChannels[ch] = true
				}
		}
	}
}

func handleClientMessage(msg ClientMessage, numberToGuess int, win *bool, lastTurn bool) ServerMessage {
	guess := msg.guess
	fmt.Printf("%s guess: %d\n", msg.senderId, guess)
	switch {
		case lastTurn:
			return ServerMessage{hint: Lose}
		case guess < numberToGuess:
			return ServerMessage{hint: Upper}
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