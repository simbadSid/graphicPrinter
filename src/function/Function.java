package function;

import symbolicComputation.ExceptionEmptyDomain;
import symbolicComputation.ExceptionVariableRepresentation;










public interface Function
{
//--------------------------------------------
//Attributs:
//--------------------------------------------
		// Identifiants des types de fonctions
		public static final int			pointFunction				= 0;
		public static final int			continueFunction1Var		= 1;
		public static final int			InterpolationFunction		= 2;
		public static final int			PiecesOfPolynomialFunction	= 3;
		public static final String[]	functionTypes			= {"Point Function", 
																   "Continue Function", 
																   "InterpolationFunction",
																   "PiecesOfPolynomialFunction"};

//--------------------------------------------
// Accesseur:
//--------------------------------------------
		public String	getVariable();
		public double[]	getX();
		public double[]	getY();
		public double	getXMin();
		public double	getXMax();
		public double	getYMin();
		public double	getYMax();
		public int		getNbrDomain();
		public double	getDomainSize(int domain);
		public String	getTypeRepresentation();
		public int 		getFunctionType();
		public String	getFunctionRepresentation();
		public String	getDomainRepresentation();
		public boolean	isDerivable();
		public void		setVariable(String s) throws ExceptionVariableRepresentation, ExceptionNoVariableAccepted;
		public boolean	isVariableSetable();

//--------------------------------------------
// Methodes principale:
//--------------------------------------------
		public boolean	equal		(String representation);
		public double	eval		(double x);
		public void		eval		(int domainId, Double[] X, Double[] Y);
		public Function	derivate	() throws ExceptionEmptyDomain;
		public boolean	matchPoint	(double x, double y, double gapX, double gapY);
}