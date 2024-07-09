package main

import (
	"fmt"
	"math/rand"
	"reflect"
)

func createGame(numPlayers, max int) {

	numberToGuess := rand.Intn(max)
	println("Number to guess: ", numberToGuess)
	channels, fanInCases := setupGame(numPlayers, max)

	fmt.Println("Game started")
	startGame(channels, fanInCases, numberToGuess)
	fmt.Println("Game finished")
}

func setupGame(numPlayers, max int) ([]chan Message, []reflect.SelectCase) {
	channels := setupChannels(numPlayers, max)
	cases := make([]reflect.SelectCase, len(channels))
	for i, ch := range channels {
		cases[i] = reflect.SelectCase{Dir: reflect.SelectRecv, Chan: reflect.ValueOf(ch)}
	}
	return channels, cases
}

func setupChannels(numPlayers, max int) []chan Message {
	var channels []chan Message
	for i := 0; i < numPlayers; i++ {
		playerId := fmt.Sprintf("player-%d", i)
		fmt.Println(playerId, "joined")
		ch := spawnPlayer(playerId, max)
		channels = append(channels, ch)
	}
	return channels
}

func startGame(channels []chan Message, fanInCases []reflect.SelectCase, numberToGuess int) {
	turnResponses := make(map[chan Message]ServerMessage)
	closedChannels := make(map[chan Message]bool)
	win := false
	for len(closedChannels) != len(channels) {
		channelIndex, message, isOpen := reflect.Select(fanInCases)
		ch := channels[channelIndex]
		switch msg := message.Interface().(type) {
		case ClientMessage:
			turnResponses[ch] = handleClientMessage(msg, numberToGuess, &win)
			if len(turnResponses) == len(channels) {
				for k, v := range turnResponses {
					k <- v
				}
				turnResponses = make(map[chan Message]ServerMessage)
			}
		default:
			//handle channels closures done by clients
			if !isOpen {
				closedChannels[ch] = true
			}
		}
	}
}

func handleClientMessage(msg ClientMessage, numberToGuess int, win *bool) ServerMessage {
	guess := msg.guess
	fmt.Printf("%s guess: %d\n", msg.senderId, guess)
	switch {
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

/*
alternative to make a fanInChannel that cause deadlock:
for redirect message and listen for a server response concurrency come to play, so two channels are needed

agg := make(chan string)
for _, ch := range chans {
  go func(c chan string) {
    for msg := range c {
      agg <- msg
    }
  }(ch)
}

select {
case msg <- agg:
    fmt.Println("received ", msg)
}
*/
