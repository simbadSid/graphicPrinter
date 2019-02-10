package function;

import auxiliaire.AuxGeneral;






public class PointFunction implements Function
{
//--------------------------------------------
// Attributs:
//--------------------------------------------
		private int			functionType;
		private String		function;			// Representation textuelle de l'expression formelle
		private String		domaine;			// Representation textuelle du domaine de definition
		private String		stringFunctionType	= "Ponctual function";
		private double		xMin;
		private double		xMax;
		private double		yMin;
		private double		yMax;
		private double[]	X;
		private double[]	Y;

//--------------------------------------------
// Constructeur:
//--------------------------------------------
		/************************************************************
		 * Cree une fonction definie uniquement sur les points X de valeurs Y
		 * Les doublons de X sont acceptes
		 * @param functionName: expression formelle de la fonction (optionelle)
		 **************************************************************/
		public PointFunction(double[] X, double[] Y, String functionName) throws RuntimeException
		{
			this.functionType	= pointFunction;
			this.X				= AuxGeneral.copyDoubleTab(X);
			this.Y				= AuxGeneral.copyDoubleTab(Y);
			checkData();
			sortByX();
			initSides();
			if ((functionName == null) || (functionName.length()== 0))
				this.function	= "Unknown formel expression";
			else
				this.function	= "" + functionName;
			this.domaine		= "[" + xMin + " --> " + xMax + "]";
		}

//--------------------------------------------
// Accesseur:
//--------------------------------------------
		public String	getVariable()				{throw new RuntimeException("No continue domain defined!");}
		public double[]	getX()						{return AuxGeneral.copyDoubleTab(X);}
		public double[]	getY()						{return AuxGeneral.copyDoubleTab(Y);}
		public double	getXMin()					{return xMin;}
		public double	getXMax()					{return xMax;}
		public double	getYMin()					{return yMin;}
		public double	getYMax()					{return yMax;}
		public int		getNbrDomain()				{throw new RuntimeException("No continue domain defined!");}
		public double	getDomainSize(int domain)	{throw new RuntimeException("No continue domain defined!");}
		public int 		getFunctionType()			{return functionType;}
		public String	getFunctionRepresentation()	{return ""+function;}
		public String	getDomainRepresentation()	{return ""+domaine;}
		public String	getTypeRepresentation()		{return ""+stringFunctionType;}
		public boolean	isDerivable()				{return false;}
 		public boolean	isVariableSetable()			{return false;}
		public void		setVariable(String s) throws ExceptionNoVariableAccepted
 		{
 			throw new ExceptionNoVariableAccepted();
 		}

//--------------------------------------------
// Methodes principale:
//--------------------------------------------
 		public boolean	equal (String representation)
 		{
 			return (representation.equals(this.function));
 		}
		/************************************************************
		 * Rend la valeur de y correspondant a x
		 * @throws RuntimeException: Si le point d'abscisse x n'a pas
		 *  ete renseigne a la creation
		 ************************************************************/
		public double eval(double x)
		{
			for (int i = 0; i<X.length; i++)
			{
				if (x == X[i]) return Y[i];
			}
			throw new RuntimeException("Unknown x value!");	
		}
		public void eval(int domainId, Double[] X, Double[] Y)
		{
			throw new RuntimeException("No continue domain defined!");
		}
		/**************************************************************
		 * Returns The derivated function
		 **************************************************************/
		public PointFunction derivate()
		{
			throw new RuntimeException("The point Function is not derivable (not continue)");
		}
		/**************************************************************
		 * Determine si le point (x, y) aproche a gap appartient 
		 * a la fonction
		 **************************************************************/
		public boolean matchPoint(double x, double y, double gapX, double gapY)
		{
			if ((gapX <= 0) || (gapY <= 0)) throw new RuntimeException("negative gap");
			double hx = gapX / 2.;
			double hy = gapY / 2.;

			if (x < X[0]-hx)								return false;
			if (x > X[X.length-1]+hx)						return false;
			for (int i = 0; i<X.length; i++)
			{
				if ((x >= X[i]-hx) && (x <= X[i]+hx) &&
					(y >= Y[i]-hy) && (y <= Y[i]+hy))		return true;
				if (x < X[i]-hx)							break;
			}
			return false;
		}

//--------------------------------------------
// Methodes auxiliaires:
//--------------------------------------------
		/***************************************************************** 
		 * Verifie la presence de valeur infinie ou Nan dans X et Y
		 *****************************************************************/
		private void checkData()
		{
			if (X.length != Y.length)	throw new RuntimeException("Uncompatibl arguments X, Y");
			if (X.length == 0)			throw new RuntimeException("Umpty arguments X and Y");
	
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
		 * Initialise les valeurs de xMin, xMax, yMin, yMax
		 ******************************************************************/
		private void initSides()
		{
			// Determination de xMin et xMax
			xMin = X[0];
			xMax = X[X.length-1];
			yMin = Y[0];
			yMax = Y[0];

			for (int i = 1; i<Y.length; i++)
			{
				if (Y[i] < yMin) yMin = Y[i];
				if (Y[i] > yMax) yMax = Y[i];
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
					if(X[i] > X [i+1])
					{
						exchange(i,i+1);
						inv = true;
					}
				}
			}while(inv);
		}
		/******************************************************************
		 *  Echange les valeurs de X et Y d'index i, j
		 ******************************************************************/
		private void exchange(int i, int j)
		{
			double x0 = X[i];
			double y0 = Y[i];

			X[i]	= X[j];
			Y[i]	= Y[j];
			X[j]	= x0;
			Y[j]	= y0;
		}
}