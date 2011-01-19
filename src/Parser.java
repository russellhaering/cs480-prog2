
//parser skeleton, CS 480, Winter 2006
//written by Tim Budd
//modified by:


public class Parser {
	private final Lexer lex;
	private final boolean debug;

	public Parser (Lexer l, boolean d) { lex = l; debug = d; }

	public void parse () throws ParseException {
		lex.nextLex();
		program();
		if (lex.tokenCategory() != Lexer.endOfInput)
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

		if(lex.tokenCategory() == 3 || lex.tokenCategory() == 4|| lex.tokenCategory() == 5)
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

	private boolean variableDeclaration() throws ParseException
	{
		start("variableDeclaration");
		if(lex.match("var"))
			lex.nextLex();
		else
			throw new ParseException(15);

		stop("variableDeclaration");
		return nameDeclaration();
	}

	private boolean nameDeclaration() throws ParseException
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
		return true;
	}

	private boolean classDeclaration() throws ParseException
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
		return true;
	}

	private boolean classBody() throws ParseException
	{
		start("classBody");

		if(lex.match("begin"))
			lex.nextLex();
		else
			throw new ParseException(4);

		while (!lex.match("end")&&lex.tokenCategory() != 7) {
			nonClassDeclaration();
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		if(lex.match("end"))
			lex.nextLex();
		else
			throw new ParseException(8);
		stop("classBody");
		return true;
	}

	private boolean functionDeclaration() throws ParseException
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

		return true;


	}

	private boolean arguments() throws ParseException
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
		return true;
	}

	private boolean argumentList() throws ParseException
	{
		start("argumentList");

		while(!lex.match(")")&&lex.tokenCategory() != 7)
		{
			nameDeclaration();


			if(lex.match(","))
				lex.nextLex();
			else
				break;
		}

		stop("argumentList");
		return true;
	}

	private boolean returnType() throws ParseException
	{
		start("returnType");
		if(lex.match(":"))
		{
			lex.nextLex();
			type();
		}
		stop("returnType");
		return true;
	}

	private boolean type() throws ParseException
	{
		start("type");


		if(lex.isIdentifier())
		{
			stop("type");
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
		}
		else
			throw new ParseException(30);
		stop("type");
		return true;
	}

	private boolean functionBody() throws ParseException
	{
		start("functionBody");
		while((lex.match("function")||lex.match("const")||lex.match("var")||lex.match("type"))&&lex.tokenCategory() != 7)
		{
			nonClassDeclaration();
			if(!lex.match(";"))
				throw new ParseException(18);
			lex.nextLex();
		}
		compoundStatement();
		stop("functionBody");
		return true;
	}

	private boolean compoundStatement() throws ParseException
	{
		start("compoundStatement");
		if(lex.match("begin"))
			lex.nextLex();
		else
			throw new ParseException(4);

		while (!lex.match("end")&&lex.tokenCategory() != 7 ) {
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
		return true;
	}

	private boolean statement() throws ParseException
	{
		start("statement");
		if(lex.match("return"))
			returnStatement();
		else if (lex.match("if"))
			ifStatement();
		else if (lex.match("while"))
			whileStatement();
		else if(lex.match("begin"))
			compoundStatement();
		else
			reference();

		stop("statement");
		return true;
	}
	private boolean returnStatement() throws ParseException
	{
		start("returnStatement");
		if(lex.match("return"))
			lex.nextLex();
		else
			throw new ParseException(12);
		if(lex.match("("))
		{
			lex.nextLex();
			expression();
			if(lex.match(")"))
				lex.nextLex();
			else
				throw new ParseException(22);
		}
		stop("returnStatement");
		return true;
	}

	private boolean ifStatement() throws ParseException
	{
		start("ifStatement");
		if(lex.match("if"))
			lex.nextLex();
		else
			throw new ParseException(11);
		if(lex.match("("))
		{
			lex.nextLex();
			expression();
			if(lex.match(")"))
				lex.nextLex();
			else
				throw new ParseException(22);
		}
		else
			throw new ParseException(21);
		statement();
		if(lex.match("else"))
		{
			lex.nextLex();
			statement();
		}
		stop("ifStatement");
		return true;
	}

	private boolean whileStatement() throws ParseException
	{
		start("whileStatement");
		if(lex.match("while"))
			lex.nextLex();
		else
			throw new ParseException(16);
		if(lex.match("("))
			lex.nextLex();
		else
			throw new ParseException(21);
		expression();
		if(lex.match(")"))
			lex.nextLex();
		else
			throw new ParseException(22);
		statement();
		stop("whileStatement");
		return true;
	}
	private boolean assignOrFunction() throws ParseException
	{
		start("assignOrFunction");
		reference();
		if(lex.match("=")){
			lex.nextLex();
			expression();
		}
		else if (lex.match("("))
		{
			lex.nextLex();
			parameterList();
			if(lex.match(")"))
				lex.nextLex();
			else
				throw new ParseException(22);
		}
		else
			throw new ParseException(37);
		stop("assignOrFunction");
		return true;
	}

	private boolean parameterList() throws ParseException
	{
		start("parameterList");
		if(lex.match("(")||lex.match("not")||lex.match("new")||lex.match("-")||lex.match("&")||lex.tokenCategory() != 6||lex.tokenCategory() != 7||lex.tokenCategory() != 2)
		{
			lex.nextLex();
			expression();
			while(lex.match(",")&&lex.tokenCategory() != 7)
			{
				lex.nextLex();
				expression();
			}
		}
		stop("parameterList");
		return true;
	}

	private boolean expression() throws ParseException
	{
		start("expression");
		relExpression();
		while((lex.match("and")||lex.match("or"))&&lex.tokenCategory() != 7)
		{
			lex.nextLex();
			relExpression();
		}
		stop("expression");
		return true;
	}
	private boolean relExpression() throws ParseException
	{
		start("relExpression");
		plusExpression();
		if(lex.match("<")||lex.match("<=")||lex.match("!=")||lex.match("==")||lex.match(">=")||lex.match(">")){
			lex.nextLex();
			plusExpression();
		}
		stop("relExpression");
		return true;
	}

	private boolean plusExpression() throws ParseException
	{
		start("plusExpression");
		timesExpression();
		while((lex.match("+")||lex.match("-")||lex.match("<<"))&&lex.tokenCategory() != 7){
			lex.nextLex();
			timesExpression();
		}
		stop("plusExpression");
		return true;
	}

	private boolean timesExpression() throws ParseException
	{
		start("timesExpression");
		term();
		while((lex.match("*")||lex.match("/")||lex.match("%"))&&lex.tokenCategory() != 7){
			lex.nextLex();
			term();
		}
		stop("timesExpression");
		return true;
	}
	private boolean term() throws ParseException
	{
		start("term");
		if(lex.match("("))
		{
			lex.nextLex();
			expression();
			if (lex.match(")"))
				lex.nextLex();
			else
				throw new ParseException(22);
		}
		else if(lex.match("not"))
		{
			lex.nextLex();
			term();
		}
		else if(lex.match("new"))
		{
			lex.nextLex();
			type();
		}
		else if(lex.match("-"))
		{
			lex.nextLex();
			term();
		}
		else if(lex.match("&"))
		{
			lex.nextLex();
			reference();
		}
		else if(lex.tokenCategory() <= 5 && lex.tokenCategory() >= 3)
		{
			lex.nextLex();
		}
		else
		{
			reference();
			if (lex.match("("))
			{
				lex.nextLex();
				reference();
				if (lex.match(")"))
					lex.nextLex();
				else
					throw new ParseException(22);
			}
		}
		stop("term");
		return true;
	}
	private boolean reference() throws ParseException
	{
		start("reference");
		if(lex.tokenCategory() == 1)
			lex.nextLex();
		else
		{
			while (lex.match("^") || lex.match(".") || lex.match("[")
					&& lex.tokenCategory() != 7)
			{
				if (lex.match("^"))
				{
					lex.nextLex();
				}
				else if(lex.match("."))
				{
					lex.nextLex();
					if(lex.tokenCategory() == 1)
						lex.nextLex();
					else
						throw new ParseException(27);
				}
				else if(lex.match("["))
				{
					lex.nextLex();
					expression();
					if(lex.match("]"))
						lex.nextLex();
					else
						throw new ParseException(24);
				}
			}
		}
		stop("reference");
		return true;
	}

	private boolean isClassDeclaration()
	{
		return lex.match("class");
	}
	private boolean isNonClassDeclaration()
	{
		return lex.match("function") || lex.match("var") || lex.match("const") || lex.match("type");
	}
	private boolean isFunctionDeclaration()
	{
		return lex.match("function");
	}
	private boolean isNonFunctionDeclaration()
	{
		return lex.match("var") || lex.match("const") || lex.match("type");
	}
	private boolean isConstantDeclaration()
	{
		if(lex.match("const"))
			return true;
		else return false;
	}
	private boolean isTypeDeclaration()
	{
		if(lex.match("type"))
			return true;
		else return false;
	}
	private boolean isVariableDeclaration()
	{
		if(lex.match("var"))
			return true;
		else return false;
	}
	private boolean isNameDeclaration()
	{
		if(lex.isIdentifier())
			return true;
		else return false;
	}

}
