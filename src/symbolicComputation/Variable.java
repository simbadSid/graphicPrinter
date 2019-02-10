package symbolicComputation;





public class Variable
{
// -------------------------------
// Attributs
//-------------------------------
	public String name;

// -------------------------------
// Constructeur
// -------------------------------
	public Variable(String var) throws ExceptionVariableRepresentation
	{
		name = "";
		int i = 0;

		while ((i < var.length()) && (var.charAt(i) == ' ')) i++;
		if (i == var.length())			throw new ExceptionVariableRepresentation();
		if (isNumerical(var.charAt(i)))	throw new ExceptionVariableRepresentation();
		name += var.charAt(i);
		i++;
		while ((i < var.length()) &&
				((isAlphabet(var.charAt(i))) || (isNumerical(var.charAt(i)))))
		{
			name += var.charAt(i);
			i++;
		}
		for (int j=i; j<var.length(); j++)
		{
			if (var.charAt(j) != ' ') throw new ExceptionVariableRepresentation();
		}
	}
	public Variable(Variable v)
	{
		this.name	= new String(v.name);
	}

// -------------------------------
// Methodes Locales
// -------------------------------
	public static boolean isVariableName(String var)
	{
		try{new Variable(var);}
		catch (Exception e){return false;}
		return true;
	}
	public boolean equals(Variable var)
	{
		return this.name.equals(var.name);
	}
	public boolean equals(String var)
	{
		return this.name.equals(var);
	}

// -------------------------------
// Methodes Auxiliaires
// -------------------------------
	private boolean isNumerical(char c)
	{
		return ((c >= '0') && (c <= '9'));
	}
	private boolean isAlphabet(char c)
	{
		return (((c >= 'a') && (c <= 'z')) ||
				((c >= 'A') && (c <= 'Z')));
	}
}