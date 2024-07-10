package main
// import fyne
import (
    "fyne.io/fyne/v2"
    "fyne.io/fyne/v2/widget"
)
func showGUI(name string, channel chan string) {
    // New app
    
    // new windown and title
    window := application.NewWindow(name+" GUI")
    // resize
    window.Resize(fyne.NewSize(400, 400))

    // New Scroll & Vbox
	
    multilineEntry := widget.NewMultiLineEntry()
    multilineEntry.SetText("StartLogging")
    //multilineEntry.SetText("Lorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \nLorem Ipsum ... \n ")
    go GUIUpdater(multilineEntry, channel)
    
    //focusedItem := nil
    // If the user is not focused on the text area then scroll to the end
    //if focusedItem == nil || focusedItem != multilineEntry {
    multilineEntry.CursorRow = len(multilineEntry.Text) - 1 // Sets the cursor to the end
    //}
	multilineEntry.Refresh()
    //multilineEntry.CursorRow = len(multilineEntry.Text) - 1
    // Change scroll direction
    //c.Direction = container.ScrollVerticalOnly
    // setup content
    window.SetContent(multilineEntry)
    // show and run

    window.Show()
	
	multilineEntry.CursorRow = 20// Sets the cursor to the end
	
	println(multilineEntry.CursorRow)
    
}

func GUIUpdater(textGUI *widget.Entry, channel chan string) {
	for msg := range channel {
		textGUI.SetText(textGUI.Text+"\n"+msg)
        textGUI.CursorRow = len(textGUI.Text) - 1
        textGUI.Refresh()
	}
}

func createGUI(playerName string) chan string {
	ch := make(chan string)
	defer showGUI(playerName, ch)
	return ch
}