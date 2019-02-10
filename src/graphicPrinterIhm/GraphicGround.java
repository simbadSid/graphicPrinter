package graphicPrinterIhm;

import java.awt.Point;









public class GraphicGround
{
// --------------------------------------------- 
// Attributs:
//----------------------------------------------
		private double	 	xMin;
		private double		xMax;
		private double		yMin;
		private double		yMax;
		private int			widthWindow;
		private int			heightWindow;

// ---------------------------------------------
// Constructeur:
// ---------------------------------------------
		public GraphicGround(double xMin, double xMax, double yMin, double yMax, int widthWindow, int heightWindow)
		{
			if ((xMin >= xMax) || (yMin >= yMax) || (widthWindow <= 0) || (heightWindow <= 0)) 
				throw new RuntimeException("Parametre invalide: xMin = " + xMin + "  xMax = " + xMax + "  yMin = " + yMin + "  yMax = " + yMax + "  width = " + widthWindow+ "  height = " + heightWindow);

			this.xMin			= xMin;
			this.xMax			= xMax;
			this.yMin			= yMin;
			this.yMax			= yMax;
			this.widthWindow	= widthWindow;
			this.heightWindow	= heightWindow;
		}

// ---------------------------------------------
// Accesseurs:
//---------------------------------------------
		public double	getXMin()				{return xMin;}
		public double	getXMax()				{return xMax;}
		public double	getYMin()				{return yMin;}
		public double	getYMax()				{return yMax;}
		public int		getHeightFrame()		{return this.heightWindow;}
		public int		getWidthFrame()			{return this.widthWindow;}

// ---------------------------------------------
// Modificateur:
//---------------------------------------------
		/*************************************************************
		 * redimensionne le panneau
		 *************************************************************/
		public void resize(int width, int height)
		{
			if ((width <= 0) || (height <= 0)) 
				throw new RuntimeException("Parametre negatif: width = " + width+ "height = " + height);

			this.widthWindow	= width;
			this.heightWindow	= height;
		}
		/*************************************************************
		 * redimensionne les axes
		 *************************************************************/
		public void resetAxes(double xMin, double xMax, double yMin, double yMax)
		{
			if ((xMin >= xMax) || (yMin >= yMax))
				throw new RuntimeException("Parametre invalide: xMin = " + xMin + "  xMax = " + xMax + "  yMin = " + yMin + "  yMax = " + yMax);

			this.xMin			= xMin;
			this.xMax			= xMax;
			this.yMin			= yMin;
			this.yMax			= yMax;
		}

// ---------------------------------------------
// Methodes Locales:
// ---------------------------------------------
		/**************************************************************************
		 * Converti un point du referentiel reel(de la fenetre graphique) au referentiel des axes
		 **************************************************************************/
		public double convertRealToAxesX(double x)
		{
			double tx	= x / widthWindow;
			return (xMin + tx * (xMax - xMin));
		}
		/**************************************************************************
		 * Converti un point du referentiel reel(de la fenetre graphique) au referentiel des axes
		 **************************************************************************/
		public double convertRealToAxesY(double y)
		{
			double ty	= y / heightWindow;
			return (yMax + ty * (yMin - yMax));
		}
		/**************************************************************************
		 * Converti un point du referentiel des axes en un point du referentiel reel
		 **************************************************************************/
		public Point convertAxesToReal(double x, double y)
		{
			double dx	= xMax - xMin;
			double dy	= yMax - yMin;
			double tx	= (x - xMin) / dx;
			double ty	= (y - yMax) / dy;
			int resX	= (int)(tx * widthWindow);
			int resY	= (int)(-ty * heightWindow);
			return new Point(resX, resY);
		}
		public int convertDistAxesToRealX(double xDist)
		{
			return (int)(xDist * widthWindow / (xMax - xMin));
		}
		public int convertDistAxesToRealY(double yDist)
		{
			return (int)(yDist * heightWindow / (yMax - yMin));
		}
		public double convertDistRealToAxesX(int xDist)
		{
			return (xDist * (xMax - xMin) / widthWindow);
		}
		public double convertDistRealToAxesY(int yDist)
		{
			return (yDist * (yMax - yMin) / heightWindow);
		}
}