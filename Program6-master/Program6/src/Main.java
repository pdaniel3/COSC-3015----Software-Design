
/**
 * @author COSC 3011 Team H
 * Date: Mar 25, 2016
 */
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

	public static void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException {
		// This is the play area
		
	    GameWindow game = new GameWindow("Group H aMaze");

		game.setSize(new Dimension(900, 1000));
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.getContentPane().setBackground(Color.cyan);

		game.setUp();
		game.setVisible(true);
		game.setResizable(false);
		

		try {
			// The 4 that installed on Linux here
			// May have to test on Windows boxes to see what is there.
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			// This is the "Java" or CrossPlatform version and the default
			// UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			// Linux only
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			// really old style Motif
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
		} catch (UnsupportedLookAndFeelException e) {
			// handle possible exception
		} catch (ClassNotFoundException e) {
			// handle possible exception
		} catch (InstantiationException e) {
			// handle possible exception
		} catch (IllegalAccessException e) {
			// handle possible exception
		}

	}

};
