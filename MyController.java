package mines;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

//MyController is a controller class to access and use the properties of the scene built by the SceneBuilder into FXML file.
public class MyController {
	// properties from the FXML file:
	@FXML
	private Label minesC;
	@FXML
	private TextField heightVal;
	@FXML
	private TextField minesVal;
	@FXML
	private TextField widthVal;
	@FXML
	private HBox lifes;

	// additional properties:
	private MinesFX mainClass;

	@FXML
	// reset is a method created from the FXML file to define what the 'reset' button does.
	// when the 'reset' button clicked, it creates a new blank game board.
	void reset(ActionEvent event) {
		mainClass.createBoardGrid(getHeight(), getWidth(), getMines(), true);
	}

	@FXML
	// startOver method, resets the same game board for another try when the 'StartOver' button clicked.
	void StartOver(ActionEvent event) {
		mainClass.startOver();
	}

	// setLifes method, gets the number of lifes and sets the hearts images in their correct way.
	void setLifes(int num) {
		int i = 0;
		ObservableList<Node> hearts = lifes.getChildren();
		for (Node tmp : hearts) {
			ImageView temp = (ImageView) tmp;
			if (i < num) {
				File file = new File("src/mines/redHeart.PNG");
				temp.setImage(new Image(file.toURI().toString()));
			} else {
				File file = new File("src/mines/greyHeart.PNG");
				temp.setImage(new Image(file.toURI().toString()));
			}
			i++;
		}
	}

	// setMainClass is a helping method to make the main class be recognizable by the controller, so it can use it's methods.
	public void setMainClass(MinesFX mainClass) {
		this.mainClass = mainClass;
	}

	// getWidth method, returns the widthVal field.
	public int getWidth() {
		return Integer.parseInt(widthVal.getText());
	}

	// getHeight method, returns the heightVal field.
	public int getHeight() {
		int temp = Integer.parseInt(heightVal.getText());
		return temp;
	}

	// getMines method, returns the minesVal field.
	public int getMines() {
		return Integer.parseInt(minesVal.getText());
	}

	// setNewMinesCounting changes the number in the label minesCounting.
	public void setNewMinesCounting(int count, int mines) {
		minesC.setText("mines : " + count + " / " + mines);
	}
}