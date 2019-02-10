package graphicPrinterInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import graphicPrinter.Application;










public class NewInterface implements ActionListener
{
//------------------------------------ Attributs ----------------------------
	private Application application;

//------------------------------------ Constructeur--------------------------
	public NewInterface(Application app)
	{
		this.application = app;
	}

//------------------------------------ Methodes locales ---------------------
	public void actionPerformed(ActionEvent e) 
	{
		application.initApplication();
	}
}