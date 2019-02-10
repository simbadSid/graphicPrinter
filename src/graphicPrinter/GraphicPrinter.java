package graphicPrinter;

import javax.swing.SwingUtilities;

import graphicPrinterIhm.GraphicPrinterIhm;











public class GraphicPrinter implements Runnable
{
//--------------------------------------------
// Attributs:
//--------------------------------------------
		public static final int		crossPoint	= 0;
		public static final int		roundPoint	= 1;
		
		private int					defaultXMin	= -5; 
		private int					defaultXMax	= 5; 
		private int					defaultYMin	= -5; 
		private int					defaultYMax	= 5; 
		private	Application			application;

//--------------------------------------------
// Constructeur:
//--------------------------------------------
		public GraphicPrinter()
		{
		}

//--------------------------------------------
// Methodes principale:
//--------------------------------------------
		public void run() 
		{
			application = new Application(defaultXMin, defaultXMax, defaultYMin, defaultYMax);
			new GraphicPrinterIhm(application, defaultXMin, defaultXMax, defaultYMin, defaultYMax);
			application.initApplication();
		}

		public static void main(String [] args) 
		{
			System.out.println("Welcome to this graphic printer application !");
			SwingUtilities.invokeLater(new GraphicPrinter());
		}
}