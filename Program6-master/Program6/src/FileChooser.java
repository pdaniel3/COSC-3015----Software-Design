
import java.io.*;
import java.net.URISyntaxException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class FileChooser extends JPanel
                         implements ActionListener {
    
	private static final long serialVersionUID = 1L;
	JButton openButton, saveButton;
    JTextArea log;
    JFileChooser fc;
    static GameWindow g;
    
    JFrame frame;
 
    public FileChooser(GameWindow window) {
        super(new BorderLayout());
        g = window;
        fc = new JFileChooser();
 
        openButton = new JButton("Open a File");
        openButton.addActionListener(this);
 
        //Create the save button.  We use the image from the JLF
        //Graphics Repository (but we extracted it from the jar).
        saveButton = new JButton("Save a File");
        saveButton.addActionListener(this);
 
        //For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(); //use FlowLayout
        buttonPanel.add(openButton);
        buttonPanel.add(saveButton);
 
        //Add the buttons and the log to this panel.
        add(buttonPanel, BorderLayout.PAGE_START);
        //add(logScrollPane, BorderLayout.CENTER);
    }

	public void actionPerformed(ActionEvent e) {
 
        //Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(FileChooser.this);
 
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                
                String extension = "";

                int i = file.getName().lastIndexOf('.');
                if (i >= 0) {
                    extension = file.getName().substring(i+1);
                }
                
                if(!(extension.equals("mze"))){
                	JOptionPane.showMessageDialog(this, "You must select a .mze file.");
                } else {
                	try {
						g.newGame(file);
					} catch (ClassNotFoundException | IOException | URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                }
            } 
        //Handle save button action.
        } else if (e.getSource() == saveButton) {
            int returnVal = fc.showSaveDialog(FileChooser.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                WriteData writer = new WriteData();
                try {
					writer.createArray(g.getTileArr(), g.getIfPlayed(), g.getTime());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
                 byte[] bytes = writer.getBytes();
                 
                 FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(file);
				} catch (FileNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				try {
					fos.write(bytes);
					fos.flush();
					fos.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
    }
 
    //makes the window pop up
    public void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("FileChooserDemo");
 
        //Add content to the window.
        frame.add(new FileChooser(g));
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public void exit() {
    	frame.dispose();
    }
}
