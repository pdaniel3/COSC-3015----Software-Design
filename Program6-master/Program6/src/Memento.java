

public class Memento {
	//Initial "New" State
    private int rot1;
    private int xLoc1;
    private int yLoc1;
    
    //"Local" State for when the game resets, but the randomized
    //values need to stay the same
    private int rot2;
    private int xLoc2;
    private int yLoc2;
    
    public void newGame(int newRot, int newX, int newY) {
    	rot1 = newRot;
    	xLoc1 = newX;
    	yLoc1 = newY;
    }
    
    public void resetGame() {
    	setRot2(rot1);
    	setxLoc2(xLoc1);
    	setyLoc2(yLoc1);
    }

	public int getxLoc2() {
		return xLoc2;
	}

	public void setxLoc2(int xLoc2) {
		this.xLoc2 = xLoc2;
	}

	public int getRot2() {
		return rot2;
	}

	public void setRot2(int rot2) {
		this.rot2 = rot2;
	}

	public int getyLoc2() {
		return yLoc2;
	}

	public void setyLoc2(int yLoc2) {
		this.yLoc2 = yLoc2;
	}
}
