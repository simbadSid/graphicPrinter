package function;

import symbolicComputation.Domain;
import symbolicComputation.ExceptionVariableRepresentation;
import symbolicComputation.Variable;
import function.Polynomial.MinMax;
import auxiliaire.AuxGeneral;
import auxiliaire.AuxMath;;








public class VandermondeInterpolationFunction implements Function
{
//--------------------------------------------
// Attributs:
//--------------------------------------------
		// Parametres de la classe
		public static final int		maxInterpolationDegres	= 9;

		// Variables de la classe
		private int					functionType		= InterpolationFunction;
		private String				function;			// Representation textuelle de l'expression formelle
		private String				domain;			// Representation textuelle du domaine de definition
		private String				stringFunctionType	= "Continue function by Vandermondian interpolation";
		private double				xMin;
		private double				xMax;
		private double				yMin;
		private double				yMax;
		private double[]			X;
		private double[]			Y;
		private Polynomial[]		interpolationPolynome;
		private int					interpolationDegres;
		private String				variable			= "x";

//--------------------------------------------
// Constructeur:
//--------------------------------------------
		/************************************************************
		 * Cree une fonction constitue de polynomes degres (degres) sur
		 * chaque interval [Xi, Xi+1]
		 * @param degres: Degres du polynome d'interpolation entre 
		 * chaque point [Xi, Xi+1]
		 * functionName: expression formelle de la fonction (optionelle)
		 * @throws Runtime Exception si le degres est < 1 ou > maxInterpolationDegres, 
		 * ou en cas de doublons de X, ou s'il n'y a pas 
		 * assez de point pour une interpolation de degres donne
		 **************************************************************/
		public VandermondeInterpolationFunction(double[] X, double[] Y, String functionName, int degres) throws RuntimeException
		{
			this.X						= AuxGeneral.copyDoubleTab(X);
			this.Y						= AuxGeneral.copyDoubleTab(Y);
			this.interpolationDegres	= degres;
			checkData();
			sortByX();
			xMin = X[0];
			xMax = X[X.length-1];
			initInterpolationPolynomes();
			optimizeInterpolationDegres();
			initYSides();
			if ((functionName == null) || (functionName.length()== 0))
				this.function	= "Unknown formel expression";
			else
				this.function	= "" + functionName;
			this.domain	= "[" + xMin + " --> " + xMax + "]";
		}

//--------------------------------------------
// Accesseur:
//--------------------------------------------
		public String	getVariable()				{return new String(variable);}
		public double[]	getX()						{return AuxGeneral.copyDoubleTab(X);}
		public double[]	getY()						{return AuxGeneral.copyDoubleTab(Y);}
		public double	getXMin()					{return xMin;}
		public double	getXMax()					{return xMax;}
		public double	getYMin()					{return yMin;}
		public double	getYMax()					{return yMax;}
		public int		getNbrDomain()				{return 1;}
		public double	getDomainSize(int domain)	{if(domain!= 0)throw new RuntimeException("Unknonw domain!");return (xMax - xMin);}
		public int 		getFunctionType()			{return functionType;}
		public String	getFunctionRepresentation()	{return ""+function;}
		public String	getDomainRepresentation()	{return domain;}
		public String	getTypeRepresentation()		{return ""+stringFunctionType 	+ " of degres " + interpolationDegres;}
		public int		getInterpolationDegres()	{return interpolationDegres;}
		public int		getMaxInterpolationDegres()	{return maxInterpolationDegres;}
		public boolean	isDerivable()				{return true;}
 		public boolean	isVariableSetable()			{return true;}
		public void		setVariable(String s) throws ExceptionVariableRepresentation
 		{
 			if (!Variable.isVariableName(s)) throw new ExceptionVariableRepresentation();
 			this.variable	= new String(s);
 		}
		public String toString()
		{
			String res = "";
			for (int i=0; i<this.interpolationPolynome.length; i++)
			{
				res += "[" + X[i] + " --> " + X[i+1] + "]" + "     ";
				res += interpolationPolynome[i].toString(variable) + "\n";
			}
			return res;
		}

//--------------------------------------------
// Methodes principale:
//--------------------------------------------
 		public boolean	equal (String representation)
 		{
 			return (representation.equals(this.function));
 		}
		/************************************************************
		 * Rend la valeur de y correspondant a x par interpolation 
		 * des points entourant x par un polynome de
		 * degres this.interpolationDegres
		 * @throws RuntimeException: Si le point d'abscisse x est en 
		 * dehors du domaine de definition
		 **************************************************************/
		public double eval(double x)
		{
			if ((x < getXMin()) || (x > getXMax())) throw new RuntimeException("X value is out of the definition domain: "+ x);

			// Determiner l'interval de x
			int i = -1;
			if (x == X[0])			return Y[0];
			for (i = 0; i<X.length-1; i++)
			{
				if (x == X[i+1])	return Y[i+1];
				if (x <  X[i+1])	break;
			}
			// Evalue la fonction en utilisant le polynome trouve
			return eval(x, i);
		}
		/****************************************************************
		 * Remplit les points X et Y de points equidistants
		 * appartenant au domaine domainId
		 ***************************************************************/
		public void eval(int domainId, Double[] X, Double[] Y)
		{
			if (domainId != 0) throw new RuntimeException("unknown definition domain Id");
			double dx	= (xMax - xMin) / (X.length-1);
			int l		= X.length;
			for (int i = 0; i<=l/2; i++)
			{
				X[i]		= xMin + (i * dx);
				Y[i]		= eval(X[i]);
				X[l-1-i]	= xMax - (i * dx);
				Y[l-1-i]	= eval(X[l-1-i]);
			}
		}
		/**************************************************************
		 * Returns The derivated function
		 **************************************************************/
		public PiecesOfPolynomials derivate()
		{
			Polynomial[] polys = new Polynomial[interpolationPolynome.length];
			Domain[] domains	= new Domain[interpolationPolynome.length];

			double x0 = X[0];
			for (int i=1; i<X.length; i++)
			{
				double x1			= X[i];
				domains[i-1]		= new Domain(x0, x1, false, false);
				polys[i-1]			= this.interpolationPolynome[i-1].getDerivate();
				x0					= x1;
			}
			String function = "d( " + this.function + " ) / dx";
			return new PiecesOfPolynomials(polys, domains, function);
		}
		/**************************************************************
		 * Determine si le point (x, y) aproche a gap appartient 
		 * a la fonction
		 **************************************************************/
		public boolean matchPoint(double x, double y, double gapX, double gapY)
		{
			if (gapY <= 0) throw new RuntimeException("negative gap: " + gapY);
			double hx = gapX / 2.;
			double hy = gapY / 2.;

			if (x < X[0]-hx)				return false;
			if (x > X[X.length-1]+hx)		return false;
			int i;
			for (i = 0; i<=X.length; i++)
			{
				if (x <  X[i+1])			break;
			}
			double ny = eval(x, i);
			return ((ny >= y-hy) && (ny <= y+hy));
		}
		/**************************************************************
		 * Affiche tous les polynomes d'interpolations
		 **************************************************************/
		public void printInterpolationPolynomes()
		{
			for (int i=0; i<interpolationPolynome.length; i++)
			{
				System.out.println(interpolationPolynome[i]);
			}
		}

//--------------------------------------------
// Methodes auxiliaires:
//--------------------------------------------
		/***************************************************************** 
		 * Verifie la presence de valeur infinie ou Nan dans X et Y
		 * Verifie le degres des polynomes d'interpolation
		 *****************************************************************/
		private void checkData()
		{
			if ((interpolationDegres > maxInterpolationDegres) ||
				(interpolationDegres<1))throw new RuntimeException("Unhandled interpolation degres: " + interpolationDegres);
			if  (X.length != Y.length)	throw new RuntimeException("Uncompatibl arguments X, Y");
			if  (X.length == 0)			throw new RuntimeException("Umpty arguments X and Y");
			if  (X.length < interpolationDegres+1)
										throw new RuntimeException("No enought points to interpole!");

			for (int i = 0; i<X.length; i++)
			{
				if ((X[i] == Double.POSITIVE_INFINITY) ||
					(X[i] == Double.NEGATIVE_INFINITY)) throw new RuntimeException("Infinite X value at index: " + i);
				if  (Double.isNaN(X[i]))				throw new RuntimeException("NaN X value at index: " + i);
				if ((Y[i] == Double.POSITIVE_INFINITY) ||
					(Y[i] == Double.NEGATIVE_INFINITY)) throw new RuntimeException("Infinite Y value at index: " + i);
				if  (Double.isNaN(Y[i]))				throw new RuntimeException("NaN Y value at index: " + i);
			}
		}
		/******************************************************************
		 * Initialise les valeurs de yMin, yMax
		 ******************************************************************/
		private void initYSides()
		{
			// Determination de xMin et xMax
			int n		= X.length;
			double min = Math.min(Y[0], Y[n-1]);
			double max = Math.max(Y[0], Y[n-1]);

			for (int i = 0; i<interpolationPolynome.length; i++)
			{
				Polynomial p = interpolationPolynome[i];
				MinMax mm	 = p.MinMax(X[i], X[i+1]);
				if (mm == null) continue;
				if (mm.yMin < min) min = mm.yMin;
				if (mm.yMax > max) max = mm.yMax;
			}
/*			for (int i = 1; i<this.Y.length; i++)
			{
				if (Y[i] < yMin) yMin = Y[i];
				if (Y[i] > yMax) yMax = Y[i];
			}
*/
			this.yMin = min;
			this.yMax = max;
		}
		/*******************************************************************
		 * Tri les tab X et Y par X croissants
		 *******************************************************************/
		private void sortByX()
		{
			int lenth = X.length;
			boolean inv;
	
			do
			{
				inv = false;
				for(int i=0;i<lenth-1;i++)
				{
					if (X[i] == X[i+1]) throw new RuntimeException("Double value of x: " + X[i]);
					if (X[i] >  X[i+1])
					{
						exchangeTab(i,i+1);
						inv = true;
					}
				}
			}while(inv);
		}
		/******************************************************************
		 * Calcul les coeff du polynome d'interpolation et 
		 * rempli this.interpolationPolynome
		 ******************************************************************/
		private void initInterpolationPolynomes()
		{
			int deg					= interpolationDegres;
			int nbrGap				= X.length-1;
			double[][] A			= new double[deg+1][deg+1];
			double[]   y			= new double[deg+1];
			interpolationPolynome	= new Polynomial[nbrGap];

			// Calcul des coeef des polynomes
			for (int i = 0; i<nbrGap; i++)
			{
				initMat(i, A, y);
				double det = AuxMath.determinant(A);
				if (det == 0) throw new RuntimeException("The corresponding interpolation polynome does not exist: det = 0");
				interpolationPolynome[i] = new Polynomial(deg);
				for (int j = 0; j<=deg; j++)
				{
					double detAi = AuxMath.determinant(exchangeMat(A, y, j));
					interpolationPolynome[i].setCoeff(deg-j, detAi / det);
				}
			}
		}
		/******************************************************************
		 * Optimise le degres du polynome d'interpolation
		 ******************************************************************/
		private void optimizeInterpolationDegres()
		{
			int maxDegres = 0;

			for (int i=0; i<interpolationPolynome.length; i++)
			{
				Polynomial p = interpolationPolynome[i];
				if (maxDegres < p.getDegres()) maxDegres = p.getDegres();
			}
			interpolationDegres = maxDegres;
		}
		/*****************************************************************
		 * Initialise la matrice d'interpolation A de l'interval 
		 * index et la vecteur res y
		 *****************************************************************/
		private void initMat(int index, double[][] A, double[] y)
		{
			int deg	= interpolationDegres;
			int k	= 0;

			if (index >= (X.length-deg-1))	k = X.length-deg-1;
			else							k = index;
			for (int i = 0; i<=deg; i++)
			{
				double x	= X[k+i];
				double pow	= 1;
				for (int j = deg; j>=0; j--)
				{
					A[j][i]	= pow;
					pow		*= x;
				}
				y[i] = Y[k+i];
			}
		}
		/******************************************************************
		 *  Echange les valeurs de X et Y d'index i, j
		 ******************************************************************/
		private void exchangeTab(int i, int j)
		{
			double x0 = X[i];
			double y0 = Y[i];

			X[i]	= X[j];
			Y[i]	= Y[j];
			X[j]	= x0;
			Y[j]	= y0;
		}
		/******************************************************************
		 *  Remplace le ieme vecteur de mat par vect
		 ******************************************************************/
		private double[][] exchangeMat(double[][] mat, double[] vect, int i)
		{
			double[][] res = new double[mat.length][mat[0].length];

			for (int x = 0; x<i; x++)				for (int y = 0; y<mat[0].length; y++)res[x][y] = mat[x][y];
													for (int y = 0; y<mat[0].length; y++)res[i][y] = vect[y];
			for (int x = i+1; x<mat.length; x++)	for (int y = 0; y<mat[0].length; y++)res[x][y] = mat[x][y];
			return res;
		}
		/******************************************************************
		 * evalue la fonction en x en utilisant le polynome domainId
		 * Mis en oeuvre de l'algorithme de Horner
		 ******************************************************************/
		private double eval(double x, int domainId)
		{
			if ((domainId < 0) || (domainId >= interpolationPolynome.length)) throw new RuntimeException("Unknown domain ID: " + domainId);

			Polynomial poly	= this.interpolationPolynome[domainId];
			return poly.eval(x);
		}
}