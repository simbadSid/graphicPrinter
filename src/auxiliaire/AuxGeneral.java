package auxiliaire;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;






/**********************************************
 * Methodes auxiliaires general
 * @author kassuskley
 **********************************************/




public class AuxGeneral
{
	/***********************************************************
	 * Determine un degrade de la couleur c de degre d sachant
	 *    que le degres max est nbrDegrades
	 * @param c				: Couleur a degrader
	 * @param nbrDegrades	: nombre max de degres
	 * @param degrade		: degres de la couleur recherchee
	 * @param darkness		: vrai si le resulta doit etre plus foncé
	 ***********************************************************/
	public static Color getGradient(Color c, int nbrDegrades, int d)
	{
		if ((nbrDegrades <= 0) || (d < 0) || (d > nbrDegrades)) throw new RuntimeException();
		boolean darkness;
		int red   = c.getRed();
		int green = c.getGreen();
		int blue  = c.getBlue();

		darkness = initDarkness(c);
		if (darkness == true)
		{
			red   -= d * red   /(2*nbrDegrades);
			green -= d * green /(2*nbrDegrades);
			blue  -= d * blue  /(2*nbrDegrades);
		}
		else
		{
			red   += d * (255 - red)   / nbrDegrades;
			green += d * (255 - green) / nbrDegrades;
			blue  += d * (255 - blue)  / nbrDegrades;

		}
		return new Color(red, green, blue);
	}
	// Indique si le degrade doit etre positif ou negatif
	private static boolean initDarkness(Color c)
	{
		int half 	= 256 / 2;
		int nbrPlus	= 0;

		if (c.getRed() 	 > half)	nbrPlus ++;
		if (c.getGreen() > half)	nbrPlus ++;
		if (c.getBlue()	 > half)	nbrPlus ++;
		if (nbrPlus > 1)	return true;
		else				return false;
	}
	/***********************************************************
	 * Cree une liste identique a l (en changeant tout les pointeurs de l)
	 ***********************************************************/
	public static LinkedList<Point> copyList(LinkedList<Point> l)
	{
		LinkedList<Point> res = new LinkedList<Point>();
		
		for (int i = 0; i<l.size(); i++)
		{
			Point pi = l.get(i);
			Point pf = new Point(pi.x, pi.y);
			res.addLast(pf);
		}
		return res;
	}
	/***********************************************************
	 * Cree un tableau identique a tab (en changeant tout
	 *  les pointeurs de tab)
	 ***********************************************************/
	public static double[] copyDoubleTab(double[] tab)
	{
		double[] res = new double[tab.length];
		
		for (int i = 0; i<tab.length; i++)
		{
			res[i] = tab[i];
		}
		return res;
	}
	/***********************************************************
	 * Affiche un tableau
	 ***********************************************************/
	public static void printTab(double[] tab)
	{
		if (tab.length == 0) return;
		for (int x = 0; x<tab.length; x++)
		{
			System.out.print(tab[x] + "\t");
		}
		System.out.println();
	}
	/***********************************************************
	 * Affiche une matrice 
	 ***********************************************************/
	public static void printMatrix(double[][] mat)
	{
		if (mat.length == 0) return;
		for (int y = 0; y<mat[0].length; y++)
		{
			for (int x = 0; x<mat[y].length; x++)
			{
				System.out.print(mat[x][y] + "\t");
			}
			System.out.println();
		}
	}
}