package function;

import symbolicComputation.Domain;
import symbolicComputation.ExceptionDomainRepresentation;
import symbolicComputation.ExceptionEmptyDomain;
import symbolicComputation.ExceptionFunctionRepresentation;
import symbolicComputation.ExceptionUnrecognizedExpression;
import symbolicComputation.ExceptionVariableRepresentation;
import symbolicComputation.FormalExpression;
import symbolicComputation.Variable;








public class ContinueFunction1Var implements Function
{
//--------------------------------------------
// Attributs:
//--------------------------------------------
		private int					functionType;
		private String				function;			// Representation textuelle de l'expression formelle
		private String				domain;				// Representation textuelle du domaine de definition
		private Variable			variable;
		private String				stringFunctionType;
		private FormalExpression	fe;					// Expression formelle de l'entree utilisateur
		private Domain[]			domainTab;
		private double				yMin				= -5;//******************************
		private double				yMax				= 5;//*******************************

//--------------------------------------------
//Constructeur:
//--------------------------------------------
		/*=============================================================
		 * Cree une fonction continue sur un intervale donnée a 
		 * partir de sa representation textuele
		 * @param function: Representation textuelle de la fonction avec x pour seule inconnue
		 * @param domaine: Representation textuelle du domaine
		 * @throws ExceptionDomainRepresentation 
		 * @throws ExceptionVariableRepresentation 
		 ==============================================================*/
		public ContinueFunction1Var(String function, String var, String domain) throws ExceptionFunctionRepresentation, ExceptionDomainRepresentation, ExceptionVariableRepresentation, ExceptionEmptyDomain
		{
			this.variable		= new Variable(var);
			Variable[] vari		= {variable};
			try {this.fe		= new FormalExpression(function, vari);}
			catch (ExceptionUnrecognizedExpression e) {throw new ExceptionFunctionRepresentation();}
			this.function		= fe.toString();
			this.domainTab		= Domain.parseDomain(domain);
			if (domainTab.length == 0)		throw new ExceptionDomainRepresentation();
			actualizeDomain(fe.getDomains()[0]);
			if (domainTab.length == 0)		throw new ExceptionEmptyDomain();
			sortByXmin();
			initDomainRepresentation();
			if (domainTab.length > 1)	stringFunctionType = "Function continue by pieces";
			else						stringFunctionType = "Continue function";
			this.functionType	= continueFunction1Var;
		}
		private ContinueFunction1Var(){}

//--------------------------------------------
//Accesseur:
//--------------------------------------------
		public String	getVariable()				{return new String(variable.name);}
		public double[]	getX()						{throw new RuntimeException("No input points!");}
		public double[]	getY()						{throw new RuntimeException("No input points!");}
		public double	getXMin()					{return domainTab[0].x0;}
		public double	getXMax()					{return domainTab[domainTab.length-1].x1;}
		public double	getYMin()					{return yMin;}
		public double	getYMax()					{return yMax;}
		public int		getNbrDomain()				{return domainTab.length;}
		public double	getDomainSize(int d)		{return (domainTab[d].x1 - domainTab[d].x0);}
		public int 		getFunctionType()			{return functionType;}
		public String	getFunctionRepresentation()	{return new String(function);}
		public String	getDomainRepresentation()	{return new String(domain);}
 		public String	getTypeRepresentation()		{return new String(stringFunctionType);}
 		public boolean	isDerivable()				{return true;}
 		public String	toString()					{return new String(function);}
 		public boolean	isVariableSetable()			{return true;}
 		public void		setVariable(String s) throws ExceptionVariableRepresentation
 		{
 			if (!Variable.isVariableName(s)) throw new ExceptionVariableRepresentation();
 			this.variable	= new Variable(s);
 			this.fe			.setVariableName(0, s);
 			this.function	= fe.toString();
 		}

//--------------------------------------------
//Methodes principale:
//--------------------------------------------
 		public boolean	equal (String representation)
 		{
 			return (representation.equals(this.function));
 		}
 		/*=============================================================
		 * Rend la valeur de y correspondant a x 
		 ==============================================================*/
		public double eval(double x)
		{
			if (!isInDomain(x)) throw new RuntimeException("Value out of definition domain:" + x);
			double[] var = {x};
			return fe.eval(var);
		}
		/*==============================================================
		 * Remplit les points X et Y de points equidistants
		 * appartenant au domaine domainId
		 ===============================================================*/
		public void eval(int domainId, Double[] X, Double[] Y)
		{
			Domain d		= domainTab[domainId];
			double x0		= domainTab[domainId].x0;
			double x1		= domainTab[domainId].x1;
			double dx		= (x1 - x0) / (X.length-1);
			boolean test	= false;

			if (!d.isX0In)	{x0 += dx;	test = true;}
			if (!d.isX1In)	{x1 -= dx;	test = true;}
			if (test)		dx = (x1 - x0) / (X.length-1);
			
			for (int i = 0; i<X.length; i++)
			{
				X[i] = x0 +(i * dx);
				double[] var = {X[i]};
				try					{Y[i] = fe.eval(var);}
				catch(Exception e)	{Y[i] = null;}
			}
		}
		/*=============================================================
		 * Returns The derivated function
		 ==============================================================*/
		public Function derivate() throws ExceptionEmptyDomain
		{
			ContinueFunction1Var res = new ContinueFunction1Var();
			res.functionType		= this.functionType;
			res.variable			= new Variable(this.variable);
			res.stringFunctionType	= new String(this.stringFunctionType);
			res.fe					= this.fe.derivate(0);
			res.function			= res.fe.toString();
			res.domain				= new String(this.domain);
			res.domainTab			= copyDomainTab();
			res.actualizeDomain(res.fe.getDomains()[0]);
			if (domainTab.length == 0)		throw new ExceptionEmptyDomain();

			return res;
		}
		/*=============================================================
		 * Determine si le point (x, y) aproche a gap appartient 
		 * a la fonction
		 ==============================================================*/
		public boolean matchPoint(double x, double y, double gapX, double gapY)
		{
			if (gapX <= 0) throw new RuntimeException("negative gapX: " + gapX);
			if (gapY <= 0) throw new RuntimeException("negative gapY: " + gapY);

			double hx = gapX / 2, hy = gapY / 2, ry;
			int i;
			for (i=0; i<domainTab.length; i++)
			{
				Domain d = domainTab[i];
				if  (x <  d.x0-hx)						return false;
				if ((x >  d.x0-hx) && (x < d.x1+hx))	break;
				if ((x == d.x0-hx) && (d.isX0In))		break;
				if ((x == d.x1+hx) && (d.isX1In))		break;				
			}
			if (i == domainTab.length)					return false;
			double[] var = {x};
			try					{ry = this.fe.eval(var);}
			catch (Exception e)	{return false;}
			return ((y > ry-hy) && (y < ry+hy));
		}

//--------------------------------------------
// Fonctions Auxiliaires:
//--------------------------------------------
		/*=================================================================
		 * Initialise la representation textuelle du domaine
		 ==================================================================*/
		private void initDomainRepresentation()
		{
			domain = domainTab[0].toString();
			for (int i=1; i<domainTab.length; i++)
			{
				domain	+= "  U  " + domainTab[i].toString();
			}
		}
		private void actualizeDomain(Domain[] d)
		{
/////////////////////// A faire
		}
		/*================================================================
		 * indique si la valeur x appartient au dommaine de definition
		 * de la fonction
		 =================================================================*/
		private boolean isInDomain(double x)
		{
			for (int i=0; i<domainTab.length; i++)
			{
				Domain d = domainTab[i];
				if (d.isInDomain(x)) return true;
			}
			return false;
		}
		private void sortByXmin()
		{
			boolean isSorted = false;
			while(!isSorted)
			{
				isSorted = true;
				for (int i=1; i<domainTab.length; i++)
				{
					if (domainTab[i-1].x0 > domainTab[i].x0)
					{
						echangeDomain(i, i-1);
						isSorted = false;
					}
				}			
			}
		}
		private void echangeDomain(int i, int j)
		{
			Domain tmp		= domainTab[i];
			domainTab[i]	= domainTab[j];
			domainTab[j]	= tmp;
		}
		private Domain[] copyDomainTab()
		{
			Domain[] res = new Domain[domainTab.length];

			for (int i=0; i<domainTab.length; i++)
			{
				res[i] = new Domain(domainTab[i]);
			}
			return res;
		}
}