package main

import (
	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/widget"
)

func showGUI(name string, logChannel chan string) {
	window := application.NewWindow(name + " GUI")
	window.Resize(fyne.NewSize(400, 400))

	multilineEntry := widget.NewMultiLineEntry()
	multilineEntry.SetText("StartLogging")

	go guiUpdater(multilineEntry, logChannel)

	window.SetContent(multilineEntry)
	window.Show()
}

func guiUpdater(textGUI *widget.Entry, channel chan string) {
	for msg := range channel {
		textGUI.SetText(textGUI.Text + "\n" + msg)
		textGUI.CursorRow = len(textGUI.Text) - 1
		textGUI.Refresh()
	}
}

func createGUI(name string) chan string {
	logChannel := make(chan string)
	showGUI(name, logChannel)
	return logChannel
}
