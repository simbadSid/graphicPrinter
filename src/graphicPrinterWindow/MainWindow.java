package graphicPrinterWindow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import function.*;
import graphicPrinter.GraphicPrinter;
import graphicPrinterIhm.GraphicGround;









@SuppressWarnings("serial")
public class MainWindow extends JPanel
{
//------------------------------------------------
// Attributs:
//-------------------------------------------------
		// Parametres graphiques
		private final int			maxNbrGridX			= 150;
		private final int			maxNbrGridY			= 150;
		private final int			maxNbrGraduationX	= 150;
		private final int			maxNbrGraduationY	= 150;
		private final double		maxPointSize		= 2./100.;
		private final double		minPointSize 		= 0.8/100.;
		private final double		drawingPrecision	= 5./10;		// Nombre de points par unite
		private final int			nbrPointInGap		= 5;			// Nombre de points dans un pointille
		private final double		matchGapX			= 2./100.;		// Interval horizontal de selection d'une fonction 
		private final double		matchGapY			= 3./100.;		// Interval vertical de selection d'une fonction

		// Variables graphiques
		private double				graduationSize		= 1./50.;
		private double				pointSize			= 1./100.;
		private int					circlPrecision		= 100;
		private int					pointType			= GraphicPrinter.crossPoint;

		// Couleurs
		private Color				backgroundColor 	= Color.BLACK;
		private Color 				axesColor			= Color.WHITE;
		private Color 				graduationColor		= Color.WHITE;
		private Color 				gridColor			= Color.GREEN;
		private Color 				defaultCurveColor	= Color.LIGHT_GRAY;

