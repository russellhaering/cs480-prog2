//
//	parser skeleton, CS 480, Winter 2006
//	written by Tim Budd
//		modified by:
//

public class Parser {
	private final Lexer lex;
	private final boolean debug;

	public Parser (Lexer l, boolean d) { lex = l; debug = d; }

	public void parse () throws ParseException {
		lex.nextLex();
		program();
		if (lex.tokenCategory() != lex.endOfInput)
			parseError(3); // expecting end of file
	}

	private final void start (String n) {
		if (debug) System.out.println("start " + n + " token: " + lex.tokenText());
	}

	private final void stop (String n) {
		if (debug) System.out.println("recognized " + n + " token: " + lex.tokenText());
	}

	private void parseError(int number) throws ParseException {
		throw new ParseException(number);
	}

	private void program () throws ParseException {
		start("program");

		while (lex.tokenCategory() != Lexer.endOfInput) {
			declaration();
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}        
		stop("program");
	}

	private void declaration() throws ParseException 
	{
		start("declaration");
		
		if(isClassDeclaration())
			classDeclaration();
		else if(isNonClassDeclaration())
			nonClassDeclaration();
		else 
			throw new ParseException(26);
		
		stop("declaration");
	}
	
	private void nonClassDeclaration() throws ParseException 
	{
		start("nonClassDeclaration");
		
		if(isFunctionDeclaration())
			functionDeclaration();
		else if(isNonFunctionDeclaration())
			nonFunctionDeclaration();
		else
			throw new ParseException(26);
		
		stop("nonClassDeclaration");
	}
	
	private void nonFunctionDeclaration() throws ParseException
	{
		start("nonFunctionDeclaration");
		
		if(isVariableDeclaration())
			variableDeclaration();
		else if(isConstantDeclaration())
			constantDeclaration();
		else if(isTypeDeclaration())
			typeDeclaration();
		else 
			throw new ParseException(26);
		
		stop("nonFunctionDeclaration");
	}
	
	private void constantDeclaration() throws ParseException
	{
		start("constant");
		if(lex.match("const"))
			lex.nextLex();
		else
			throw new ParseException(32);
		
		if(lex.isIdentifier())
			lex.nextLex();
		else
			throw new ParseException(27);
		
		if(lex.match("="))
			lex.nextLex();
		else
			throw new ParseException(20);
		
		if(isConstant())
			lex.nextLex();
		else
			throw new ParseException(20);
		
		stop("constant");
	}
	
	private void typeDeclaration() throws ParseException
	{
		start("typeDeclaration");
		if(lex.match("type"))
			lex.nextLex();
		else
			throw new ParseException(14);	
		
		nameDeclaration();
		
		stop("typeDeclaration");
	}

	private void variableDeclaration() throws ParseException
	{
		start("variableDeclaration");
		if(lex.match("var"))
			lex.nextLex();
		else
			throw new ParseException(15);	
			
		nameDeclaration();
		
		stop("variableDeclaration");
	}
	
	private void nameDeclaration() throws ParseException
	{
		start("nameDeclaration");
		
		if(lex.isIdentifier())
			lex.nextLex();
		else
			throw new ParseException(32);
		
		if(lex.match(":"))
			lex.nextLex();
		else 
			throw new ParseException(19);
		
		type();
		
		stop("nameDeclaration");
	}
	
	private void classDeclaration() throws ParseException
	{
		start("classDeclaration");
		
		if(lex.match("class"))
			lex.nextLex();
		else 
			throw new ParseException(5);
		
		if(lex.isIdentifier())
			lex.nextLex();
		else
			throw new ParseException(27);

		classBody();
		
		stop("classDeclaration");
	}
	
	private void classBody() throws ParseException
	{
		start("classBody");
		
		if(lex.match("begin"))
			lex.nextLex();
		else 
			throw new ParseException(4);
		
		while (!lex.match("end") && lex.tokenCategory() != 7) 
		{
			nonClassDeclaration();
			if(!lex.match(";"))
				throw new ParseException(18);
			else
				lex.nextLex();
		}
		if(!lex.match("end"))
			throw new ParseException(8);
		lex.nextLex();
		
		stop("classBody");
	}

	
	private void functionDeclaration() throws ParseException
	{
		start("functionDeclaration");
		
		if(lex.match("function"))
			lex.nextLex();
		else
			throw new ParseException(10);
		
		if(lex.isIdentifier())
			lex.nextLex();
		else 
			throw new ParseException(27);
		
		arguments();
		returnType();
		functionBody();

		stop("functionDeclaration");
	}
	
	private void arguments() throws ParseException
	{
		start("arguments");
		
		if(lex.match("("))
			lex.nextLex();
		else
			throw new ParseException(21);
		
		argumentList();
		
		if(lex.match(")"))
			lex.nextLex();
		else
			throw new ParseException(22);
		
		stop("arguments");
	}

