import javax.swing.JLabel;

public class Tile extends JLabel {

	private static final long serialVersionUID = 1L;

	//the lines on the tile
	private Line[] lines;
	
	//name of the tile. used and updated depending
	//on where the tile is located, used to update
	//locations
	private String name;
	
	//used to determine the array size
	private int numLines;
	
	//used to add lines to the array in
	//proper spot
	private int counter;
	
	//rotation as read in from ReadData.java
	private int rotation;

	//constructor
	public Tile(String name, int numLines, int rotation) {
		this.name = name;
		this.setNumLines(numLines);
		this.lines = new Line[numLines];
		this.counter = 0;
	}

	//adds the line to the array, at the next spot in line
	public void addLine(Line line) {
		lines[counter] = line;
		counter++;
	}

	//get the lines, used by other classes
	public Line[] getLines() {
		return lines;
	}

	//update the name, used to update location mostly
	public void setName(String name) {
		this.name = name;
	}

	//sets the location based on the name of the tile
	public void setBoundsBasedOnName(Tile t) {
		
		//each number represents a spot as
		//specified in Dr Buckner's instructions
		if (t.name.equals("0")) {
			t.setBounds(28, 75, 100, 100);
		} else if (t.name.equals("1")) {
			t.setBounds(28, 185, 100, 100);
		} else if (t.name.equals("2")) {
			t.setBounds(28, 295, 100, 100);
		} else if (t.name.equals("3")) {
			t.setBounds(28, 405, 100, 100);
		} else if (t.name.equals("4")) {
			t.setBounds(28, 515, 100, 100);
		} else if (t.name.equals("5")) {
			t.setBounds(28, 625, 100, 100);
		} else if (t.name.equals("6")) {
			t.setBounds(28, 735, 100, 100);
		} else if (t.name.equals("7")) {
			t.setBounds(28, 845, 100, 100);

		} else if (t.name.equals("8")) {
			t.setBounds(768, 75, 100, 100);
		} else if (t.name.equals("9")) {
			t.setBounds(768, 185, 100, 100);
		} else if (t.name.equals("10")) {
			t.setBounds(768, 295, 100, 100);
		} else if (t.name.equals("11")) {
			t.setBounds(768, 405, 100, 100);
		} else if (t.name.equals("12")) {
			t.setBounds(768, 515, 100, 100);
		} else if (t.name.equals("13")) {
			t.setBounds(768, 625, 100, 100);
		} else if (t.name.equals("14")) {
			t.setBounds(768, 735, 100, 100);
		} else if (t.name.equals("15")) {
			t.setBounds(768, 845, 100, 100);

		} else if (t.name.equals("16")) {
			t.setBounds(248, 225, 100, 100);
		} else if (t.name.equals("20")) {
			t.setBounds(248, 325, 100, 100);
		} else if (t.name.equals("24")) {
			t.setBounds(248, 425, 100, 100);
		} else if (t.name.equals("28")) {
			t.setBounds(248, 525, 100, 100);
		}

		else if (t.name.equals("17")) {
			t.setBounds(348, 225, 100, 100);
		} else if (t.name.equals("21")) {
			t.setBounds(348, 325, 100, 100);
		} else if (t.name.equals("25")) {
			t.setBounds(348, 425, 100, 100);
		} else if (t.name.equals("29")) {
			t.setBounds(348, 525, 100, 100);
		}

		else if (t.name.equals("18")) {
			t.setBounds(448, 225, 100, 100);
		} else if (t.name.equals("22")) {
			t.setBounds(448, 325, 100, 100);
		} else if (t.name.equals("26")) {
			t.setBounds(448, 425, 100, 100);
		} else if (t.name.equals("30")) {
			t.setBounds(448, 525, 100, 100);
		}

		else if (t.name.equals("19")) {
			t.setBounds(548, 225, 100, 100);
		} else if (t.name.equals("23")) {
			t.setBounds(548, 325, 100, 100);
		} else if (t.name.equals("27")) {
			t.setBounds(548, 425, 100, 100);
		} else if (t.name.equals("31")) {
			t.setBounds(548, 525, 100, 100);
		} else {
		}
	}

	//getter if we need the number of lines
	public int getNumLines() {
		return numLines;
	}

	//probably not needed but just in case
	public void setNumLines(int numLines) {
		this.numLines = numLines;
	}

	//used to get rotation by other classes
	public int getRotation() {
		return rotation;
	}
	
	//gets name to update tile location, getName() 
	//already used by java differently
	public String getTheName() {
		return name;
	}

	//updates rotation if necesary
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
}
