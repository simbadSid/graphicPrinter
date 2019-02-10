package function;

import symbolicComputation.ExceptionVariableRepresentation;

import symbolicComputation.Variable;
import function.Polynomial.MinMax;
import auxiliaire.AuxGeneral;










public class LagrangeInterpolationFunction implements Function
{
//--------------------------------------------
//Attributs:
//--------------------------------------------
		private int				functionType		= InterpolationFunction;
		private String			function;			// Representation textuelle de l'expression formelle
		private String			domaine;			// Representation textuelle du domaine de definition
		private String			stringFunctionType	= "Continue function by Lagrangian interpolation";
		private double			xMin;
		private double			xMax;
		private double			yMin;
		private double			yMax;
		private double[]		X;
		private double[]		Y;
		private Polynomial		interpolationPolynomial;
		private String			variable			= "x";

//--------------------------------------------
//Constructeur:
//--------------------------------------------
		/************************************************************
		 * Cree un polynome de degres (nbrPoint - 1) par interpolation
		 * de tous les points donnes en entree
		 * Le polynome Cree est P(x) = Somme(Yi * Li(x))
		 *   avec Li le polynome de Lagrange
		 * @param functionName: expression formelle de la fonction (optionelle)
		 * @throws Runtime Exception si X contient des doublons, ou s'il  a 
		 * moins de 2 points
		 **************************************************************/
		public LagrangeInterpolationFunction(double[] X, double[] Y, String functionName) throws RuntimeException
		{
			this.X		= AuxGeneral.copyDoubleTab(X);
			this.Y		= AuxGeneral.copyDoubleTab(Y);
			checkData();
			sortByX();
			xMin = X[0];
			xMax = X[X.length-1];
			initInterpolationPolynomial();
			initYSides();
			if ((functionName == null) || (functionName.length()== 0))
				this.function	= "Unknown formel expression";
			else
				this.function	= "" + functionName;
			this.domaine		= "[" + xMin + " --> " + xMax + "]";
		}

//--------------------------------------------
//Accesseur:
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
		public String	getDomainRepresentation()	{return ""+domaine;}
		public String	getTypeRepresentation()		{return ""+stringFunctionType 	+ " of degres " + (X.length-1);}
		public int		getInterpolationDegres()	{return X.length-1;}
		public boolean	isDerivable()				{return true;}
 		public boolean	isVariableSetable()			{return true;}
 		public void		setVariable(String s) throws ExceptionVariableRepresentation
 		{
 			if (!Variable.isVariableName(s)) throw new ExceptionVariableRepresentation();
 			this.variable	= new String(s);
 		}
		public String toString()
		{
			return interpolationPolynomial.toString(variable);
		}

//--------------------------------------------
//Methodes principale:
//--------------------------------------------
		public boolean	equal (String representation)
		{
			return (representation.equals(this.function));
		}
		/************************************************************
		 * Rend l'image de x par le polynome d'interpolation de Lagrange
		 **************************************************************/
		public double eval(double x)
		{
			if ((x < getXMin()) || (x > getXMax())) throw new RuntimeException("X value is out of the definition domain: "+ x);

			return interpolationPolynomial.eval(x);
		}
		/****************************************************************
		 * Remplit les points X et Y de points equidistants
		 * appartenant au domaine domainId
		 ***************************************************************/
		public void eval(int domainId, Double[] X, Double[] Y)
		{
			if (domainId != 0)			throw new RuntimeException("unknown definition domain Id");
			if (X.length == 0) 			throw new RuntimeException("Umpty Parameter X: ");
			if (X.length == 0)			throw new RuntimeException("Uncompatibl arguments X, Y");

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
		public LagrangeInterpolationFunction derivate()
		{
			String function = "d( " + this.function + " ) / dx";
			LagrangeInterpolationFunction res = new LagrangeInterpolationFunction (X, X, function);
			Polynomial p = interpolationPolynomial.getDerivate();

			res.interpolationPolynomial = p;
			for (int i=0; i<X.length; i++) res.Y[i] = p.eval(X[i]);
			res.initYSides();
			return res;
		}
		/**************************************************************
		 * Determine si le point (x, y) aproche a gap appartient 
		 * a la fonction
		 **************************************************************/
		public boolean matchPoint(double x, double y, double gapX, double gapY)
		{
			if (gapY <= 0) throw new RuntimeException("negative gap: " + gapY);
			if (gapX <= 0) throw new RuntimeException("negative gap: " + gapY);
			double hx = gapX / 2.;
			double hy = gapY / 2.;

			if (x < X[0]-hx)				return false;
			if (x > X[X.length-1]+hx)		return false;
			double ny;
			if 		(x < xMin)	ny = eval(xMin);
			else if (x > xMax)	ny = eval(xMax);
			else				ny = eval(x);
			return ((ny >= y-hy) && (ny <= y+hy));
		}

//--------------------------------------------
//Methodes auxiliaires:
//--------------------------------------------
		/***************************************************************** 
		 * Verifie la presence de valeur infinie ou Nan dans X et Y
		 * Verifie le degres des polynomes d'interpolation
		 *****************************************************************/
		private void checkData()
		{
			if  (X.length != Y.length)	throw new RuntimeException("Uncompatibl arguments X, Y");
			if  (X.length == 0)			throw new RuntimeException("Umpty arguments X and Y");
			if  (X.length < 2)
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
					if (X[i] == X[i+1]) throw new RuntimeException("Double value of x: " + X[i] + "    at index: " + i);
					if (X[i] >  X[i+1])
					{
						exchangeTab(i,i+1);
						inv = true;
					}
				}
			}while(inv);
		}
		/*****************************************************************
		 * Initialise le polynome d'interpolation = Somme (yi * Lagrange i(Xi))
		 *****************************************************************/
		private void initInterpolationPolynomial()
		{
			int n = X.length;

			Polynomial expression = new Polynomial(0);
			for (int i=0; i<n; i++)
			{
				int k;
				if (i == 0)			k = 1;
				else				k = 0;
				double[]			l = {Y[i]};
				Polynomial lagrange	  = new Polynomial(l);
				for (int j=k; j<n; j++)
				{
					if (j == i) continue;
					l = new double[2];
					l[0] = -X[j] / (X[i] - X[j]);
					l[1] = 1.	/ (X[i] - X[j]);
					Polynomial p = new Polynomial(l);
					lagrange = Polynomial.multiply(lagrange, p);
				}
				expression = Polynomial.add(expression, lagrange);
			}
			this.interpolationPolynomial = expression;
		}
		/******************************************************************
		 * Initialise les valeurs de yMin, yMax
		 ******************************************************************/
		private void initYSides()
		{
/***** A faire			MinMax mm	= interpolationPolynomial.MinMax(xMin, xMax);
			this.yMin	= mm.yMin;
			this.yMax	= mm.yMax;
*/
			// Determination de xMin et xMax
			double yMin = Y[0];
			double yMax = Y[0];

			for (int i = 1; i<this.Y.length; i++)
			{
				if (Y[i] < yMin) yMin = Y[i];
				if (Y[i] > yMax) yMax = Y[i];
			}

			this.yMin = yMin;
			this.yMax = yMax;
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
}