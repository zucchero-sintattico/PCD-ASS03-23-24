package main

type Status int

const(
	Start Status = iota
	End
)

type Hint int

const(
	Lower Hint = iota
	Higher 
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

type PlayerBackendConfiguration struct {
	playerChannel chan Message
	playerEventChannel chan Status
}

type PlayerConfiguration struct {
	id string
	playerLogChannel chan string
	playerBackendConfiguration PlayerBackendConfiguration 
}