	private void argumentList() throws ParseException
	{
		start("argumentList");
	
		if(!lex.match(")"))
		{
			nameDeclaration();
			while(lex.match(",") && !lex.match(")"))
			{
				lex.nextLex();
				nameDeclaration();
			}
		}
		
		stop("argumentList");
	}
	
	private void returnType() throws ParseException
	{
		start("returnType");
		if(lex.match(":"))
		{
			lex.nextLex();
			type();
		}
		stop("returnType");
	}

	private void type() throws ParseException
	{
		start("type");
	
		if(lex.isIdentifier())
		{
			lex.nextLex();
		}
		else if(lex.match("^"))
		{
			lex.nextLex();
			type();
		}
		else if(lex.match("["))
		{
			lex.nextLex();
			if(lex.tokenCategory() != 3)
				throw new ParseException(32);
			lex.nextLex();
			if(!lex.match(":"))
				throw new ParseException(19);
			lex.nextLex();
			if(lex.tokenCategory() != 3)
				throw new ParseException(32);
			lex.nextLex();
			if(!lex.match("]"))
				throw new ParseException(24);
			lex.nextLex();
			type();
		}
		else
			throw new ParseException(30);

		stop("type");
	}	
	
	private void functionBody() throws ParseException
	{
		start("functionBody");
		
		while(isNonClassDeclaration())
		{
			nonClassDeclaration();
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		} 
		
		compoundStatement();
		
		stop("functionBody");
	}
	
	private void compoundStatement() throws ParseException
	{
		start("compoundStatement");
		
		if(lex.match("begin"))
			lex.nextLex();
		else 
			throw new ParseException(4);
		
		while (isStatement()) {
			statement();
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}    
		
		if(lex.match("end"))
			lex.nextLex();
		else 
			throw new ParseException(8);
			
		stop("compoundStatement");
	}
	
	private void statement() throws ParseException
	{
		start("statement");
		if(isReturnStatement())
			returnStatement();
		else if(isIfStatement())
			ifStatement();
		else if(isWhileStatement())
			whileStatement();
		else if(isCompoundStatement())
			compoundStatement();
		else if(isAssignOrFunction())
			assignOrFunction();
		else
			throw new ParseException(34);
		
		stop("statement");
	}
	
	private void returnStatement() throws ParseException
	{
		start("returnStatement");
		
		if(lex.match("return"))
			lex.nextLex();
		else 
			throw new ParseException(12);
		
		if (lex.match("(")) {
			lex.nextLex();
			expression();
			if (lex.match(")"))
				lex.nextLex();
			else
				throw new ParseException(22);
		}    
	
		stop("returnStatement");
	}
	
	private void ifStatement() throws ParseException
	{
		start("ifStatement");
		
		if(lex.match("if"))
			lex.nextLex();
		else 
			throw new ParseException(11);
		
		expression();
		
		if (lex.match("then")) 
				lex.nextLex();
		else
			throw new ParseException(13);
		
		statement();
		
		if(lex.match("else"))			// Optional
		{
			lex.nextLex();
			statement();
		}
		
		stop("ifStatement");
	}
	
	private boolean whileStatement() throws ParseException
	{
		start("whileStatement");
		
		if(lex.match("while"))
			lex.nextLex();
		else 
			throw new ParseException(16);
		
		expression();
		
		if (lex.match("do")) 
				lex.nextLex();
		else
			throw new ParseException(7);
		
		statement();
		
		stop("whileStatement");
		return true;
	}
	
	private boolean assignOrFunction() throws ParseException
	{
		start("assignOrFunction");
		
		reference();
		
		if(lex.match("="))		// Expression
		{
			lex.nextLex();
			expression();
		}
		else if(lex.match("(")) // ParameterList
		{
			lex.nextLex();
			parameterList();
			if(!lex.match(")"))
					throw new ParseException(22);
			lex.nextLex();
		}
		else
			throw new ParseException(37);
		
		stop("assignOrFunction");
		return true;
	}
	
	private boolean parameterList() throws ParseException
	{
		start("parameterList");
		
		while(isExpression())
		{
			expression();
			if(lex.match(")") || lex.match(";"))	// end of expression list
				break;
			else if(lex.match(","))
				lex.nextLex();	// keep going
			else				// error
				throw new ParseException(22);
		}
		
		stop("parameterList");
		return true;
	}
	
	private void expression() throws ParseException
	{
		start("expression");
		
		relExpression();
		while(isLogicalOperator()) // logical operators
		{
			lex.nextLex();
			relExpression();
		}
		
		stop("expression");
	}

