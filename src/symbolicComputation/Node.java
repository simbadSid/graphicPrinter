package symbolicComputation;




class Node
{
// --------------------------------------
// Attributs
// --------------------------------------
	// Constantes de definition des types de noeud
	public static final int UnaryOperator		= 0;
	public static final int BinaryOperator		= 1;
	public static final int Constant			= 2;
	public static final int Variable			= 3;
	public static final int OpeningParenthesis	= 4;
	public static final int ClosingParenthesis	= 5;
	public static final int NbrNodeType		 	= 6;

	// Operation geres
	public static final String[] binaryOperator	= {"+", "-", "*", "/", "^", "%"};
	public static final String[] unaryOperator	= {"-", "sqrt", "sin", "cos", "tan", "arcsin", "arccos", "arctan", "exp", "ln", "abs"};

	private int 	type;
	private String	val;

// --------------------------------------
// Constructeur
//--------------------------------------
	/*=================================================================
	 * @param id: donne le type de noeud: Correspond aux attributs de 
	 * la classe ExpressionTree
	 ==================================================================*/
	public Node (int type, String val) throws RuntimeException
	{
		if ((type < 0) && (type >= NbrNodeType))
			throw new RuntimeException("Wrong Node Type!: "+ type);
		this.type	= type;
		this.val	= new String(val);
	}
	public Node(Node n)
	{
		this.type	= n.type;
		this.val	= new String(n.val);
	}

//--------------------------------------
// Identificateur
//--------------------------------------
	public String getVal()					{return new String(val);}
	public boolean isUnaryOperator()		{return (type == UnaryOperator);}
	public boolean isBinaryOperator()		{return (type == BinaryOperator);}
	public boolean isConstant()				{return (type == Constant);}
	public boolean isVariable()				{return (type == Variable);}
	public boolean isOpeningParenthesis()	{return (type == OpeningParenthesis);}
	public boolean isClosingParenthesis()	{return (type == ClosingParenthesis);}
	public boolean isMinus()				{return ((type == UnaryOperator) && 
													 (val.equals("-")));}

//--------------------------------------
// Methode Locale
//--------------------------------------
	public boolean equalConst(double c)
	{
		if (this.type != Constant)	return false;
		double c0 = Double.parseDouble(val);
		return (c == c0);
	}
	public double eval(Double d0, Double d1)
	{
		switch (type)
		{
			case Constant:			return Double.parseDouble(val);
			case Variable:			return d0;
			case BinaryOperator:	if (val.equals("+")) 		return (d0 + d1);
									if (val.equals("-")) 		return (d0 - d1);
									if (val.equals("*")) 		return (d0 * d1);
									if (val.equals("/")) 		return (d0 / d1);
									if (val.equals("%")) 		return (d0 % d1);
									if (val.equals("^")) 		return (Math.pow(d0, d1));
									else throw new RuntimeException("Unhandled Binary Operator: " + val);

			case UnaryOperator:		if (val.equals("-"))		return (-d0);
									if (val.equals("sqrt"))		return (Math.sqrt(d0));
									if (val.equals("sin"))		return (Math.sin(d0));
									if (val.equals("cos"))		return (Math.cos(d0));
									if (val.equals("tan"))		return (Math.tan(d0));
									if (val.equals("arcsin"))	return (Math.asin(d0));
									if (val.equals("arccos"))	return (Math.acos(d0));
									if (val.equals("arctan"))	return (Math.atan(d0));
									if (val.equals("exp"))		return (Math.exp(d0));
									if (val.equals("ln"))		return (Math.log(d0));
									if (val.equals("abs"))		return (Math.abs(d0));
									else throw new RuntimeException("Unhandled Binary Operator: " + val);

			default:				throw new RuntimeException("Non evaluable Node Type!");
		}
	}
	public double eval(String s0, String s1)
	{
		double d0	= Double.parseDouble(s0);
		double d1	= Double.parseDouble(s1);
		return eval(d0, d1);
	}
	public String toString()
	{
		String res = "\ntype : ";
		switch(type)
		{
			case UnaryOperator		: res += "UnaryOperator\t\t";			break;
			case BinaryOperator		: res += "BinaryOperator\t\t";			break;
			case Constant			: res += "Constant\t\t\t";				break;
			case Variable			: res += "Variable\t\t\t";				break;
			case OpeningParenthesis	: res += "OpeningParenthesis\t";		break;
			case ClosingParenthesis	: res += "ClosingParenthesis\t";		break;
			default 				: throw new RuntimeException("Wrong Node Type!: "+ type);
		}
		res += "\"" + val + "\"";
		return res;
	}
	public void setVal(String val)
	{
		this.val = new String(val);
	}
}