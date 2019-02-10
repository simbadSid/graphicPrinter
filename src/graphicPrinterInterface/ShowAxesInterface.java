package graphicPrinterInterface;

import graphicPrinter.Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;





public class ShowAxesInterface implements ActionListener
{
// ----------------------
// Attributs:
// ----------------------
	private Application app;

// ----------------------
// Constructeur:
// ----------------------
	public ShowAxesInterface(Application app)
	{
		this.app = app;
	}

// ----------------------
// Methode locale:
//----------------------
	public void actionPerformed(ActionEvent arg0)
	{
		app.showAxes();
	}
}