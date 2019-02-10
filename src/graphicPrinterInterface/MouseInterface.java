package graphicPrinterInterface;

import java.awt.event.*;

import graphicPrinter.Application;







public class MouseInterface implements MouseListener, MouseMotionListener, ActionListener
{
// ------------------------------------------ 
// Attributs:
// ------------------------------------------
		private Application		application;
		private boolean			tangent;

// ------------------------------------------
// Constructeur:
// ------------------------------------------
		public MouseInterface(Application app)
		{
			this.application 	= app;
			this.tangent		= false;
		}

// ------------------------------------------
// Methodes Locales: 
// ------------------------------------------
		/*Active ou desactive la fonction PrintTangent*/
		public void actionPerformed(ActionEvent arg0)
		{
			tangent = !tangent;
			application.enableTangentPrinter(tangent);
		}
		/*Selectionne une fonction*/
		public void mousePressed(MouseEvent e) 
		{
			application.selectFunction(e.getX(), e.getY());
		}
		public void mouseMoved(MouseEvent e)
		{
			application.mooveMouse	(true, e.getX(), e.getY());
			if (tangent) application.printTangent(true, e.getX(), e.getY());
		}

		public void mouseExited(MouseEvent e)
		{
			application.mooveMouse	(false, -1, -1);
		}
	    public void mouseEntered(MouseEvent e) 	{}
	    public void mouseClicked(MouseEvent e) 	{}
	    public void mouseDragged(MouseEvent e)	{}
	    public void mouseReleased(MouseEvent e) {}
}