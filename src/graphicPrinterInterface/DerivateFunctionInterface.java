package graphicPrinterInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

import graphicPrinter.Application;









public class DerivateFunctionInterface implements ActionListener
{
// --------------------------------
// Attributs:
//--------------------------------
		private Application		app;

//--------------------------------
// Constructeur:
//--------------------------------
		public DerivateFunctionInterface(Application app)
		{
			this.app = app;
		}

//--------------------------------
// Methodes Locales:
//--------------------------------
		public void actionPerformed(ActionEvent e)
		{
			String func = app.getSelectedFunction();
			
			if (func == null)
			{
				func = chooseFunction();
				if (func == null) return;
			}
			while(true)
			{
				String message = "Do you really want to derivate the function: " + func + " ?";
				Object[] options = {"YES", "NO", "ChangeFunction"};
				int test = JOptionPane.showOptionDialog(null,
										 message,
										 "",
										 JOptionPane.YES_NO_OPTION,
										 JOptionPane.QUESTION_MESSAGE,
										 null ,     //do not use a custom Icon
										 options,  //the titles of buttons
										 options[0]); //default button title
				switch (test)
				{
					case 0: 
							try			{app.derivateFunction(func);}
							catch(Exception ex)
							{
								ex.printStackTrace();
								message = "The function " + func + " is not known!";
								JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
								break;
							}
							return;
					case 2: func = chooseFunction(); break;
					default: return;
				}
	
			}
		}

//--------------------------------
// Methodes Auxiliaires:
//--------------------------------
		private String chooseFunction()
		{
			String[] funcList = app.getDerivableFunction();
			if (funcList == null)
			{
				JOptionPane.showMessageDialog(null, "Ther are no derivable functions!" );
				return null;
			}
			String message = "Please choose the function to derivate";
			return (String)JOptionPane.showInputDialog(null, message, "Function Choose", JOptionPane.PLAIN_MESSAGE, null, funcList, null);
		}
}