		// Variables Graphiques du panneau
		private BufferedImage 		image;
		private GraphicGround		gg;

//-------------------------------------------------
// Constructeur
//-------------------------------------------------
		public MainWindow(int width, int height, GraphicGround gg)
		{
			this.setSize(width, height);
			this.gg		= gg;
			initDrawingWindow();
			this.repaint();
		}

//-------------------------------------------------
//Methodes principale
//-------------------------------------------------
		public void paintComponent(Graphics g) 
	    {
			g.drawImage(this.image, UNDEFINED_CONDITION, UNDEFINED_CONDITION, null);			
	    }
		/******************************************************
		 * Initialise le panneau graphique
		 ******************************************************/
		public void initDrawingWindow ()
		{
			this.image			= new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D drawable = this.image.createGraphics();
			drawable.setColor(this.backgroundColor);
			drawable.fillRect(0, 0, this.getWidth(), this.getHeight());
			this.repaint();
		}
		/******************************************************
		 * Affiche la fonction f sur son dommaine de definition
		 * @param linkInterpolationPoint: indique si l'on 
		 * relie les points d'interpolations
		 * @param displayInterpolationPoint: indique si l'on
		 * affiche les points d'interpolation
		 * @param selectedFunction: indique si l'on affiche la 
		 * courbe en pointilles
		 * @param c: si c == null, la fonction est tracee
		 *  avec la couleur par default
		 ******************************************************/
		public void drawFunction(Function f, boolean linkInterpolationPoint, boolean displayInterpolationPoint, boolean selectedFunction, Color c)
		{
			Color c0;
			if (c == null)	c0 = defaultCurveColor;
			else			c0 = c;
			switch(f.getFunctionType())
			{
				case Function.pointFunction:						// Fonction Points non lies
						displayPoint(f, c0, selectedFunction);
						break;
				case Function.InterpolationFunction:				// Fonction Points relies par un polynome
						if (linkInterpolationPoint)
							displayCurve(f, c0, selectedFunction);
						if (displayInterpolationPoint)
							displayPoint(f, c0, selectedFunction);
						break;
				case Function.PiecesOfPolynomialFunction:
						displayPoint(f, c0, selectedFunction);
						displayCurve(f, c0, selectedFunction);
						break;
				case Function.continueFunction1Var:					// Fonction continue par morceau
						displayCurve(f, c0, selectedFunction);
						break;
				default: throw new RuntimeException("Unhandled function Type");
			}
			this.repaint();
		}
		/********************************************************
		 * Affiche la tangente f
		 ********************************************************/
		public void drawTangent(Function f, Color c)
		{
			Graphics2D drawable = this.image.createGraphics();
			drawable.setColor(c);
			double[] X	= f.getX();			
			double[] Y	= f.getY();
			Point p0	= gg.convertAxesToReal(X[0], Y[0]);
			Point p1	= gg.convertAxesToReal(X[1], Y[1]);
			Point p2	= gg.convertAxesToReal(X[2], Y[2]);

			drawable.drawLine(p0.x, p0.y, p2.x, p2.y);
			this.displayCross(p0, c, true);
			this.displayCross(p1, c, false);
			this.displayCross(p2, c, true);
		}
		/********************************************************
		 * 
		 ********************************************************/
		public void drawAxes()
		{
			Graphics2D drawable = this.image.createGraphics();
			drawable.setColor(axesColor);
			Point p1	= gg.convertAxesToReal(gg.getXMin(),	0);
			Point p2	= gg.convertAxesToReal(gg.getXMax(),	0);
			Point p3	= gg.convertAxesToReal(0,				gg.getYMin());
			Point p4	= gg.convertAxesToReal(0,				gg.getYMax());

			drawable.drawLine(p1.x, p1.y, p2.x, p2.y);
			drawable.drawLine(p3.x, p3.y, p4.x, p4.y);
			this.repaint();
		}
		/********************************************************
		 * Affiche des graduations espaces de x et y
		 * @param x: espacement horizontal des graduations
		 * @param y: espacement vertical des graduations
		 ********************************************************/
		public void drawGraduation(double x, double y)
		{
			if ((x <= 0) || (y <= 0)) throw new RuntimeException();
			
			displayGraduation(graduationColor, x, y);
			this.repaint();
		}
		/********************************************************
		 * Affiche une grille espaces de x et y
		 * @param x: espacement horizontal de la grille
		 * @param y: espacement vertical de la grille
		 ********************************************************/
		public void drawGrid(double x, double y)
		{
			if ((x <= 0) || (y <= 0)) throw new RuntimeException();

			displayGrid(gridColor, x, y);
			this.repaint();
		}
		/*********************************************************
		 * Determine si le nombre de graduation a
		 *  dessiner est > au nombre max
		 **********************************************************/
		public boolean isDrawableGraduation(double x, double y)
		{
			int nbrGridX = (int)((gg.getXMax() - gg.getXMin()) / x);
			int nbrGridY = (int)((gg.getYMax() - gg.getYMin()) / y);
			if ((nbrGridX > maxNbrGraduationX) || (nbrGridY > maxNbrGraduationY)) return false;
			return true;
		}
		/*********************************************************
		 * Determine si le nombre de grilles a
		 *  dessiner est > au nombre max
		 **********************************************************/
		public boolean isDrawableGrid(double x, double y)
		{
			int nbrGridX = (int)((gg.getXMax() - gg.getXMin()) / x);
			int nbrGridY = (int)((gg.getYMax() - gg.getYMin()) / y);
			if ((nbrGridX > maxNbrGridX) || (nbrGridY > maxNbrGridY)) return false;
			return true;
		}
		public void resetSize(int width, int height)
		{
			this.setSize(width, height);
		}

//-------------------------------------------------
// Accesseur
//-------------------------------------------------
		public BufferedImage	getImage()			{return image;}
		public Color			getBGColor()		{return backgroundColor;}
		public Color			getAxesColor()		{return axesColor;}
		public Color			getGridColor()		{return gridColor;}
		public Color			getGraduationColor(){return graduationColor;}
		public int				getPointType()		{return pointType;}
		public int				getMatchGapX()		{return (int)(getWidth()*matchGapX);}
		public int				getMatchGapY()		{return (int)(getHeight()*matchGapY);}
		public double			getPointSizePercent(){return (pointSize - minPointSize) * 100 / (maxPointSize-minPointSize);}

//--------------------------------------------
//Modificateur:
//--------------------------------------------
		public boolean setBGColor(Color c)
		{
			if (c == backgroundColor) return false;
			backgroundColor = c;
			return true;
		}
		public boolean setAxesColor(Color c)
		{
			if (c == axesColor) return false;
			axesColor = c;
			return true;
		}
		public boolean setGridColor(Color c)
		{
			if (c == gridColor ) return false;
			gridColor = c;
			return true;
		}
		public boolean setGraduationColor(Color c)
		{
			if (c == graduationColor) return false;
			graduationColor = c;
			return true;
		}
		public boolean setPointType(int pt)
		{
			if (pt == pointType) return false;
			pointType = pt;
			return true;
		}
		public boolean setPointSize(double percent)
		{
			if ((percent < 0) || (percent > 100))throw new RuntimeException("Wrong percent: " + percent);
			double ps = minPointSize + (maxPointSize-minPointSize)*percent/100;
			if (ps == pointSize) return false;
			pointSize = ps;
			return true;
		}

//----------------------------------------------------
// Methodes d'affichage elementaire
//----------------------------------------------------
		/**********************************************************
		 * Affiche la courbe de la fonction f en reliant ces points
		 * par des droites
		 *********************************************************/
		private void displayCurve(Function f, Color c, boolean dottedLine)
		{
			Graphics2D drawable = this.image.createGraphics();
			drawable.setColor(c);

			for (int i = 0; i<f.getNbrDomain(); i++)						// Pour chaque interval de definition:
			{
				int dist		= gg.convertDistAxesToRealX(f.getDomainSize(i));
				int nbrPoint	= (int)(dist * drawingPrecision);			// Determiner le nombre de points a tracer
				Double[] X		= new Double[nbrPoint];
				Double[] Y		= new Double[nbrPoint];
				f.eval(i, X, Y);											// Determiner tous les point a tracer
				boolean draw	= false;

				for (int j=0; j<X.length; j++)
				{
					while((j<X.length) && (Y[j] == null)) j++;				// Sauter les point non definis
					if (j>=X.length) break;
					Point p0 = gg.convertAxesToReal(X[j], Y[j]), p1;
					if (j == (X.length-1))									// Cas d'un point extremum
					{
						drawable.drawLine(p0.x, p0.y, p0.x, p0.y);
						break;
					}
					if(Y[j+1] == null) continue;							// Cas ou un seul point est defini
					p1 = gg.convertAxesToReal(X[j+1], Y[j+1]);
					if (j%this.nbrPointInGap == 0) draw = !draw;
					if ((!dottedLine) || (draw))
					{
						drawable.drawLine(p0.x, p0.y, p1.x, p1.y);
					}
				}
			}
		}
		/**********************************************************
		 * Affiche les points de la fonction discontinue f sans
		 * relier ces points
		 *********************************************************/
		private void displayPoint(Function f,Color c, boolean dottedLine)
		{
			Graphics2D drawable = this.image.createGraphics();
			drawable.setColor(c);
			double[] x = f.getX();
			double[] y = f.getY();
			Point p0, p1;

			if (x.length == 0) return;
			p0 = gg.convertAxesToReal(x[0], y[0]);
			displayPoint(p0, c, dottedLine);
			for (int i = 1; i<x.length; i++)
			{
				p1 = gg.convertAxesToReal(x[i], y[i]);
				displayPoint(p1, c, dottedLine);
				p0 = p1;
			}
		}
		/***************************************************************
		 * Realise l'affichege des graduation
		 * @param x: espacement horizontal de la grille
		 * @param y: espacement vertical de la grille
		 ****************************************************************/
		private void displayGraduation(Color c, double x, double y)
		{
			Graphics2D drawable = this.image.createGraphics();
			drawable.setColor(c);
			int size = (int)(Math.min(getWidth(), getHeight()) * this.graduationSize);
			double k, xi, yi;

			// Dessin des lignes vertiales
			k	= Math.ceil(gg.getXMin()/x);
			xi	= k * x;
			while (xi < gg.getXMax())
			{
				Point np = gg.convertAxesToReal(xi, 0);
				drawable.drawLine(np.x, np.y, np.x, np.y-size);
				xi += x;
			}
			// Dessin des lignes horizontales
			k	= Math.ceil(gg.getYMin()/y);
			yi	= k * y;
			while (yi < gg.getYMax())
			{
				Point np = gg.convertAxesToReal(0, yi);
				drawable.drawLine(np.x, np.y, np.x+size, np.y);
				yi += y;
			}
		}
		/***************************************************************
		 * Realise l'affichege de la grille
		 * @param x: espacement horizontal de la grille
		 * @param y: espacement vertical de la grille
		 ****************************************************************/
		private void displayGrid(Color c, double x, double y)
		{
			Graphics2D drawable = this.image.createGraphics();
			drawable.setColor(c);
			double k, xi, yi;

			// Dessin des lignes vertiales
			k	= Math.ceil(gg.getXMin()/x);
			xi	= k * x;
			while (xi < gg.getXMax())
			{
				Point np = gg.convertAxesToReal(xi, 0);
				drawable.drawLine(np.x, 0, np.x, this.getHeight());
				xi += x;
			}
			// Dessin des lignes horizontales
			k	= Math.ceil(gg.getYMin()/y);
			yi	= k * y;
			while (yi < gg.getYMax())
			{
				Point np = gg.convertAxesToReal(0, yi);
				drawable.drawLine(0, np.y, this.getWidth(), np.y);
				yi += y;
			}
		}
		/***************************************************************
		 * Affiche une croix ou un point au point p
		 ***************************************************************/
		private void displayPoint(Point p, Color c, boolean dottedLine)
		{
			switch(pointType)
			{
				case GraphicPrinter.crossPoint:	this.displayCross(p, c, dottedLine);	break;
				case GraphicPrinter.roundPoint:	this.displayCircl(p, c, dottedLine);	break;
			}
		}
		private void displayCross(Point p, Color c, boolean dottedLine)
		{
			Graphics2D drawable = this.image.createGraphics();
			int d = (int)(pointSize * Math.min(getWidth(), getHeight()));
			drawable.setColor(c);

			drawable.drawLine(p.x+d, p.y-d, p.x-d, p.y+d);
			drawable.drawLine(p.x+d, p.y+d, p.x-d, p.y-d);
			if (dottedLine)
			{
				drawable.drawLine(p.x,		p.y-d,	p.x,	p.y+d);
				drawable.drawLine(p.x+d,	p.y,	p.x-d,	p.y);
			}
		}
		private void displayCircl(Point p, Color c, boolean dottedLine)
		{
			Graphics2D drawable = this.image.createGraphics();
			int d = (int)(pointSize * Math.min(getWidth(), getHeight()));
			drawable.setColor(c);

			double dTeta 	= Math.PI / (double)circlPrecision;
			double teta 	= 0;
			int[] xi = new int[2*circlPrecision];
			int[] yi = new int[2*circlPrecision];
			for (int i = 0; i<circlPrecision; i++)
			{
				int x					= (int)(d * Math.cos(teta));
				int y					= (int)(d * Math.sin(teta));
				xi[i] 					= p.x + x;
				yi[i] 					= p.y - y;
				xi[i+circlPrecision]	= p.x + x;
				yi[i+circlPrecision]	= p.y + y;
				teta += dTeta;
			}
			drawable.setPaint(c);
			Polygon s = new Polygon(xi, yi, 2*circlPrecision);
			drawable.fill(s);

			if (dottedLine)
			{
				drawable.setColor(backgroundColor);
				drawable.drawLine(p.x+d, p.y-d, p.x-d, p.y+d);
				drawable.drawLine(p.x+d, p.y+d, p.x-d, p.y-d);
				drawable.drawLine(p.x,		p.y-d,	p.x,	p.y+d);
				drawable.drawLine(p.x+d,	p.y,	p.x-d,	p.y);
			}
		}
}