	private void relExpression() throws ParseException
	{
		start("relExpression");
		
		plusExpression();
		
		if(isRelationalOperator()) // Looking for relationalOperator
		{
			lex.nextLex();
			plusExpression();
		}
		stop("relExpression");
	}
	
	private void plusExpression() throws ParseException
	{
		start("plusExpression");
		
		timesExpression();
		
		while(isAdditionOperator()) // Looking for additionOperator
		{
			lex.nextLex();
			timesExpression();
		}
		
		stop("plusExpression");
	}
	
	private void timesExpression() throws ParseException
	{
		start("timesExpression");
		
		term();
		
		while(isMultiplicationOperator()) // multiplicationOperator
		{
			lex.nextLex();
			term();
		}
		
		stop("timesExpression");
	}
	
	private void term() throws ParseException
	{
		start("term");
	
		if(lex.match("("))
		{
			lex.nextLex();
			expression();
			if(!lex.match(")"))
				throw new ParseException(22);
			else
				lex.nextLex();
		}
		else if(lex.match("not") || lex.match("-"))
		{
			lex.nextLex();
			term();
		}
		else if(lex.match("new"))
		{
			lex.nextLex();
			type();
		}
		else if(isReference())
		{
			reference();
			if(lex.match("("))
			{
				lex.nextLex();
				parameterList();
				if(lex.match(")"))
					lex.nextLex();
				else
					throw new ParseException(22);
			}
		}
		else if(lex.match("&"))
		{
			lex.nextLex();
			reference();
		}
		else if(isConstant())
		{
			lex.nextLex();
		}
		else
			throw new ParseException(33);
				
		stop("term");
	}
	
	private boolean reference() throws ParseException
	{
		start("reference");
	
		if(!isReference())
			throw new ParseException(27);
		
		lex.nextLex();	
		
		if(lex.match("."))
		{
			lex.nextLex();
			if(!lex.isIdentifier())
				throw new ParseException(27);
			else
				lex.nextLex();
		}
		else if(lex.match("["))
		{
			lex.nextLex();
			expression();
			if(!lex.match("]"))
				throw new ParseException(24);
			else
				lex.nextLex();
		}
		else if(!lex.match("^"))
		{
		}
		
		stop("reference");
		return true;
	}
	
	private boolean isClassDeclaration()
	{
		return(lex.match("class"));
	}
	
	
	/*private boolean isNonClassDeclaration()
	{
		return(isFunctionDeclaration() || isVariableDeclaration()
				|| isConstantDeclaration() || isTypeDeclaration());
	}*/
	boolean isNonClassDeclaration(){ return !lex.match("begin"); } 
	
	private boolean isFunctionDeclaration()
	{
		return(lex.match("function"));
	}
	private boolean isNonFunctionDeclaration()
	{
		return(lex.match("var") || lex.match("const") || lex.match("type"));
	}
	private boolean isConstantDeclaration()
	{
		return(lex.match("const"));
	}
	private boolean isTypeDeclaration()
	{
		return(lex.match("type"));
	}
	private boolean isVariableDeclaration()
	{
		return(lex.match("var"));
	}
	private boolean isNameDeclaration()
	{
		return(lex.isIdentifier());
	}
	private boolean isStatement()
	{
		return( isReturnStatement() || isIfStatement() || isWhileStatement() 
			|| isCompoundStatement() || isAssignOrFunction() );
	}
	private boolean isReturnStatement()
	{
		return(lex.match("return"));
	}
	
	private boolean isIfStatement()
	{
		return(lex.match("if"));
	}
	
	private boolean isWhileStatement()
	{
		return (lex.match("while"));
	}
	
	private boolean isCompoundStatement()
	{
		return(lex.match("begin"));
	}
	
	private boolean isAssignOrFunction()
	{
		return(lex.isIdentifier());
	}
	
	private boolean isExpression()
	{
		return( lex.match("(") || lex.match("not") || lex.match("new") ||
			lex.match("-") || lex.match("&") || isReference() || isConstant());
	}
	
	private boolean isReference()
	{
		return(lex.isIdentifier());
	}
	private boolean isConstant()
	{
		return (lex.tokenCategory() == 3 || lex.tokenCategory() == 4 
				|| lex.tokenCategory() == 5);
	}
	
	private boolean isLogicalOperator()
	{
		return(lex.match("and") || lex.match("or"));
	}
	
	private boolean isRelationalOperator()
	{
		return (lex.match("<") || lex.match("<=") || lex.match("!=") || 
			lex.match("==") || lex.match(">=") || lex.match(">") );
	}
	
	private boolean isAdditionOperator()
	{
		return (lex.match("+") || lex.match("-") || lex.match("<<") );
	}
	private boolean isMultiplicationOperator()
	{
		return (lex.match("*") || lex.match("/") || lex.match("%") );
	}
}
