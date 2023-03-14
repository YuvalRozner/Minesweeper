package mines;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

//MinesFX is the application which responsible for the graphic and all the user interface of the mineweeper game.
public class MinesFX extends Application {
	// class properties:
	private Stage stage; // the stage of the game window.
	private MyController controller; // the controller, to communicate with it's method.
	private BorderPane root; // the root of the scene.
	private Mines game; // the backend implement of the game itself.
	private GridPane boardGrid; // the grid of buttons which created manually.
	private IndexedButton[][] boardButtons; // matrix of the buttons.
	// properties of the game:
	private int height;
	private int width;
	private int mines;
	private int minesCount; // the count of flag set.
	private int lifesCounter;

	// IndexedButton class is an extension of javafx Button Class.
	// It has two additional properties, the indexes (row,col).
	public class IndexedButton extends Button {
		// class properties:
		private int row;
		private int col;

		// constructor. using the constructor of the super class 'Button', and save the indexes.
		public IndexedButton(String text, int row, int col) {
			super(text);
			this.row = row;
			this.col = col;
		}

		// getButton method is an implements to enable the 'Button''s getButton method works for IndexedButton as well.
		// it will used to notice which mouse button pressed (primary or secondary).
		public MouseButton getButton() {
			return (MouseButton) getProperties().get("javafx.scene.control.MouseButton");
		}
	}

	// the main method which run and launch the game.
	public static void main(String[] args) {
		launch(args); // Launch the JavaFX application
	}

	@Override
	// The main entry point for the application.
	// it creates a scene and show it with the blank game board.
	public void start(Stage stage) {
		this.stage = stage;
		root = getGameInterface(); // the root created by the SceneBuilder.
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Yuval Rozner's Best MineWeeper!");
		stage.show();
		controller.reset(null); // start the default game 10*10*10 automatically.
	}

	// getGameInterface method, load the FXML file and creates the root by it.
	// it returns the root type of GridPane.
	private BorderPane getGameInterface() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("GameUserInterface.fxml"));
			root = loader.load();
			// make both class recognize each other, so they could use each other's methods:
			controller = loader.getController();
			controller.setMainClass(this);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return root;
	}

	// createBoardGrid method, creates a new game, and a new board which is a grid of IndexedButtons.
	// it use Mine class to set the game itself, and to control the sign of each button.
	public void createBoardGrid(int height, int width, int mines) {
		minesCount = 0; // resets the counter of the flag from last game.
		// sets the properties of the game:
		this.height = height;
		this.width = width;
		this.mines = mines;
		lifesCounter = 3;
		controller.setLifes(lifesCounter);
		controller.setNewMinesCounting(0, mines); // resets the mines counting
		game = new Mines(height, width, mines); // creates the backend of the game.

		boardGrid = new GridPane(); // creates the grid for placing the buttons in it.
		boardGrid.setAlignment(Pos.CENTER); // set it to the center of the window.
		boardGrid.setPrefSize(width, height); // set the preferred size of the grid.

		boardButtons = new IndexedButton[height][width]; // allocates the matrix of buttons.
		// run over the buttons, allocates them, sets their text and font and adds them to the grid in the correct place.
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++) {
				boardButtons[i][j] = new IndexedButton(game.get(i, j), i, j);
				boardButtons[i][j].setMinHeight(28);
				boardButtons[i][j].setMinWidth(28);
				boardButtons[i][j].setOnMouseClicked(new play());
				boardGrid.add(boardButtons[i][j], j, i);
			}

		// sets the new dimensions of the game window by the size of the board:
		stage.setHeight(300);
		stage.setWidth(250);
		if (width * 29 + 150 >= 250)
			stage.setWidth(width * 29 + 180);
		if (height * 29 + 30 >= 300)
			stage.setHeight(height * 29 + 50);

		root.setRight(boardGrid); // add the new game board to the root of the scene.
	}

	// the method runs over all the buttons on the game board and reset their signs.
	// Typically in use after any change of the board, open, toggle flag ..
	public void reshowAllBoardSigns() {
		minesCount = 0; // resets the counter.
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++) {
				boardButtons[i][j]. setText(game.get(i, j));
				if (game.get(i, j) == "F") // checks if it is a flag.
					minesCount++; // count how may flags already set.
			}
		controller.setNewMinesCounting(minesCount, mines); // sets the new counting of flags.
	}

	// the class represents a turn in the game (a click on one of the buttons).
	// it used for the action of the event of each button in the game board grid.
	class play implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			IndexedButton button = (IndexedButton) event.getSource();
			int row = button.row;
			int col = button.col;
			MouseButton bType = event.getButton();
			if (bType == MouseButton.PRIMARY) { // if the left mouse button pressed, open.
				if (!game.open(row, col)) { // checks losing. if open a mine.
					controller.setLifes(--lifesCounter);
					if (lifesCounter == 0) {
						game.setShowAll(true); // if lose, it reveals the board.
						popUpMessage("Opss..");
					}
				}
				if (game.isDone()) // checks winning.
					popUpMessage("OMG, You Won!!!");
			} else if (bType == MouseButton.SECONDARY) // if the right mouse button pressed, toggle flag.
				game.toggleFlag(row, col);
			reshowAllBoardSigns();
		}
	}

	// the method gets a String message, it creates a new window with the message written in it, and shows it on the screen.
	// the popUpMessage method is used for the winning and losing popping messages.
	private void popUpMessage(String msg) {
		BorderPane msgWindow = new BorderPane();
		Label msgLabel = new Label(msg);
		msgLabel.setFont(new Font("Ariel", 22));
		msgWindow.setCenter(msgLabel);
		msgWindow.setMinHeight(70);
		msgWindow.setMinWidth(210);
		Stage msgStage = new Stage();
		msgStage.setScene(new Scene(msgWindow));
		msgStage.show();
	}

	// startOver method, resets the same game board for another try.
	public void startOver() {
		game.startOver();
		game.setShowAll(false);
		lifesCounter = 3;
		controller.setLifes(lifesCounter);
		reshowAllBoardSigns();
	}
}