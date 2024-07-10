package main

import (
	"strconv"
	"fyne.io/fyne/v2/widget"
	"fyne.io/fyne/v2"
)

func showAndRunGameGUI() {
	window := application.NewWindow("Controller GUI")
	window.Resize(fyne.NewSize(600, 150))

	gameStatusChannel := make(chan Status)

	startGameButton :=  createStartGameButton(gameStatusChannel)

	onCreate := func (numPlayer, maxNum int) {
		window.SetContent(startGameButton)
		go createGame(numPlayer,maxNum, gameStatusChannel)
	}
	
	form := createSetupForm(onCreate)

	window.SetOnClosed(func() {application.Quit()})
	window.SetContent(form)
	window.ShowAndRun()
}

func createStartGameButton(gameStatusChannel chan Status) *widget.Button {
	var start *widget.Button
	start = widget.NewButton("StartRound", func() {
		start.Disable()
		gameStatusChannel <- Start
		go func() {
			switch msg := <-gameStatusChannel; msg {
			case End:
				start.Enable()
			}
		}()
	})
	return start
}

func createSetupForm(onCreate func(int, int)) *widget.Form {
	numPlayerEntry := widget.NewEntry()
	numPlayerEntry.SetText("10")
	maxNumEntry := widget.NewEntry()
	maxNumEntry.SetText("100")
	return &widget.Form{
		Items: []*widget.FormItem{
			{Text: "numPlayer", Widget: numPlayerEntry},
			{Text: "maxNum", Widget: maxNumEntry},
		},
		OnSubmit: func() { 
			numPlayer, err1 := strconv.Atoi(numPlayerEntry.Text)
			maxNum, err2 := strconv.Atoi(maxNumEntry.Text)
			switch {
			case err1 != nil:
				numPlayerEntry.SetText("")
			case err2 != nil:
				maxNumEntry.SetText("")
			default:
				onCreate(numPlayer, maxNum)
			}
		},
	}
}