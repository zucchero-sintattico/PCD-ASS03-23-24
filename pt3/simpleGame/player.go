package main

import (	
	"fmt"
)

func playGame(ch chan Message, id string, configuration GameConfiguration) {
	var lowerBound, upperBound = 0, configuration.upperBound
	var guess = calculateGuess(lowerBound, upperBound)
	ch <- ClientMessage{guess: guess, senderId: id}
	for msg := range ch {
        switch m := msg.(type) {
       		case ServerMessage:
				switch m.hint {
					case Lower:
						upperBound = guess
						guess = calculateGuess(lowerBound, upperBound)
						ch <- ClientMessage{guess: guess, senderId: id}
					case Upper:
						lowerBound = guess
						guess = calculateGuess(lowerBound, upperBound)
						ch <- ClientMessage{guess: guess, senderId: id}	 	
					case Win:
						fmt.Println(id, "WIN")
						close(ch)
					case Lose:
						fmt.Println(id, "LOSE")
						close(ch)


				}
				
        }
    }
}

func calculateGuess(lowerBound, upperBound int) int {
	guess := (lowerBound + upperBound)/2
	return guess
}

func spawnPlayer(id string, configuration GameConfiguration) chan Message {
	ch := make(chan Message)
	go playGame(ch, id, configuration)
	return ch
}