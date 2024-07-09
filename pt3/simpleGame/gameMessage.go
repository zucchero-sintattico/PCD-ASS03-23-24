package main

type Hint int

const(
	Lower Hint = iota
	Upper 
	Win
	Lose 
)

type Message interface {}

type ServerMessage struct {
	hint Hint
}

type ClientMessage struct {
	guess int
	senderId string 
}

type GameConfiguration struct {
	upperBound int
	turns int 
}