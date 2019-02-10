package graphicPrinterInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import graphicPrinter.Application;










public class SaveInterface implements ActionListener
{
// ------------------------------------ Attributs ----------------------------
	private String			defaultDirectory	= "savedImages/";
	private Application		application;

// ------------------------------------ Constructeur--------------------------
	public SaveInterface(Application application)
	{
		this.application	= application;
	}

// ------------------------------------ Methodes locales ---------------------
	public void actionPerformed(ActionEvent e) 
	{
		JFileChooser chooser = new JFileChooser(defaultDirectory);
		chooser.showSaveDialog(null);
		File fic = chooser.getSelectedFile();
		if (fic == null) return;
		if (fic.exists())
        {
        	String message  = "An image named \"" + fic.getName()+ "\" alredy exists.\n";
        	message += "Do you want to replace it?";
        	
        	Object[] options = {"Replace", "Change the file name", "Cancel"};
        	int n = JOptionPane.showOptionDialog(null,
									 message,
									 "Save image",
									 JOptionPane.YES_NO_OPTION,
									 JOptionPane.QUESTION_MESSAGE,
									 null ,     					//do not use a custom Icon
									 options,						//the titles of buttons
									 options[0]);					//default button title
        	switch(n)
        	{
	        	case 0: break;
	        	case 1: actionPerformed(e); return;
	        	case 2: return;
        	}
        }
		else
		{
			try						{fic.createNewFile();}
			catch (IOException e1)	{e1.printStackTrace();return;}
		}
		try 
		{
			application.saveImage(fic);
		}
		catch(FileNotFoundException e1)
		{
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occured while opening the file \"" + fic.getName()+ ": 1\".");
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occured while opening the file \"" + fic.getName()+ ": 2\".");
		}
	}
}