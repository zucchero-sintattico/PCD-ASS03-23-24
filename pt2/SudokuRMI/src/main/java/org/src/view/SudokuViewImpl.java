package org.src.view;

import org.src.model.Controller;
import org.src.model.grid.SudokuGrid;

import javax.swing.*;

public class SudokuViewImpl implements SudokuView {

    private final Menu menu;
    private final GridView gridView;
    private final Controller controller;

    private JFrame currentScreen;

    public SudokuViewImpl(Controller controller) {
        this.controller = controller;
        this.menu = new Menu(this.controller);
        this.gridView = new GridView(this.controller);

        this.currentScreen = this.menu;
        this.menu.onChange(this.changeScreen(this.gridView));
        this.gridView.onChange(this.changeScreen(this.menu));
    }

    private Runnable changeScreen(JFrame gridView) {
        return () -> {
            SwingUtilities.invokeLater(() -> {
                this.currentScreen.setVisible(false);
                this.currentScreen = gridView;
                this.currentScreen.setVisible(true);
            });
        };
    }

    @Override
    public void update(SudokuGrid grid) {
        this.gridView.update(grid);
    }

    @Override
    public void display() {
        SwingUtilities.invokeLater(() -> {
            this.currentScreen.setVisible(true);
        });
    }

}