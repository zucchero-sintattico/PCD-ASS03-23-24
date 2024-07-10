package main

import (
	"log"
	"strconv"
	"fyne.io/fyne/v2/widget"
	"fyne.io/fyne/v2"
)

func showAndRunGameGUI() {
	
	window := application.NewWindow("Controller GUI")
	window.Resize(fyne.NewSize(600, 150))
	ch := make(chan Status)

	numPlayerEntry := widget.NewEntry()
	maxNumEntry := widget.NewEntry()

	var start *widget.Button
	start = widget.NewButton("StartRound", func() {
		start.Disable()
		ch <- Start
		go func() {
			switch msg := <-ch; msg {
			case End:
				start.Enable()
			}
		}()
	})
	numPlayerEntry.SetText("20")
	maxNumEntry.SetText("1000")
	form := &widget.Form{
		Items: []*widget.FormItem{ // we can specify items in the constructor
			{Text: "numPlayer", Widget: numPlayerEntry}},
		OnSubmit: func() { // optional, handle form submission
			log.Println("Form submitted:", numPlayerEntry.Text)
			log.Println("multiline:", maxNumEntry.Text)
			n1, err1 := strconv.Atoi(numPlayerEntry.Text)
			n2, err2 := strconv.Atoi(maxNumEntry.Text)
			if err1 != nil || err2 != nil {
				numPlayerEntry.SetText("")
				maxNumEntry.SetText("")
			} else {
				window.SetContent(start)
				go createGame(n1,n2, ch)
			}
		},
	}

	
	

	// container := container.NewVBox(
	// 	form,
	// 	start,
	// )
	// we can also append items
	form.Append("maxNum", maxNumEntry)

	window.SetOnClosed(func() {
		println("quit")
		application.Quit()
	})
	window.SetContent(form)
	window.ShowAndRun()
	// application.Run()
}