
/**
  * @author Group Team H 
  * Date: Mar 25, 2016
  */

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameWindow extends JFrame implements ActionListener, MouseMotionListener, MouseListener {
	public static final long serialVersionUID = 1;

	// our buttons
	private JButton exitButton, resetButton, fileButton;

	private FileChooser mychooser;

	private int counter = 0;
	
	// keep track of initial positions for reset()
	private String[] original_positions;

	// our panel to draw buttons on, place tiles, etc.
	private Container pane;

	// reusable reader that goes through our mze files
	// for every one loaded in
	private ReadData reader;

	// our tiles
	private Tile tile00, tile01, tile02, tile03, tile04, tile05, tile06, tile07, tile08, tile09, tile010, tile011,
			tile012, tile013, tile014, tile015, currentTile;

	private JLabel timer;

	// the current tile being clicked or moved
	private static ArrayList<Tile> tiles = new ArrayList<Tile>();

	// used in calculations with mouse
	private int newX, newY, tileInitialX, tileInitialY;

	// boolean values that are useful in some of our game logic
	private boolean one, two, three, four, five, six, seven, eight, nine, ten, eleven, twelve, thirteen, fourteen,
			fifteen, sixteen, tiles_added;
			// private boolean reset, been_played;

	// if the user has made a move, used for save dialog
	boolean made_a_move;

	// this game, used to pass to other classes
	GameWindow game;

	// all of our variables to keep the timer going
	private Timer thread;
	private long initialtime, time;

	// constructor, sets up pane, and layout
	public GameWindow(String s) throws ClassNotFoundException, URISyntaxException {
		super(s);

		// save the game object here for use by other classes
		game = this;

		// null layout because we weren't smart enough to use the gridbag
		setLayout(null);

		pane = getContentPane();
		reader = new ReadData();
		try {
			// code we came up with to load the default.mze in
			Class<?> cls = Class.forName("GameWindow");
			ClassLoader cLoader = cls.getClassLoader();
			URL url = cLoader.getResource("default1.mze");
			File file = new File(url.toURI());
		    newGame(file);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(pane, "error reading in file, load another");
		}

		tiles = reader.getArray();

		// these boolean values help with checking
		// if a space is occupied
		one = false;
		two = false;
		three = false;
		four = false;
		five = false;
		six = false;
		seven = false;
		eight = false;
		nine = false;
		ten = false;
		eleven = false;
		twelve = false;
		thirteen = false;
		fourteen = false;
		fifteen = false;
		sixteen = false;
		made_a_move = false;

	}

	/**
	 * Establishes the initial board
	 */
	public void setUp() {

		// remove all so there are no duplicate tiles on screen
		pane.removeAll();

		// much of our game is set up in paint
		repaint();
		addMouseListener(this);
	}

	// draws the board with rectangles
	public void paint(Graphics g) {

		super.paint(g);

		// if the tiles have not been added, add them. Otherwise...
		if (!tiles_added) {
			addTiles();
			addButtons();
			tiles_added = true;
		}

		// center board lines
		g.setColor(Color.WHITE);
		for (int i = 0; i < 4; i++) {
			g.drawRect(250, 250 + 100 * i, 100, 100);
			g.drawRect(350, 250 + 100 * i, 100, 100);
			g.drawRect(450, 250 + 100 * i, 100, 100);
			g.drawRect(550, 250 + 100 * i, 100, 100);
		}

		// right and left edge lines
		g.setColor(Color.BLACK);
		for (int i = 0; i < 8; i++) {
			g.drawRect(30, 100 + 110 * i, 100, 100);
			g.drawRect(770, 100 + 110 * i, 100, 100);
		}

		// draws the lines in relation to where the tiles are
		for (int i = 0; i < tiles.size(); i++) {
			Tile current_tile_info = tiles.get(i);
			Line[] current_tile_lines = current_tile_info.getLines();

			// offset needed because windows is terrible
			int x_offset = tiles.get(i).getX() + 2;
			int y_offset = tiles.get(i).getY() + 25;

			// draw all the lines for each tiles
			for (Line l : current_tile_lines) {
				g.drawLine(l.getX0() + x_offset, l.getY0() + y_offset, l.getX1() + x_offset, l.getY1() + y_offset);

			}
		}
		//call this dummy function to prevent a bug
		d();
	}
	
	//this dummy function helps with a bug we had where reset was messing up
	public void d() {
		if(counter == 0) {
			resetButton.doClick();
			counter++;
		}
	}

	// used by other classes to get the tiles
	public ArrayList<Tile> getTileArr() {
		return tiles;
	}

	// adds the buttons to the game
	public void addButtons() {

		// all the code we need to add the exit button
		exitButton = new JButton("exit");
		exitButton.setBounds(210, 10, 80, 60);
		pane.add(exitButton);
		exitButton.addActionListener(new ActionListener() {

			// this is really long because we prompt the user if
			// they want to save
			public void actionPerformed(ActionEvent e) {
				if (made_a_move) {
					thread.stop();
					int n = JOptionPane.showConfirmDialog(game, "Save before you quit?", "WARNING",
							JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.YES_OPTION) {
						JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
						int returnVal = chooser.showSaveDialog(GameWindow.this);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File file = chooser.getSelectedFile();

							// checks for overwrites
							if (file.exists()) {
								int n1 = JOptionPane.showConfirmDialog(pane, "This file already exists. "
										+ "Do you want to replace " + "the existing file?");
								if (n1 != 0) {
									// saves the file
									WriteData writer = new WriteData();
									try {
										writer.createArray(getTileArr(), true, time);
									} catch (IOException e1) {
										e1.printStackTrace();
									}

									byte[] bytes = writer.getBytes();

									FileOutputStream fos = null;
									try {
										fos = new FileOutputStream(file);
									} catch (FileNotFoundException e2) {
										e2.printStackTrace();
									}
									try {
										fos.write(bytes);
										fos.flush();
										fos.close();
										made_a_move = false;
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								}
							} else {
								WriteData writer = new WriteData();
								try {
									writer.createArray(getTileArr(), made_a_move, time);
								} catch (IOException e1) {
									e1.printStackTrace();
								}

								byte[] bytes = writer.getBytes();

								FileOutputStream fos = null;
								try {
									fos = new FileOutputStream(file);
								} catch (FileNotFoundException e2) {
									e2.printStackTrace();
								}
								try {
									fos.write(bytes);
									fos.flush();
									fos.close();
									made_a_move = false;
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}

					}
				}
				System.exit(0);
			}
		});

		// mbutton, no functionality yet
		resetButton = new JButton("reset");
		pane.add(resetButton);
		resetButton.setBounds(120, 10, 80, 60);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
				repaint();
			}
		});

		// file button
		fileButton = new JButton("file");
		fileButton.setBounds(30, 10, 80, 60);
		pane.add(fileButton);
		fileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// stop the timer
				thread.stop();
				// if they made a move, ask if they want to save
				if (made_a_move) {
					int n = JOptionPane.showConfirmDialog(game, "Save before you load?", "WARNING",
							JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.YES_OPTION) {
						JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
						int returnVal = chooser.showSaveDialog(GameWindow.this);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File file = chooser.getSelectedFile();
							// if the file exists
							if (file.exists()) {
								int n1 = JOptionPane.showConfirmDialog(pane, "This file already exists. "
										+ "Do you want to replace " + "the existing file?");
								if (n1 != 0) {
									WriteData writer = new WriteData();
									try {
										writer.createArray(getTileArr(), made_a_move, time);
									} catch (IOException e1) {
										e1.printStackTrace();
									}

									byte[] bytes = writer.getBytes();

									FileOutputStream fos = null;
									try {
										fos = new FileOutputStream(file);
									} catch (FileNotFoundException e2) {
										e2.printStackTrace();
									}
									try {
										fos.write(bytes);
										fos.flush();
										fos.close();
										made_a_move = false;
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								}
							} else {
								WriteData writer = new WriteData();
								try {
									writer.createArray(getTileArr(), made_a_move, time);
								} catch (IOException e1) {
									e1.printStackTrace();
								}

								byte[] bytes = writer.getBytes();

								FileOutputStream fos = null;
								try {
									fos = new FileOutputStream(file);
								} catch (FileNotFoundException e2) {
									e2.printStackTrace();
								}
								try {
									fos.write(bytes);
									fos.flush();
									fos.close();
									made_a_move = false;
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}

					}
				}
				mychooser = new FileChooser(game);
				mychooser.createAndShowGUI();
				thread.start();
			}
		});

		// our running timer as specified by the assignment
		timer = new JLabel("Elapsed Time: " + time);
		pane.add(timer);
		timer.setForeground(Color.BLACK);
		timer.setBounds(300, 10, 400, 60);

		// the initial time to compare to
		initialtime = System.nanoTime() + time * 1000000000;

		//how often should our timer update?
		int delay = 1000; // milliseconds
		
		//what should happen after every delay?
		ActionListener updateTimer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//time elapsed - initial time, casting for precision and coverting to 
				//proper units
				time = (long) ((double) (System.nanoTime() - initialtime) / 1000000000);
				timer.setText("Elapsed Time: " + time);
			}
		};
		thread = new Timer(delay, updateTimer);
		thread.start();
	}

	// in case other classes need to
	// start or stop our timer
	public void startThread() {
		thread.start();
	}

	public void stopThread() {
		thread.stop();
	}

	public boolean getIfPlayed() {
		return made_a_move;
	}

	public void newGame(File f) throws ClassNotFoundException, IOException, URISyntaxException {

		// setting this all to null
		// so there are no duplicates
		// from the old game

		// suppress warning is so the warning isn't shown,
		// it bugs the hell out of me
		for (@SuppressWarnings("unused")
		Tile t : tiles) {
			t = null;
		}

		tiles = null;
		pane.removeAll();

		reader = new ReadData();
		reader.createArray(f, pane);
		tiles = reader.getArray();
		
		if (reader.isNew()) {
			made_a_move = false;
		}

		addButtons();
		if (tiles.size() == 16) {
			addTiles();
			repaint();
		}
	}

	// add all of our tiles to the pane, painfully long
	public void addTiles() {

		// start with a Tile, put an image on top, add
		// mouse listeners then add it to the pane and set
		// its location

		// I left everything here except setBounds

		tile015 = tiles.get(15);
		tile015.addMouseMotionListener(this);
		tile015.addMouseListener(this);
		pane.add(tile015);

		tile014 = tiles.get(14);
		tile014.addMouseMotionListener(this);
		tile014.addMouseListener(this);
		pane.add(tile014);

		tile013 = tiles.get(13);
		tile013.addMouseMotionListener(this);
		tile013.addMouseListener(this);
		pane.add(tile013);

		tile012 = tiles.get(12);
		tile012.addMouseMotionListener(this);
		tile012.addMouseListener(this);
		pane.add(tile012);

		tile011 = tiles.get(11);
		tile011.addMouseMotionListener(this);
		tile011.addMouseListener(this);
		pane.add(tile011);

		tile010 = tiles.get(10);
		tile010.addMouseMotionListener(this);
		tile010.addMouseListener(this);
		pane.add(tile010);

		tile09 = tiles.get(9);
		tile09.addMouseMotionListener(this);
		tile09.addMouseListener(this);
		pane.add(tile09);

		tile08 = tiles.get(8);
		tile08.addMouseMotionListener(this);
		tile08.addMouseListener(this);
		pane.add(tile08);

		tile07 = tiles.get(7);
		tile07.addMouseMotionListener(this);
		tile07.addMouseListener(this);
		pane.add(tile07);

		tile06 = tiles.get(6);
		tile06.addMouseMotionListener(this);
		tile06.addMouseListener(this);
		pane.add(tile06);

		tile05 = tiles.get(5);
		tile05.addMouseMotionListener(this);
		tile05.addMouseListener(this);
		pane.add(tile05);

		tile04 = tiles.get(4);
		tile04.addMouseMotionListener(this);
		tile04.addMouseListener(this);
		pane.add(tile04);

		tile03 = tiles.get(3);
		tile03.addMouseMotionListener(this);
		tile03.addMouseListener(this);
		pane.add(tile03);

		tile02 = tiles.get(2);
		tile02.addMouseMotionListener(this);
		tile02.addMouseListener(this);
		pane.add(tile02);

		tile01 = tiles.get(1);
		tile01.addMouseMotionListener(this);
		tile01.addMouseListener(this);
		pane.add(tile01);

		tile00 = tiles.get(0);
		tile00.addMouseMotionListener(this);
		tile00.addMouseListener(this);
		pane.add(tile00);

		// if a niot a new game, put tiles where the file
		// specified
		if (!(reader.isNew())) {
			time = reader.getTime();
			for (Tile t : tiles) {
				t.setBackground(Color.WHITE);
				t.setOpaque(true);
				Line[] lines = t.getLines();
				for (Line l : lines) {
					l.rotate(t.getRotation());
				}
				t.setBoundsBasedOnName(t);
			}

			original_positions = new String[16];
			for (int i = 0; i < 16; i++) {
				original_positions[i] = tiles.get(i).getTheName();
			}

		} else {
			time = 0;
			randomize();
		}
		repaint();
		
	}

	// Creates array of our tiles in order, then randomly shuffles them
	public void randomize() {

		Collections.shuffle(tiles);

		// Now they're assigned locations based on their random position in the
		// array
		original_positions = new String[16];

		for (int i = 0; i < tiles.size(); i++) {
			tiles.get(i).setName(i + "");
			tiles.get(i).setBoundsBasedOnName(tiles.get(i));
		}
		
		for (int i = 0; i < tiles.size(); i++) {
			original_positions[i] = tiles.get(i).getTheName();
		}

		// set the background white, and opaque so we
		// can see the lines
		for (Tile t : tiles) {
			t.setBackground(Color.WHITE);
			t.setOpaque(true);
		}

		// set to a temp so we can shuffle without
		// messing up the order
		ArrayList<Tile> temp = tiles;
		Collections.shuffle(temp);
		for (int i = 0; i < 12; i++) {

			// randomly rotate the temp tiles 0-11
			// (which was randomly shuffled) which
			// is 75% of the tiles
			Random rand = new Random();
			int n = 0;
			// randomInts.add(n);
			n = rand.nextInt(3);
			if (n == 0) {
				temp.get(i).setRotation(1);
				for (Line l : temp.get(i).getLines()) {
					l.rotateOnce();
				}
			} else if (n == 1) {
				temp.get(i).setRotation(2);
				for (Line l : temp.get(i).getLines()) {
					l.rotateOnce();
					l.rotateOnce();
				}
			} else if (n == 2) {
				temp.get(i).setRotation(3);
				for (Line l : temp.get(i).getLines()) {
					l.rotateOnce();
					l.rotateOnce();
					l.rotateOnce();
				}
			}
		}
		// the rest don't get rotated
		for (int i = 12; i < 16; i++) {
			temp.get(i).setRotation(0);
		}
        repaint();
	}

	// used by reset button to reset tiles to original
	// position
	public void reset() {
		for (int i = 0; i < 16; i++) {
			tiles.get(i).setName(original_positions[i]);
			tiles.get(i).setBoundsBasedOnName(tiles.get(i));
		}

		// if reset, they didn't technically "make a move"
		// as specified by the assignment
		made_a_move = false;

		// reset the time as specified by the assignment
		resetTime();

		// rotate lines back to original rotation
		for (int i = 0; i < 16; i++) {
			for (Line l : tiles.get(i).getLines()) {
				if (l.getRotation() == 1) {
					l.rotateOnce();
					l.rotateOnce();
					l.rotateOnce();
				} else if (l.getRotation() == 2) {
					l.rotateOnce();
					l.rotateOnce();
				} else if (l.getRotation() == 3) {
					l.rotateOnce();
				} else {

				}
			}
		}

	}

	public void resetTime() {
        time = initialtime;
	}

	/*
	 * These are our mouse listeners, we used drag and drop for the tiles
	 */

	/*
	 * this is where the mouse is listened for. the mouse is pressed and the
	 * location is saved to calculate where to reset it to if the move was
	 * invalid
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		// make sure they are not clicking the window
		if (!(arg0.getSource() instanceof GameWindow)) {
			currentTile = (Tile) arg0.getSource();
			tileInitialX = currentTile.getX();
			tileInitialY = currentTile.getY();
		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		ArrayList<Tile> tileInfo = tiles;
		if (arg0.getSource() instanceof Tile) {
			if (SwingUtilities.isRightMouseButton(arg0)) {
				Tile clicked_tile = (Tile) arg0.getSource();
				// they rotated the tile and technically made a move
				made_a_move = true;

				int index = 0;
				for (int i = 0; i < 16; i++) {
					if (clicked_tile == tileInfo.get(i)) {
						index = i;
					}
				}

				Tile clicked_info = tiles.get(index);

				// rotate every line once
				for (Line l : clicked_info.getLines()) {
					l.rotateOnce();
				}
				// repaint so the gui updates
				repaint();
			} else {
				// must not of been a right click see if a valid
				// move
				tryMove(arg0);
			}
		}
	}

	// gets the time played
	public long getTime() {
		return time;
	}

	public void tryMove(MouseEvent arg) {
		/*
		 * This nasty nested if statement checks to see if the mouse was
		 * released in a square then sets the tile in the square starting at the
		 * top left corner. For some reason, there is an odd offset, the image
		 * is drawn 2 pixels to the right and 25 pixels below the point where we
		 * set the bounds to, so we have compensated for that. The for loops
		 * check to see if the space is being occupied (IE another tile has that
		 * coordinate). Apologies for the sloppy nested if's, not sure of a
		 * better way to do this, our initial design led us to using this
		 * monster unfortunately
		 */

		// we add the offset because getX and getY starts at 0
		if (arg.getSource() instanceof Tile) {
			newX = arg.getLocationOnScreen().x;
			newY = arg.getLocationOnScreen().y;

			// logic for column one
			if (newX >= 250 && newX < 350) {
				// column 1 row 1
				if (newY >= 250 && newY < 350) {
					// check against every other tile
					for (Tile i : tiles) {
						// if there exists a tile with an x pos and y pos
						// equal to where we want to go, the space must
						// be occupied
						if (i.getX() == 248 && i.getY() == 225) {
							one = true;
							break;
						} else {
							one = false;
						}
					}
					// the space must not have been occupied
					if (one == false) {
						// set the tile to new location, update
						// name, make the space occupied, user
						// must have made a move
						currentTile.setBounds(248, 225, 100, 100);
						currentTile.setName("16");
						made_a_move = true;
						one = true;
					}
					// column 1 row 2...
				} else if (newY >= 350 && newY < 450) {
					for (Tile i : tiles) {
						if (i.getX() == 248 && i.getY() == 325) {
							two = true;
							break;
						} else {
							two = false;
						}
					}
					if (two == false) {
						currentTile.setBounds(248, 325, 100, 100);
						made_a_move = true;
						currentTile.setName("20");
						two = true;
					}
				} else if (newY >= 450 && newY < 550) {
					for (Tile i : tiles) {
						if (i.getX() == 248 && i.getY() == 425) {
							three = true;
							break;
						} else {
							three = false;
						}
					}
					if (three == false) {
						currentTile.setBounds(248, 425, 100, 100);
						made_a_move = true;
						currentTile.setName("24");
						three = true;
					}
				} else if (newY >= 550 && newY < 650) {
					for (Tile i : tiles) {
						if (i.getX() == 248 && i.getY() == 525) {
							four = true;
							break;
						} else {
							four = false;
						}
					}
					if (four == false) {
						currentTile.setBounds(248, 525, 100, 100);
						made_a_move = true;
						currentTile.setName("28");
						four = true;
					}
				} else {
					currentTile.setBounds(tileInitialX, tileInitialY, 100, 100);
				}

				// row two, this goes on for a while, I'm sorry
			} else if (newX >= 350 && newX < 450) {
				if (newY >= 250 && newY < 350) {
					for (Tile i : tiles) {
						if (i.getX() == 348 && i.getY() == 225) {
							five = true;
							break;
						} else {
							five = false;
						}
					}
					if (five == false) {
						currentTile.setBounds(348, 225, 100, 100);
						made_a_move = true;
						currentTile.setName("17");
						five = true;
					}
				} else if (newY >= 350 && newY < 450) {
					for (Tile i : tiles) {
						if (i.getX() == 348 && i.getY() == 325) {
							six = true;
							break;
						} else {
							six = false;
						}
					}
					if (six == false) {
						currentTile.setBounds(348, 325, 100, 100);
						made_a_move = true;
						currentTile.setName("21");
						six = true;
					}
				} else if (newY >= 450 && newY < 550) {
					for (Tile i : tiles) {
						if (i.getX() == 348 && i.getY() == 425) {
							seven = true;
							break;
						} else {
							seven = false;
						}
					}
					if (seven == false) {
						currentTile.setBounds(348, 425, 100, 100);
						made_a_move = true;
						currentTile.setName("25");
						seven = true;
					}
				} else if (newY >= 550 && newY < 650) {
					for (Tile i : tiles) {
						if (i.getX() == 348 && i.getY() == 525) {
							eight = true;
							break;
						} else {
							eight = false;
						}
					}
					if (eight == false) {
						currentTile.setBounds(348, 525, 100, 100);
						made_a_move = true;
						currentTile.setName("29");
						eight = true;
					} else {
						currentTile.setBounds(tileInitialX, tileInitialY, 100, 100);
					}
				}
			} else if (newX >= 450 && newX < 550) {
				if (newY >= 250 && newY < 350) {
					for (Tile i : tiles) {
						if (i.getX() == 448 && i.getY() == 225) {
							nine = true;
							break;
						} else {
							nine = false;
						}
					}
					if (nine == false) {
						currentTile.setBounds(448, 225, 100, 100);
						made_a_move = true;
						currentTile.setName("18");
						nine = true;
					}
				} else if (newY >= 350 && newY < 450) {
					for (Tile i : tiles) {
						if (i.getX() == 448 && i.getY() == 325) {
							ten = true;
							break;
						} else {
							ten = false;
						}
					}
					if (ten == false) {
						currentTile.setBounds(448, 325, 100, 100);
						made_a_move = true;
						currentTile.setName("22");
						ten = true;
					}
				} else if (newY >= 450 && newY < 550) {
					for (Tile i : tiles) {
						if (i.getX() == 448 && i.getY() == 425) {
							eleven = true;
							break;
						} else {
							eleven = false;
						}
					}
					if (eleven == false) {
						currentTile.setBounds(448, 425, 100, 100);
						made_a_move = true;
						currentTile.setName("26");
						eleven = true;
					}
				} else if (newY >= 550 && newY < 650) {
					for (Tile i : tiles) {
						if (i.getX() == 448 && i.getY() == 525) {
							twelve = true;
							break;
						} else {
							twelve = false;
						}
					}
					if (twelve == false) {
						currentTile.setBounds(448, 525, 100, 100);
						made_a_move = true;
						currentTile.setName("28");
						twelve = true;
					}
				} else {
					currentTile.setBounds(tileInitialX, tileInitialY, 100, 100);
				}
			} else if (newX >= 550 && newX < 650) {
				if (newY >= 250 && newY < 350) {
					for (Tile i : tiles) {
						if (i.getX() == 548 && i.getY() == 225) {
							thirteen = true;
							break;
						} else {
							thirteen = false;
						}
					}
					if (thirteen == false) {
						currentTile.setBounds(548, 225, 100, 100);
						made_a_move = true;
						currentTile.setName("19");
						thirteen = true;
					}
				} else if (newY >= 350 && newY < 450) {
					for (Tile i : tiles) {
						if (i.getX() == 548 && i.getY() == 325) {
							fourteen = true;
							break;
						} else {
							fourteen = false;
						}
					}
					if (fourteen == false) {
						currentTile.setBounds(548, 325, 100, 100);
						made_a_move = true;
						currentTile.setName("23");
						fourteen = true;
					}
				} else if (newY >= 450 && newY < 550) {
					for (Tile i : tiles) {
						if (i.getX() == 548 && i.getY() == 425) {
							fifteen = true;
							break;
						} else {
							fifteen = false;
						}
					}
					if (fifteen == false) {
						currentTile.setBounds(548, 425, 100, 100);
						made_a_move = true;
						currentTile.setName("27");
						fifteen = true;
					}
				} else if (newY >= 550 && newY < 650) {
					for (Tile i : tiles) {
						if (i.getX() == 548 && i.getY() == 525) {
							sixteen = true;
							break;
						} else {
							sixteen = false;
						}
					}
					if (sixteen == false) {
						currentTile.setBounds(548, 525, 100, 100);
						made_a_move = true;
						currentTile.setName("31");
						sixteen = true;
					} else {
						currentTile.setBounds(tileInitialX, tileInitialY, 100, 100);
					}
				}
			} else {
				currentTile.setBounds(tileInitialX, tileInitialY, 100, 100);
			}

			// repaint so our nice little lines are still there
			repaint();

			boolean spaceOccupied = false;

			// same type of logic for board, but this
			// handles the edges
			if (newX >= 30 && newX < 130) {
				if (newY >= 100 && newY < 200) {
					for (Tile i : tiles) {

						if (i.getX() == 28 && i.getY() == 75) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(28, 75, 100, 100);
						made_a_move = true;
						currentTile.setName("0");
					}
				} else if (newY >= 210 && newY < 310) {
					for (Tile i : tiles) {

						if (i.getX() == 28 && i.getY() == 185) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(28, 185, 100, 100);
						made_a_move = true;
						currentTile.setName("1");
					}
				} else if (newY >= 320 && newY < 420) {
					for (Tile i : tiles) {

						if (i.getX() == 28 && i.getY() == 295) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(28, 295, 100, 100);
						made_a_move = true;
						currentTile.setName("2");
					}
				} else if (newY >= 430 && newY < 530) {
					for (Tile i : tiles) {

						if (i.getX() == 28 && i.getY() == 405) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(28, 405, 100, 100);
						made_a_move = true;
						currentTile.setName("3");
					}
				} else if (newY >= 540 && newY < 640) {
					for (Tile i : tiles) {

						if (i.getX() == 28 && i.getY() == 515) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(28, 515, 100, 100);
						made_a_move = true;
						currentTile.setName("4");
					}
				} else if (newY >= 650 && newY < 750) {
					for (Tile i : tiles) {

						if (i.getX() == 28 && i.getY() == 625) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(28, 625, 100, 100);
						made_a_move = true;
						currentTile.setName("5");
					}
				} else if (newY >= 760 && newY < 860) {
					for (Tile i : tiles) {

						if (i.getX() == 28 && i.getY() == 735) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(28, 735, 100, 100);
						made_a_move = true;
						currentTile.setName("6");
					}
				} else if (newY >= 870 && newY < 970) {
					for (Tile i : tiles) {

						if (i.getX() == 28 && i.getY() == 845) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(28, 845, 100, 100);
						made_a_move = true;
						currentTile.setName("7");
					}
				} else {
					currentTile.setBounds(tileInitialX, tileInitialY, 100, 100);
				}

			} else if (newX >= 770 && newX < 870) {
				if (newY >= 100 && newY < 200) {
					for (Tile i : tiles) {

						if (i.getX() == 768 && i.getY() == 75) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(768, 75, 100, 100);
						made_a_move = true;
						currentTile.setName("8");
					}
				} else if (newY >= 210 && newY < 310) {
					for (Tile i : tiles) {

						if (i.getX() == 768 && i.getY() == 185) {
							spaceOccupied = true;
							currentTile.setName("9");
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(768, 185, 100, 100);
						made_a_move = true;
					}
				} else if (newY >= 320 && newY < 420) {
					for (Tile i : tiles) {

						if (i.getX() == 768 && i.getY() == 295) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(768, 295, 100, 100);
						made_a_move = true;
						currentTile.setName("10");
					}
				} else if (newY >= 430 && newY < 530) {
					for (Tile i : tiles) {

						if (i.getX() == 768 && i.getY() == 405) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(768, 405, 100, 100);
						made_a_move = true;
						currentTile.setName("11");
					}
				} else if (newY >= 540 && newY < 640) {
					for (Tile i : tiles) {

						if (i.getX() == 768 && i.getY() == 515) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(768, 515, 100, 100);
						made_a_move = true;
						currentTile.setName("12");
					}
				} else if (newY >= 650 && newY < 750) {
					for (Tile i : tiles) {

						if (i.getX() == 768 && i.getY() == 625) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(768, 625, 100, 100);
						made_a_move = true;
						currentTile.setName("13");
					}
				} else if (newY >= 760 && newY < 860) {
					for (Tile i : tiles) {

						if (i.getX() == 768 && i.getY() == 735) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(768, 735, 100, 100);
						made_a_move = true;
						currentTile.setName("14");
					}
				} else if (newY >= 870 && newY < 970) {
					for (Tile i : tiles) {

						if (i.getX() == 768 && i.getY() == 845) {
							spaceOccupied = true;
						}
					}
					if (spaceOccupied == false) {
						currentTile.setBounds(768, 845, 100, 100);
						made_a_move = true;
						currentTile.setName("15");
					}
				} else {
					currentTile.setBounds(tileInitialX, tileInitialY, 100, 100);
				}
			} else {

			}
		}
	}

	/*
	 * these are stubs from here on out that eclipse forced us to import
	 */

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
