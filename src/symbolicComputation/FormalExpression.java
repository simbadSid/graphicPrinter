package symbolicComputation;




public class FormalExpression
{
//--------------------------------------------
//Attributs:
//--------------------------------------------
	private ExpressionTree		ET;				// Arbre representant l'expression reguliere (arbre des precedences)
	private Variable[]			var;			// Ensemble des variables de l'expressions
	private Domain[][]			dom;			// Dommaine de chaque variable

//--------------------------------------------
//Constructeur:
//--------------------------------------------
	public FormalExpression (String expression, Variable[] variable) throws ExceptionUnrecognizedExpression
	{
		this.ET 		= new ExpressionTree(expression, variable);
		this.var		= copyVarTab(variable);
		this.dom		= new Domain[variable.length][];
		setDomain();
	}
	public FormalExpression()
	{
		this.ET			= null;
		this.var		= null;
		this.dom		= null;
	}

//--------------------------------------------
// Methode Locale:
//--------------------------------------------
	/**===========================================================================
	 * Evalue l'expression pour les valeurs des variables donnes dans values
	 =============================================================================*/
	public double eval(double[] variableValue)
	{
		return eval(ET, variableValue);
	}
	/**===========================================================================
	 * Rend l'arbre derive de t par rapport a la variable var
	 =============================================================================*/
	public FormalExpression	derivate(int varId)
	{
		String var				= this.var[varId].name;
		FormalExpression res	= new FormalExpression();
		res.var					= copyVarTab(this.var);
		res.dom					= copyDomMat(this.dom);
		if (!dependsOnVar(var))
			res.ET				= new ExpressionTree(0);
		else
		{
			res.ET				= derivateTree(ET, var);
			res.ET				= res.ET.getReduceTree();
		}
		res.setDomain();
		return res;
	}
	/**==========================================================================
	 * Change le nom de la variable lastName
	 * @throws ExceptionVariableRepresentation
	 ===========================================================================*/
	public void setVariableName(int varId, String name) throws ExceptionVariableRepresentation
	{
		String n = var[varId].name;
		var[varId]	= new Variable(name);
		setVariable(ET, n, name);
	}
	/**==========================================================================
	 * Indique si la fonction depend de la variable var
	 ============================================================================*/
	public boolean dependsOnVar(String var)
	{
		if(this.getVarIndex(var) == -1) return false;
		return dependsOnVar(this.ET, var);
	}
	public Variable[] getVariables()
	{
		return copyVarTab(var);
	}
	public Domain[][] getDomains()
	{
		return copyDomMat(dom);
	}
	public String toString()
	{
		return ET.toString();
	}

//--------------------------------------------
// Methode de parcour de l'arbre:
//--------------------------------------------
	/**==========================================================================
	 * Change le nom de la variable lastName
	 ===========================================================================**/
	private void setVariable(ExpressionTree t, String lastName, String newName)
	{
		if (t == null) return;
		Node n = t.getNode();

		if ((n.isVariable()) && (n.getVal().equals(lastName))) n.setVal(newName);
		setVariable(t.getLeftTree(), lastName, newName);
		setVariable(t.getRightTree(), lastName, newName);
	}
	/**===========================================================================
	 * Evalue l'expression pour les valeurs des variables donnes dans values
	 =============================================================================*/
	private double eval(ExpressionTree t, double[] values)
	{
		Node n 				= t.getNode();
		ExpressionTree LS	= t.getLeftTree();
		ExpressionTree RS	= t.getRightTree();
		String val			= n.getVal();
		int valIndex;

		if (n.isVariable())
		{
			valIndex = getVarIndex(val);
			if (valIndex != -1)			return values[valIndex];
			throw new RuntimeException("No numercial value for the variable "+val);
		}
		if (n.isConstant())				return Double.parseDouble(val);
		if (n.isBinaryOperator())		return n.eval(eval(LS, values), eval(RS, values));
		if (n.isUnaryOperator())		return n.eval(eval(LS, values), null);
		throw new RuntimeException("Unhandled node type: " + n);
	}
	/**===========================================================================
	 * Rend l'arbre derive de t par rapport a la variable var
	 =============================================================================*/
	private ExpressionTree derivateTree(ExpressionTree t, String var)
	{
		Node n = t.getNode();
		ExpressionTree res;

		if (n.isConstant())				res = new ExpressionTree(0);			// Cas d'une constante
		else if (n.isVariable())														// Cas d'une variable
		{
			if (n.getVal().equals(var))	res = new ExpressionTree(1);			//		Variable de derivation
			else						res = new ExpressionTree(0);			// 		Autre variable
		}
		else if (n.isUnaryOperator())	res = derivateUnaryOperator(t, var);	// Cas d'un operateur unaire
		else if (n.isBinaryOperator())	res = derivateBinaryOperator(t, var);	// Cas d'un operateur binaire
		else throw new RuntimeException("Unhandeld node type!");

		return res;
	}
	private void setDomain()
	{
/////////////////////// A faire
	}

