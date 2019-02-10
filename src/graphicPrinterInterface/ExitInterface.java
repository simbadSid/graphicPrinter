package graphicPrinterInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;









public class ExitInterface implements ActionListener
{
// ------------------------------------ Attributs ----------------------------
		private SaveInterface	saveInterface;

// ------------------------------------ Constructeur--------------------------
		public ExitInterface(SaveInterface saveInterface)
		{
			this.saveInterface	= saveInterface;
		}

// ------------------------------------ Methodes locales ---------------------
		public void actionPerformed(ActionEvent e) 
		{
			String message = "Save changes before closing?";
			Object[] options = {"Close without saving", "Cancel", "Save"};
			int n = JOptionPane.showOptionDialog(null,
									 message,
									 "Exit ?",
									 JOptionPane.YES_NO_OPTION,
									 JOptionPane.QUESTION_MESSAGE,
									 null ,     //do not use a custom Icon
									 options,  //the titles of buttons
									 options[0]); //default button title
			switch (n)
			{
				case 0: break;
				case 1: return;
				case 2: saveInterface.actionPerformed(e); break;
			}
			System.exit(0);
		}
}