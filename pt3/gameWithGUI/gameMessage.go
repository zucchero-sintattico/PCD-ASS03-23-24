package main

type Hint int

const(
	Lower Hint = iota
	Higher 
	Win
	Lose 
)
 
type Message interface {}

type StatusMessage int

const(
	Start StatusMessage = iota
	End
)

type ServerMessage struct {
	hint Hint
}

type ClientMessage struct {
	guess int
	senderId string 
}
