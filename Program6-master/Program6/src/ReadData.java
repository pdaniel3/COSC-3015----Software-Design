
import java.awt.Container;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class ReadData {

	private ArrayList<Tile> tile_arr = new ArrayList<Tile>();
	private byte[] data;
	private byte[] magicValueBytes = new byte[4];
	long time;
	
	public void createArray(File f, Container pane) throws IOException, ClassNotFoundException, URISyntaxException {
		try {

			// set the file equal to f
			File file = f;

			// read all the bytes
			data = Files.readAllBytes(file.toPath());

			// save the "magic value" to compare to later
			for (int i = 0; i < 4; i++) {
				magicValueBytes[i] = data[i];
			}

			if ((magicValueBytes[0] != (byte) 0xca) || magicValueBytes[1] != (byte) 0xfe) {
				JOptionPane.showMessageDialog(pane, "Invalid file type");
			} else {
				// gotta wrap the data
				ByteBuffer buffer = ByteBuffer.wrap(data);

				// the first 5 values of the file

				// magic value not used, but we read it
				// in to continue
				@SuppressWarnings("unused")
				int magicValue = buffer.getInt();

				// total tiles
				int totalTiles = buffer.getInt();

				// time played
				time = buffer.getLong();

				// repeat this code for every tile
				for (int i = 0; i < 16; i++) {
					int currentTileNumber = buffer.getInt();
					int rotation = buffer.getInt();
					int totalLinesToDraw = buffer.getInt();

					// create the new tile
					Tile tile = new Tile(currentTileNumber + "", totalLinesToDraw, rotation);

					// add the new tile to the array
					tile_arr.add(tile);

					// read in and save all the lines on the tile
					for (int j = 0; j < totalLinesToDraw; j++) {
						// we round because tile and drawLine(0 use ints
						int x1 = Math.round(buffer.getFloat());
						int y1 = Math.round(buffer.getFloat());
						int x2 = Math.round(buffer.getFloat());
						int y2 = Math.round(buffer.getFloat());
						tile.addLine(new Line(x1, y1, x2, y2));
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found" + e);
			JOptionPane.showMessageDialog(pane, "The File Was not Found");
		} catch (IOException ioe) {
			System.out.println("Exception while reading file " + ioe);
			JOptionPane.showMessageDialog(pane, "Exception while reading file ");
		}

	}

	// gets the generated array of tiles
	public ArrayList<Tile> getArray() {
		return tile_arr;
	}

	// gets the tile at i
	public Tile getTile(int i) {
		return tile_arr.get(i);
	}

	// gets the time played
	public long getTime() {
		return time;
	}

	// if it is a new or already played game
	public boolean isNew() {
		boolean isNew = false;

		// check if byte pattern matches new game pattern
		if (magicValueBytes[2] == (byte) 0xbe && magicValueBytes[3] == (byte) 0xef) {
			isNew = true;
		}

		return isNew;
	}

	// converts integer to byte array
	public static byte[] convertToByteArray(int value) {
		byte[] bytes = new byte[4];
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.putInt(value);
		return buffer.array();
	}
	
	public static byte[] convertToByteArray(long value) {
        byte[] bytes = new byte[8];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putLong(value);
        return buffer.array();
    }
	
	
}
