package auxiliaire;

import java.awt.Point;



/**********************************************
 * Methodes auxiliaires mathematiques
 * @author kassuskley
 **********************************************/





public class AuxMath
{
	/***********************************************************
	 * Calcule le det de la matrice
	 ***********************************************************/
	public static double determinant(double[][] matrix)
	{
		double sum=0,sign;

		if(matrix.length == 1)	return(matrix[0][0]);

		for(int i=0;i<matrix.length;i++)
		{
			double[][]smaller = new double[matrix.length-1][matrix.length-1]; //creates smaller matrix- values not in same row, column
			for(int a=1;a<matrix.length;a++)
			{
				for(int b=0; b<matrix.length; b++)
				{
					if(b<i)			smaller[a-1][b]		= matrix[a][b];
					else if(b>i)	smaller[a-1][b-1]	= matrix[a][b];
				}
			}
			if(i%2==0)	sign = 1;											//sign changes based on i
			else		sign =-1;
			sum += sign*matrix[0][i]*(determinant(smaller));
		}
		return(sum); 														//returns determinant value. once stack is finished, returns final determinant.
	}
	/***********************************************************
	 * Determine le produit vectoriel entre 
	 * les vecteur P0P1 et P2P3
	 ***********************************************************/
	public static double vect(Point p0, Point p1, Point p2, Point p3)
	{
		double v1x = p1.x - p0.x;
		double v2x = p3.x - p2.x;
		double v1y = (p1.y - p0.y)*(-1);		// -1 en raison de l'orientation du repere
		double v2y = (p3.y - p2.y)*(-1);

		double res = v1x * v2y - v1y * v2x;
		return res;
	}
	/***********************************************************
	* Determine l'angle en radian de (P0P1, P2P3)
	 ***********************************************************/
	public static double angle(Point p0, Point p1, Point p2, Point p3)
	{
		double v1x = p1.x - p0.x;
		double v2x = p3.x - p2.x;
		double v1y = (p1.y - p0.y)*(-1);		// -1 en raison de l'orientation du repere
		double v2y = (p3.y - p2.y)*(-1);
		double a = Math.sqrt(v1x*v1x+v1y*v1y);
		double b = Math.sqrt(v2x*v2x+v2y*v2y);
		double cos = (v1x*v2x + v1y*v2y)/(a*b);
		double sin = (v1x*v2y - v1y*v2x)/(a*b); 

		if (cos == 0)
		{
			if (sin > 0) return Math.PI/2;
			if (sin < 0) return (-1)*Math.PI/2;
			if (sin == 0) throw new RuntimeException("Erreur dans la fonction angle 1. \n");
		}
		if (sin == 0)
		{
			if (cos > 0) return 0;
			if (cos < 0) return Math.PI;
			if (cos == 0) throw new RuntimeException("Erreur dans la fonction angle 2. \n");
		}
		double teta = Math.atan(sin/cos);
		if (teta > 0)
		{
			if ((sin > 0) && (cos > 0)) return teta;
			if ((sin < 0) && (cos < 0)) return (teta + Math.PI);
			else throw new  RuntimeException("Erreur dans la fonction angle 3. \n");
		}
		if (teta < 0)
		{
			if ((sin < 0) && (cos > 0)) return teta;
			if ((sin > 0) && (cos < 0)) return (teta + Math.PI);
			else throw new  RuntimeException("Erreur dans la fonction angle 4. \n");		
		}
//		else  throw new RuntimeException("Erreur dans la fonction angle 5. \n");
		return (double) 0;
	}
}