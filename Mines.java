package mines;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

// Mines class representing a board of the game minesweeper.
// The class destiny is to manage the minesweeper game itself without the graphic user interface.
public class Mines {
	// helping inner classes:
	// the class represents a square on the board of minesweeper game.
	public class Place {
		// class properties:
		private boolean mine = false; // true if it is a mine.
		private boolean opened = false; // true if it opened.
		private boolean flag = false; // true if it has a flag on it.
		private int numOfMinesAround = 0; // keeps the number of mines in the square around this place.

		// setMine method, sets true in the mine field. it returns the previous value of mine field.
		public boolean setMine() {
			boolean temp = mine;
			mine = true;
			return temp;
		}
	}

	// the class represents a pair of integer which represents indexes of a place on a game board.
	public class IndexesPair {
		// class properties:
		private int x;
		private int y;

		// constructor.
		public IndexesPair(int x, int y) {
			this.x = x;
			this.y = y;
		}

		// getNeighbours method creates a set of pairs representing the indexes i,j of the legal neighbors of the place x,y.
		public Set<IndexesPair> getNeighbors(int height, int width) {
			Set<IndexesPair> neighbors = new HashSet<IndexesPair>();
			for (int i = x - 1; i < x + 2; i++)
				for (int j = y - 1; j < y + 2; j++)
					if (isLegalPlace(i, j, height, width) && !(i == x && j == y))
						neighbors.add(new IndexesPair(i, j));
			return neighbors;
		}

		// isLegalPlace method gets indexes i,j and returns true if the indexes are legal on the board, else it returns false.
		public boolean isLegalPlace(int i, int j, int height, int width) {
			return (i >= 0 && i < height && j >= 0 && j < width);
		}
	}

	// class (Mine) properties:
	private int height;
	private int width;
	private int numMines = 0; // the number of mines on the board.
	private Place[][] board; // the board itself is a matrix of type Place.
	private int numOfOpenedPlaces = 0; // counter for the amount of places already opened. use for isDone method.
	private boolean showAll = false;

	// constructor.
	// sets the height, width, numMines, allocates the board, and random the places for the mines.
	public Mines(int height, int width, int numMines) {
		this.height = height;
		this.width = width;
		// allocates the board:
		board = new Place[height][width];
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				board[i][j] = new Place();
		// randomize the places of the mines:
		int i, j; // for the indexes of the mines.
		Random rand = new Random();
		for (int k = 0; k < numMines; k++) // sets numMines mines on the board on randomized places.
			do {
				i = rand.nextInt(height);
				j = rand.nextInt(width);
			} while (!addMine(i, j)); // randomize new indexes until the place is not a mine already.
	}

	// the method sets a mine on the board in indexes (i,j).
	// if already had mine there it returns false, else it returns true.
	public boolean addMine(int i, int j) {
		numMines++; // increases the number of mines on the board.
		// increase the number of mines around in each of it's neighbors:
		if (!board[i][j].setMine()) { // if it's a new mine.
			Set<IndexesPair> neighbors = new IndexesPair(i, j).getNeighbors(height, width);
			for (IndexesPair pair : neighbors)
				board[pair.x][pair.y].numOfMinesAround++;
		} else
			return false; // returns false if already had mine there.
		return true; // returns true if it's a new mine there.
	}

	// this method indicates the place on i,j that it opened.
	// if it is a mine, it returns false and doesn't open.
	// if it has a flag, it doesn't open.
	// if there is no mine in the square around it, it opens recursively all the neighbors of it.
	public boolean open(int i, int j) {
		if (board[i][j].mine) // if open a mine.
			return false;
		if (board[i][j].flag) // if has a flag on it.
			return true;
		if (!board[i][j].opened)// if hasn't opened yet.
			numOfOpenedPlaces++;
		else
			return true;
		board[i][j].opened = true;
		if (board[i][j].numOfMinesAround != 0) // if there are mines around it open only itself.
			return true;
		// if it doesn't has mines nearby, open all its neighbors:
		Set<IndexesPair> neighbors = new IndexesPair(i, j).getNeighbors(height, width);
		for (IndexesPair pair : neighbors)
			open(pair.x, pair.y);
		return true;
	}

	// the method sets a flag on (x,y) place or remove the flag if already set.
	public void toggleFlag(int x, int y) {
		board[x][y].flag = (!board[x][y].flag);
	}

	// isDone method returns true if and only if all the places which are not mines got opened already.
	public boolean isDone() {
		return (width * height - numMines == numOfOpenedPlaces);
	}

	// get method gets indexes of a place on the board and returns the right sign of it.
	// if not opened: "F" if has flag, "." if not.
	// if opened: "X" is mine, else number of mines around or " " if zero mines around.
	public String get(int i, int j) {
		if (!showAll && !board[i][j].opened)
			return (board[i][j].flag) ? "F" : ".";
		if (board[i][j].mine)
			return "X";
		return (board[i][j].numOfMinesAround == 0) ? " " : String.valueOf(board[i][j].numOfMinesAround);
	}

	// setShowAll method, set the value of showAll field.
	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	// startOver method runs over the board and closes again all the opened places, and removes all flags.
	public void startOver() {
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++) {
				board[i][j].flag = false;
				board[i][j].opened = false;
			}
	}

	// toString method creates and returns a string of the board.
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++)
				str.append(get(i, j));
			str.append("\n");
		}
		return str.toString();
	}
}