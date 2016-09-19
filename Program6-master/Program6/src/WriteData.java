
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class WriteData {
	
	//byte array that we slowly build from files
	private byte[] bytes;

	public void createArray(ArrayList<Tile> tile_info_save, boolean flag, long time) throws IOException {
		
		// first four bytes should be hex values 0xca, 0xfe, 0xde, 0xed
		// position in byte array

		// hex values to add. type them as ints with 0x at the beginning
		int hex0 = 0xca;
		int hex1 = 0xfe;
		int hex2;
		int hex3;

		//if the game flagged as played...
		if(flag == true) {
		    hex2 = 0xde;
		    hex3 = 0xed;
		//if the game is not played    
		} else {
			hex2 = 0xbe;
		    hex3 = 0xef;
		}
		
		//continually concatenate the byte array until we finish writint
		byte[] byteArrayBuilder = {(byte)hex0,(byte)hex1,(byte)hex2,(byte)hex3};
		
		// add the number of tiles to the byte array
		int n = 16;
		byteArrayBuilder = concat(byteArrayBuilder, convertToByteArray(n));
		
		//adds the time played
		byteArrayBuilder = concat(byteArrayBuilder, convertToByteArray(time));
		
		ArrayList<Tile> tile_arr = tile_info_save;
		// for each tile after this point
		//writes name, rotation number of lines, line coordinates 
		for (int i = 0; i < tile_arr.size(); i++) {
			Tile tile = tile_arr.get(i);
			int name = Integer.parseInt(tile_arr.get(i).getTheName());
			byteArrayBuilder = concat(byteArrayBuilder, convertToByteArray(name));

			int rotation = tile.getRotation();
			byteArrayBuilder = concat(byteArrayBuilder, convertToByteArray(rotation));

			int numberLines = tile.getNumLines();
			byteArrayBuilder = concat(byteArrayBuilder, convertToByteArray(numberLines));

			Line lines[] = tile.getLines();

			for (Line l : lines) {
				float x0 = (float) l.getX0();
				byteArrayBuilder = concat(byteArrayBuilder, convertToByteArray(x0));

				float y0 = (float) l.getY0();
				byteArrayBuilder = concat(byteArrayBuilder, convertToByteArray(y0));

				float x1 = (float) l.getX1();
				byteArrayBuilder = concat(byteArrayBuilder, convertToByteArray(x1));

				float y1 = (float) l.getY1();
				byteArrayBuilder = concat(byteArrayBuilder, convertToByteArray(y1));
			}
		}
		bytes = byteArrayBuilder;
	}
	
	//getter in case we need the bytes elsewhere
	public byte[] getBytes() {
		return bytes;
	}

	//one of the functions Dr. Buckner gave us
	public static byte[] convertToByteArray(int value) {
		byte[] bytes = new byte[4];
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.putInt(value);
		return buffer.array();
	}
	
	//same as other function but works on floats
	public static byte[] convertToByteArray(float value) {
		byte[] bytes = new byte[4];
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.putFloat(value);
		return buffer.array();
	}
	
	//same as other function but works on longs
	public static byte[] convertToByteArray(long value) {
        byte[] bytes = new byte[8];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putLong(value);
        return buffer.array();
    }

	//concats the the byte array as we build it from function calls
	public byte[] concat(byte[] a, byte[] b) {
		int aLen = a.length;
		int bLen = b.length;
		byte[] c = new byte[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}
}
