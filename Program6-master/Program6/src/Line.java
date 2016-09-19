

public class Line {
    
	private int x0, x1, y0, y1;
	private int rotationCounter = 0;
	
	public Line(int x0, int y0, int x1, int y1) {
    	this.x0 = x0;
    	this.x1 = x1;
    	this.y0 = y0;
    	this.y1 = y1;
    }
	
	public int getX0() { return x0;}
	
	public int getY0() { return y0;}
	
	public int getX1() { return x1;}
	
	public int getY1() { return y1;}
	
	public int getRotation(){return rotationCounter;}
	
	public void rotateOnce() {
		//for a 90 degree rotation around origin of (0,0). 
		//(x,y) -> (y,-x). To rotate around origin of (50,50).
		//(x,y) = (-y + 100, x)
		int x00 = -y0 + 100;
		int y00 = x0;
		int x11 = -y1 + 100;
		int y11 = x1;
		
		x0 = x00;
		y0 = y00;
		x1 = x11;
		y1 = y11;
	    rotationCounter++;
	    
	    if(rotationCounter == 4) {
	    	rotationCounter = 0;
	    }
		
	}
	
	public void rotate(int n){
		for(int i = 0; i < n; i++) {
			rotateOnce();
		}
	}
	
	
}