	/**===========================================================================
	 * Indique si l'arbre t depend de la variable var
	 =============================================================================*/
	private boolean dependsOnVar(ExpressionTree t, String var)
	{
		Node n = t.getNode();
		if (n.isConstant())				return false;
		if (n.isVariable())
		{
			if(n.getVal().equals(var))	return true;
			else						return false;
		}
		if (n.isUnaryOperator())		return (dependsOnVar(t.getLeftTree(), var));
		if (n.isBinaryOperator())		return (dependsOnVar(t.getLeftTree(), var) || dependsOnVar(t.getRightTree(), var));
		else throw new RuntimeException("Unhandeld node type!");
	}

//--------------------------------------------
// Methode Auxiliaire:
//--------------------------------------------
	/*============================================================
	 * Derive d'une fonction binaire
	 =============================================================*/
	private ExpressionTree derivateBinaryOperator(ExpressionTree t, String var)
	{
		Node n				= t.getNode();
		if (!n.isBinaryOperator()) throw new RuntimeException("Non binary Operator!");
		ExpressionTree LS	= t.getLeftTree();
		ExpressionTree RS	= t.getRightTree();
		String val			= n.getVal();
		ExpressionTree lt, rt, lpt, rpt;
		Node nl				= LS.getNode();
		boolean test1		= !dependsOnVar(t.getLeftTree(), var);
		boolean test2		= !dependsOnVar(t.getRightTree(), var);

		if ((val.equals("+")) || (val.equals("-")))								// 			Operateur + ou *
		{
			lpt	= derivateTree(LS, var);
			rpt	= derivateTree(RS, var);
			return new ExpressionTree(n, lpt, rpt);
		}
		if (val.equals("*"))													// 			Operateur "*"
		{
			if (test1 && test2)		return new ExpressionTree(0);
			if (test1)				return new ExpressionTree(nMult, LS, derivateTree(RS, var));
			if (test2)				return new ExpressionTree(nMult, derivateTree(LS, var), RS);
			else
			{
				lpt	= derivateTree(LS, var);
				rpt	= derivateTree(RS, var);
				lt	= new ExpressionTree(nMult, lpt, RS);
				rt	= new ExpressionTree(nMult, LS,  rpt);
				return new ExpressionTree(nPlus, lt, rt);
			}
		}
		if (val.equals("/"))													// 			Operateur "/"
		{
			ExpressionTree r2t = new ExpressionTree(nPow, RS, new ExpressionTree(2));
			if (test1 && test2)		return new ExpressionTree(0);				//				a / b
			if (test1)															//				a / f(x)
			{
				rpt	= derivateTree(RS, var);
				lt	= new ExpressionTree(nMult, LS, rpt);
				lt	= new ExpressionTree(nMoinUnaire, lt, null);
				return new ExpressionTree(nDiv, lt, r2t);
			}
			if (test2)															//				f(x) / a
			{
				lpt	= derivateTree(LS, var);
				lt	= new ExpressionTree(nMult, lpt, RS);
				return new ExpressionTree(nDiv, lt, r2t);
			}
			else																//				f(x) / g(x)
			{
				lpt	= derivateTree(LS, var);
				rpt	= derivateTree(RS, var);
				lt	= new ExpressionTree(nMult, lpt, RS);
				rt	= new ExpressionTree(nMult, LS, rpt);
				lt	= new ExpressionTree(nMoin, lt, rt);
				return new ExpressionTree(nDiv, lt, r2t);
			}
		}
		if (val.equals("^"))													// 			Operateur ^
		{
			if (test1 && test2)		return new ExpressionTree(0);
			if (test1)															//			a ^ f(x)
			{
				rpt					= derivateTree(RS, var);
				ExpressionTree lnt	= new ExpressionTree(nLn, LS, null);
				rt					= new ExpressionTree(nMult, lnt, rpt);
				return new ExpressionTree(nMult, t, rt);
			}
			if (test2)															//			f(x) ^ a
			{
				double a	= Double.parseDouble(RS.getNode().getVal());
				lt			= new ExpressionTree(nPow, LS, new ExpressionTree(a-1));
				lt			= new ExpressionTree(nMult, new ExpressionTree(a), lt);
				if (nl.isVariable())	return lt;
				lpt			= derivateTree(LS, var);
				return new ExpressionTree(nMult, lt, lpt);
			}
			else																//			f(x) ^ g(x)
			{
				rt	= new ExpressionTree(nLn, LS, null);
				rt	= new ExpressionTree(nMult, RS, rt);
				rpt	= derivateTree(rt, var);
				return new ExpressionTree(nMult, t, rpt);
			}
		}
		if (val.equals("%"))													//			Operateur %
		{
///////////////////////////
			throw new RuntimeException("A faire***************");
		}
		else throw new RuntimeException("Unhandeld binary operator!");
	}
	/*=============================================================
	 * Derive d'une fonction uinaire
	 ==============================================================*/
	private ExpressionTree derivateUnaryOperator(ExpressionTree t, String var)
	{
		Node n				= t.getNode();
		if (!n.isUnaryOperator()) throw new RuntimeException("Non binary Operator!");
		ExpressionTree LS	= t.getLeftTree();
		Node nl				= LS.getNode();
		boolean test1		= !dependsOnVar(t.getLeftTree(), var);
		String val			= n.getVal();
		ExpressionTree lt, rt;

		if (test1)				return new ExpressionTree(0);
		if (val.equals("-"))	return new ExpressionTree(n, derivateTree(LS, var), null);
		if (val.equals("sqrt"))
		{
			lt	= new ExpressionTree(nSqrt, LS, null);
			lt	= new ExpressionTree(nMult, new ExpressionTree(2), lt);
			lt	= new ExpressionTree(nDiv, new ExpressionTree(1), lt);
		}
		else if (val.equals("sin"))
		{
			lt	= new ExpressionTree(nCos, LS, null);
		}
		else if (val.equals("cos"))
		{
			lt	= new ExpressionTree(nSin, LS, null);
			lt	= new ExpressionTree(nMoinUnaire, lt, null);
		}
		else if (val.equals("tan"))
		{
			lt	= new ExpressionTree(nPow, t, new ExpressionTree(2));
			lt	= new ExpressionTree(nPlus, new ExpressionTree(1), lt);
		}
		else if (val.equals("arcsin"))
		{
			lt	= new ExpressionTree(nPow, LS, new ExpressionTree(2));
			lt	= new ExpressionTree(nMoin, new ExpressionTree(1), lt);
			lt	= new ExpressionTree(nSqrt, lt, null);
			lt	= new ExpressionTree(nDiv, new ExpressionTree(1), lt);
		}
		else if (val.equals("arccos"))
		{
			lt	= new ExpressionTree(nPow, LS, new ExpressionTree(2));
			lt	= new ExpressionTree(nMoin, new ExpressionTree(1), lt);
			lt	= new ExpressionTree(nSqrt, lt, null);
			lt	= new ExpressionTree(nDiv, new ExpressionTree(-1), lt);
		}
		else if (val.equals("arctan"))
		{
			lt	= new ExpressionTree(nPow, LS, new ExpressionTree(2));
			lt	= new ExpressionTree(nPlus, new ExpressionTree(1), lt);
			lt	= new ExpressionTree(nDiv, new ExpressionTree(1), lt);
		}
		else if (val.equals("exp"))
		{
			lt	= new ExpressionTree(t);
		}
		else if (val.equals("ln"))
		{
			lt	= new ExpressionTree(nDiv, new ExpressionTree(1), LS);
		}
		else if (val.equals("abs"))
		{
			lt	= new ExpressionTree(nDiv, t, LS);
		}
		else throw new RuntimeException("Unhandeld unary operator!");
		if (nl.isVariable())	return lt;
		rt = derivateTree(LS, var);
		return new ExpressionTree(nMult, lt, rt);
	}
//--------------------------------------------
// Methode Auxiliaire:
//--------------------------------------------
	// Determine l'index de var dans this.variable
	// Rend -1 si var n'appartient pas a this.variable
	private int getVarIndex(String v)
	{
		for (int i=0; i<this.var.length; i++)
		{
			if (this.var[i].equals(v)) return i;
		}
		return -1;
	}
	private Variable[] copyVarTab(Variable[] variable)
	{
		if (variable == null) return null;
		Variable[] res = new Variable[variable.length];
		for (int i=0; i<variable.length; i++)
		{
			res[i] = new Variable(variable[i]);
		}
		return res;
	}
	private Domain[][] copyDomMat(Domain[][] domain)
	{
		if (domain == null) return null;
		Domain[][] res = new Domain[domain.length][];
		for (int i=0; i<domain.length; i++)
		{
			res[i] = copyDomTab(domain[i]);
		}
		return res;
	}
	private Domain[] copyDomTab(Domain[] domain)
	{
		if (domain == null) return null;
		Domain[] res = new Domain[domain.length];
		for (int i=0; i<domain.length; i++)
		{
			res[i] = new Domain(domain[i]);
		}
		return res;
	}

// -------------------------------------
// Node
// -------------------------------------
	private final Node nPlus		= new Node(Node.BinaryOperator, "+");
	private final Node nMoin		= new Node(Node.BinaryOperator, "-");
	private final Node nMult		= new Node(Node.BinaryOperator, "*");
	private final Node nDiv			= new Node(Node.BinaryOperator, "/");
	private final Node nPow			= new Node(Node.BinaryOperator, "^");
	private final Node nMoinUnaire	= new Node(Node.UnaryOperator, "-");
	private final Node nLn			= new Node(Node.UnaryOperator, "ln");
	private final Node nSqrt		= new Node(Node.UnaryOperator, "sqrt");
	private final Node nCos			= new Node(Node.UnaryOperator, "cos");
	private final Node nSin			= new Node(Node.UnaryOperator, "sin");

}