import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Rule;



public class SyntacticAnalyser {

	enum Rule {
		PROG_1, LOS_1, LOS_2, STAT_1, STAT_2, STAT_3, STAT_4, STAT_5, STAT_6, STAT_7,
		WHILE_1, FOR_1, FOR_START_1, FOR_START_2, FOR_START_3, FOR_ARITH_1, FOR_ARITH_2,
		IF_1, ELSE_IF_1, ELSE_IF_2, ELSE_IFF_1, POSS_IF_1, POSS_IF_2, ASSIGN_1, DECL_1, 
		POSS_ASSIGN_1, POSS_ASSIGN_2, PRINT_1, TYPE, EXPR_1, EXPR_2, 
		CHAR_EXPR_1, BOOL_EXPR_1, BOOL_EXPR_2, BOOL_OP_1, BOOL_OP_2, BOOL_EQ_1, BOOL_EQ_2,
		BOOL_LOG_1, BOOL_LOG_2, REL_EXPR_1, REL_EXPR_2, REL_EXPR_3, REL_EXPRR_1, REL_EXPRR_2,
		REL_OP_1, REL_OP_2, REL_OP_3, REL_OP_4, ARITH_EXPR_1, ARITH_EXPRR_1, ARITH_EXPRR_2,
		ARITH_EXPRR_3, TERM_1, TERMM_1, TERMM_2, TERMM_3, TERMM_4, FACTOR_1, FACTOR_2,FACTOR_3,
		PRINT_EXPR_1, PRINT_EXPR_2
	};

	private Deque<Token> tokens;  // Token stream
    private Token lookahead;      // Current token being examined
	private Map<Pair<TreeNode.Label, Token.TokenType>, Rule> parsingTable = new HashMap<>();

    public SyntacticAnalyser(List<Token> tokens) {
        this.tokens = new ArrayDeque<>(tokens);
        this.lookahead = this.tokens.poll();  // Initialize the first token
		initializeParsingTable();
    }

    // Move to the next token
    private void consume() {
        this.lookahead = this.tokens.poll();
    }

    // Expect a specific token and consume it if it matches
    private void expect(Token.TokenType expectedType) throws SyntaxException {
        if (lookahead.getType() != expectedType) {
            throw new SyntaxException("Expected: " + expectedType + " but found: " + lookahead.getType());
        }
        consume();
    }
	private void initializeParsingTable() { 
		// Grammar rules for prog
        parsingTable.put(new Pair<>(TreeNode.Label.prog, Token.TokenType.PUBLIC), Rule.PROG_1);

        //Grammar rules for list of statements
        parsingTable.put(new Pair<>(TreeNode.Label.los, Token.TokenType.IF), Rule.LOS_1);
        parsingTable.put(new Pair<>(TreeNode.Label.los, Token.TokenType.WHILE), Rule.LOS_1);
        parsingTable.put(new Pair<>(TreeNode.Label.los, Token.TokenType.FOR), Rule.LOS_1);
        parsingTable.put(new Pair<>(TreeNode.Label.los, Token.TokenType.ID), Rule.LOS_1);
        parsingTable.put(new Pair<>(TreeNode.Label.los, Token.TokenType.TYPE), Rule.LOS_1);
        parsingTable.put(new Pair<>(TreeNode.Label.los, Token.TokenType.PRINT), Rule.LOS_1);
        parsingTable.put(new Pair<>(TreeNode.Label.los, Token.TokenType.SEMICOLON), Rule.LOS_1);
        parsingTable.put(new Pair<>(TreeNode.Label.los, Token.TokenType.RBRACE), Rule.LOS_2);

        //Grammar rules for statement
        parsingTable.put(new Pair<>(TreeNode.Label.stat, Token.TokenType.WHILE), Rule.STAT_1);
        parsingTable.put(new Pair<>(TreeNode.Label.stat, Token.TokenType.FOR), Rule.STAT_2);
        parsingTable.put(new Pair<>(TreeNode.Label.stat, Token.TokenType.IF), Rule.STAT_3);
        parsingTable.put(new Pair<>(TreeNode.Label.stat, Token.TokenType.ASSIGN), Rule.STAT_4);
        parsingTable.put(new Pair<>(TreeNode.Label.stat, Token.TokenType.TYPE), Rule.STAT_5);
        parsingTable.put(new Pair<>(TreeNode.Label.stat, Token.TokenType.PRINT), Rule.STAT_6);
        parsingTable.put(new Pair<>(TreeNode.Label.stat, Token.TokenType.SEMICOLON), Rule.STAT_7);

        //Grammar rules for while
        parsingTable.put(new Pair<>(TreeNode.Label.whilestat, Token.TokenType.WHILE), Rule.WHILE_1);

        //Grammar rules for for 
        parsingTable.put(new Pair<>(TreeNode.Label.forstat, Token.TokenType.FOR), Rule.FOR_1);

        //Grammar rules for for start
        parsingTable.put(new Pair<>(TreeNode.Label.forstart, Token.TokenType.TYPE), Rule.FOR_START_1);
        parsingTable.put(new Pair<>(TreeNode.Label.forstart, Token.TokenType.ID), Rule.FOR_START_2);
        parsingTable.put(new Pair<>(TreeNode.Label.forstart, Token.TokenType.SEMICOLON), Rule.FOR_START_3);

        //Grammar rules for for arith
        parsingTable.put(new Pair<>(TreeNode.Label.forstart, Token.TokenType.LPAREN), Rule.FOR_ARITH_1);
        parsingTable.put(new Pair<>(TreeNode.Label.forstart, Token.TokenType.ID), Rule.FOR_ARITH_1);
        parsingTable.put(new Pair<>(TreeNode.Label.forstart, Token.TokenType.NUM), Rule.FOR_ARITH_1);

        //Grammar rules for if
        parsingTable.put(new Pair<>(TreeNode.Label.ifstat, Token.TokenType.IF), Rule.IF_1);

        //Grammer rules for eles if
        parsingTable.put(new Pair<>(TreeNode.Label.elseifstat, Token.TokenType.ELSE), Rule.ELSE_IF_1);
        parsingTable.put(new Pair<>(TreeNode.Label.elseifstat, Token.TokenType.WHILE), Rule.ELSE_IF_2);
        parsingTable.put(new Pair<>(TreeNode.Label.elseifstat, Token.TokenType.FOR), Rule.ELSE_IF_2);
        parsingTable.put(new Pair<>(TreeNode.Label.elseifstat, Token.TokenType.IF), Rule.ELSE_IF_2);
        parsingTable.put(new Pair<>(TreeNode.Label.elseifstat, Token.TokenType.PRINT), Rule.ELSE_IF_2);
        parsingTable.put(new Pair<>(TreeNode.Label.elseifstat, Token.TokenType.TYPE), Rule.ELSE_IF_2);
        parsingTable.put(new Pair<>(TreeNode.Label.elseifstat, Token.TokenType.ID), Rule.ELSE_IF_2);
        parsingTable.put(new Pair<>(TreeNode.Label.elseifstat, Token.TokenType.SEMICOLON), Rule.ELSE_IF_2);
        parsingTable.put(new Pair<>(TreeNode.Label.elseifstat, Token.TokenType.RBRACE), Rule.ELSE_IF_2);

        //Grammar rules for else?if
        parsingTable.put(new Pair<>(TreeNode.Label.elseorelseif, Token.TokenType.ELSE), Rule.ELSE_IFF_1);

        //Grammar rules for poss if 
        parsingTable.put(new Pair<>(TreeNode.Label.possif, Token.TokenType.IF), Rule.POSS_IF_1);
        parsingTable.put(new Pair<>(TreeNode.Label.possif, Token.TokenType.LBRACE), Rule.POSS_IF_2);

        //Grammar rules for assign
        parsingTable.put(new Pair<>(TreeNode.Label.assign, Token.TokenType.ID), Rule.ASSIGN_1);

        //Grammar rules for decl 
        parsingTable.put(new Pair<>(TreeNode.Label.decl, Token.TokenType.TYPE), Rule.DECL_1);

        //Grammar rules for poss assign 
        parsingTable.put(new Pair<>(TreeNode.Label.possassign, Token.TokenType.EQUAL), Rule.POSS_ASSIGN_1);
        parsingTable.put(new Pair<>(TreeNode.Label.possassign, Token.TokenType.RBRACE), Rule.POSS_ASSIGN_2);
        parsingTable.put(new Pair<>(TreeNode.Label.possassign, Token.TokenType.SEMICOLON), Rule.POSS_ASSIGN_2);
        parsingTable.put(new Pair<>(TreeNode.Label.possassign, Token.TokenType.TYPE), Rule.POSS_ASSIGN_2);
        parsingTable.put(new Pair<>(TreeNode.Label.possassign, Token.TokenType.PRINT), Rule.POSS_ASSIGN_2);
        parsingTable.put(new Pair<>(TreeNode.Label.possassign, Token.TokenType.WHILE), Rule.POSS_ASSIGN_2);
        parsingTable.put(new Pair<>(TreeNode.Label.possassign, Token.TokenType.FOR), Rule.POSS_ASSIGN_2);
        parsingTable.put(new Pair<>(TreeNode.Label.possassign, Token.TokenType.IF), Rule.POSS_ASSIGN_2);
        parsingTable.put(new Pair<>(TreeNode.Label.possassign, Token.TokenType.ID), Rule.POSS_ASSIGN_2);

        //Grammar rules for print
        parsingTable.put(new Pair<>(TreeNode.Label.print, Token.TokenType.PRINT), Rule.PRINT_1);

        //Grammar rules for type 
        parsingTable.put(new Pair<>(TreeNode.Label.type, Token.TokenType.TYPE), Rule.TYPE);

        //Grammar rules for expression
        parsingTable.put(new Pair<>(TreeNode.Label.expr, Token.TokenType.LPAREN), Rule.EXPR_1);
        parsingTable.put(new Pair<>(TreeNode.Label.expr, Token.TokenType.ID), Rule.EXPR_1);
        parsingTable.put(new Pair<>(TreeNode.Label.expr, Token.TokenType.NUM), Rule.EXPR_1);
        parsingTable.put(new Pair<>(TreeNode.Label.expr, Token.TokenType.TRUE), Rule.EXPR_1);
        parsingTable.put(new Pair<>(TreeNode.Label.expr, Token.TokenType.FALSE), Rule.EXPR_1);
        parsingTable.put(new Pair<>(TreeNode.Label.expr, Token.TokenType.SQUOTE), Rule.EXPR_2);

        //Grammar rules for char expression
        parsingTable.put(new Pair<>(TreeNode.Label.charexpr, Token.TokenType.SQUOTE), Rule.CHAR_EXPR_1);

        //Grammar rules for boolean expression 
        parsingTable.put(new Pair<>(TreeNode.Label.boolexpr, Token.TokenType.EQUAL), Rule.BOOL_EXPR_1);
        parsingTable.put(new Pair<>(TreeNode.Label.boolexpr, Token.TokenType.NEQUAL), Rule.BOOL_EXPR_1);
        parsingTable.put(new Pair<>(TreeNode.Label.boolexpr, Token.TokenType.AND), Rule.BOOL_EXPR_1);
        parsingTable.put(new Pair<>(TreeNode.Label.boolexpr, Token.TokenType.OR), Rule.BOOL_EXPR_1);
        parsingTable.put(new Pair<>(TreeNode.Label.boolexpr, Token.TokenType.LPAREN), Rule.BOOL_EXPR_2);
        parsingTable.put(new Pair<>(TreeNode.Label.boolexpr, Token.TokenType.LBRACE), Rule.BOOL_EXPR_2);
        parsingTable.put(new Pair<>(TreeNode.Label.boolexpr, Token.TokenType.SEMICOLON), Rule.BOOL_EXPR_2);
        parsingTable.put(new Pair<>(TreeNode.Label.boolexpr, Token.TokenType.TYPE), Rule.BOOL_EXPR_2);
        parsingTable.put(new Pair<>(TreeNode.Label.boolexpr, Token.TokenType.PRINT), Rule.BOOL_EXPR_2);
        parsingTable.put(new Pair<>(TreeNode.Label.boolexpr, Token.TokenType.WHILE), Rule.BOOL_EXPR_2);
        parsingTable.put(new Pair<>(TreeNode.Label.boolexpr, Token.TokenType.FOR), Rule.BOOL_EXPR_2);
        parsingTable.put(new Pair<>(TreeNode.Label.boolexpr, Token.TokenType.IF), Rule.BOOL_EXPR_2);
        parsingTable.put(new Pair<>(TreeNode.Label.boolexpr, Token.TokenType.ID), Rule.BOOL_EXPR_2);

        //Grammar rules for boolean op 
        parsingTable.put(new Pair<>(TreeNode.Label.boolop, Token.TokenType.EQUAL), Rule.BOOL_OP_1);
        parsingTable.put(new Pair<>(TreeNode.Label.boolop, Token.TokenType.NEQUAL), Rule.BOOL_OP_1);
        parsingTable.put(new Pair<>(TreeNode.Label.boolop, Token.TokenType.AND), Rule.BOOL_OP_2);
        parsingTable.put(new Pair<>(TreeNode.Label.boolop, Token.TokenType.OR), Rule.BOOL_OP_2);

		// Grammar rules for <<booleq>> (checked)
		parsingTable.put(new Pair<>(TreeNode.Label.booleq, Token.TokenType.EQUAL), Rule.BOOL_EQ_1);
		parsingTable.put(new Pair<>(TreeNode.Label.booleq, Token.TokenType.NEQUAL), Rule.BOOL_EQ_2);

		//Grammar rules for <<boollog>> (checked)
		parsingTable.put(new Pair<>(TreeNode.Label.boollog, Token.TokenType.AND), Rule.BOOL_LOG_1);
		parsingTable.put(new Pair<>(TreeNode.Label.boollog, Token.TokenType.OR), Rule.BOOL_LOG_2);

		//Grammar rules for <<relexpr>> (checked)
		parsingTable.put(new Pair<>(TreeNode.Label.relexpr, Token.TokenType.LPAREN), Rule.REL_EXPR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.relexpr, Token.TokenType.ID), Rule.REL_EXPR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.relexpr, Token.TokenType.NUM), Rule.REL_EXPR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.relexpr, Token.TokenType.TRUE), Rule.REL_EXPR_2);
		parsingTable.put(new Pair<>(TreeNode.Label.relexpr, Token.TokenType.FALSE), Rule.REL_EXPR_3);

		//Grammar rules for <<relexpr'>> (checked)
		parsingTable.put(new Pair<>(TreeNode.Label.relexprprime, Token.TokenType.GT), Rule.REL_EXPRR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.relexprprime, Token.TokenType.GE), Rule.REL_EXPRR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.relexprprime, Token.TokenType.LT), Rule.REL_EXPRR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.relexprprime, Token.TokenType.LE), Rule.REL_EXPRR_1);

		parsingTable.put(new Pair<>(TreeNode.Label.relexprprime, Token.TokenType.RBRACE), Rule.REL_EXPRR_2);
		parsingTable.put(new Pair<>(TreeNode.Label.relexprprime, Token.TokenType.SEMICOLON), Rule.REL_EXPRR_2);
		parsingTable.put(new Pair<>(TreeNode.Label.relexprprime, Token.TokenType.TYPE), Rule.REL_EXPRR_2);
		parsingTable.put(new Pair<>(TreeNode.Label.relexprprime, Token.TokenType.PRINT), Rule.REL_EXPRR_2);
		parsingTable.put(new Pair<>(TreeNode.Label.relexprprime, Token.TokenType.WHILE), Rule.REL_EXPRR_2);
		parsingTable.put(new Pair<>(TreeNode.Label.relexprprime, Token.TokenType.FOR), Rule.REL_EXPRR_2);
		parsingTable.put(new Pair<>(TreeNode.Label.relexprprime, Token.TokenType.IF), Rule.REL_EXPRR_2);
		parsingTable.put(new Pair<>(TreeNode.Label.relexprprime, Token.TokenType.ID), Rule.REL_EXPRR_2);

		//Grammar rules for <<relop>> (Checked)
		parsingTable.put(new Pair<>(TreeNode.Label.relop, Token.TokenType.LT), Rule.REL_OP_1);
		parsingTable.put(new Pair<>(TreeNode.Label.relop, Token.TokenType.LE), Rule.REL_OP_2);
		parsingTable.put(new Pair<>(TreeNode.Label.relop, Token.TokenType.GT), Rule.REL_OP_3);
		parsingTable.put(new Pair<>(TreeNode.Label.relop, Token.TokenType.GE), Rule.REL_OP_4);

		//Grammar rules for <<arith expr>> (Checked)
		parsingTable.put(new Pair<>(TreeNode.Label.arithexpr, Token.TokenType.LPAREN), Rule.ARITH_EXPR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexpr, Token.TokenType.ID), Rule.ARITH_EXPR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexpr, Token.TokenType.NUM), Rule.ARITH_EXPR_1);

		//Grammar rules for <<arith expr'>> (checked)
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.PLUS), Rule.ARITH_EXPRR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.MINUS), Rule.ARITH_EXPRR_2);

		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.EQUAL), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.NEQUAL), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.GT), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.GE), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.LT), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.LE), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.RPAREN), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.RBRACE), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.AND), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.OR), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.SEMICOLON), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.TYPE), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.PRINT), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.WHILE), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.FOR), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.IF), Rule.ARITH_EXPRR_3);
		parsingTable.put(new Pair<>(TreeNode.Label.arithexprprime, Token.TokenType.ID), Rule.ARITH_EXPRR_3);

		//Grammar rules for <<term>> (checked)
		parsingTable.put(new Pair<>(TreeNode.Label.term, Token.TokenType.LPAREN), Rule.TERM_1);
		parsingTable.put(new Pair<>(TreeNode.Label.term, Token.TokenType.ID), Rule.TERM_1);
		parsingTable.put(new Pair<>(TreeNode.Label.term, Token.TokenType.NUM), Rule.TERM_1);

		//Grammar rules for <<term'>> (checked)
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.TIMES), Rule.TERMM_1);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.DIVIDE), Rule.TERMM_2);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.MOD), Rule.TERMM_3);

		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.PLUS), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.MINUS), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.EQUAL), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.NEQUAL), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.GT), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.LT), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.GE), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.LE), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.RPAREN), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.RBRACE), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.AND), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.OR), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.SEMICOLON), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.TYPE), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.PRINT), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.WHILE), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.FOR), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.IF), Rule.TERMM_4);
		parsingTable.put(new Pair<>(TreeNode.Label.termprime, Token.TokenType.ID), Rule.TERMM_4);

		//Grammar rules for <<factor>> (checked)
		parsingTable.put(new Pair<>(TreeNode.Label.factor, Token.TokenType.LPAREN), Rule.FACTOR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.factor, Token.TokenType.ID), Rule.FACTOR_2);
		parsingTable.put(new Pair<>(TreeNode.Label.factor, Token.TokenType.NUM), Rule.FACTOR_3);

		//Grammar rules for <<print expr>> (Checked)
		parsingTable.put(new Pair<>(TreeNode.Label.printexpr, Token.TokenType.LPAREN), Rule.PRINT_EXPR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.printexpr, Token.TokenType.ID), Rule.PRINT_EXPR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.printexpr, Token.TokenType.NUM), Rule.PRINT_EXPR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.printexpr, Token.TokenType.TRUE), Rule.PRINT_EXPR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.printexpr, Token.TokenType.FALSE), Rule.PRINT_EXPR_1);
		parsingTable.put(new Pair<>(TreeNode.Label.printexpr, Token.TokenType.DQUOTE), Rule.PRINT_EXPR_2);
	}

	public static ParseTree parse(List<Token> tokens) throws SyntaxException {
		SyntacticAnalyser analyser = new SyntacticAnalyser(tokens);
    
		TreeNode rootNode = new TreeNode(TreeNode.Label.prog, null); // Assuming PROGRAM is the root label
		analyser.parse(rootNode); // Start parsing with the root node
    	return new ParseTree(rootNode); 
	}

	private void parse(TreeNode tn) throws SyntaxException {
		Rule rule = parsingTable.get(new Pair<>(tn, lookahead.getType()));
		if (rule == null) {
			throw new SyntaxException("Unexpected token: " + lookahead.getValue());
			
		}
		switch(rule){
			case PROG_1:
				parseProg_1(tn);
				break;
			case LOS_1:
				parseLos_1(tn);
				break;
			case LOS_2:
				parseLos_2(tn);
				break;
			case STAT_1:
				parseStat_1(tn);
				break;
			case STAT_2:
				parseStat_2(tn);
				break;
			case STAT_3:
				parseStat_3(tn);
				break;
			case STAT_4:
				parseStat_4(tn);
				break;
			case STAT_5:
				parseStat_5(tn);
				break;
			case STAT_6:
				parseStat_6(tn);
				break;
			case STAT_7:
				parseStat_7(tn);
				break;
			case WHILE_1:
				parseWhile_1(tn);
				break;
			case FOR_1:
				parseFor_1(tn);
				break;
			case FOR_START_1:
				parseFor_Start_1(tn);
				break;
			case FOR_START_2:
				parseFor_Start_2(tn);
				break;
			case FOR_START_3:
				parseFor_Start_3(tn);
				break;
			case FOR_ARITH_1:
				parseFor_Arith_1(tn);
				break;
			case FOR_ARITH_2:
				parseFor_Arith_2(tn);
				break;
			case IF_1:
				parseIf_1(tn);
				break;
			case ELSE_IF_1:
				parseElse_If_1(tn);
				break;
			case ELSE_IF_2:
				parseElse_If_2(tn);
				break;   
			case ELSE_IFF_1:
				parseElse_Iff_1(tn);
				break;
			case POSS_IF_1:
				parsePoss_If_1(tn);
				break;
			case POSS_IF_2:
				parsePoss_If_2(tn);
				break;
			case ASSIGN_1:
				parseAssign_1(tn);
				break;
			case DECL_1:
				parseDecl_1(tn);
				break;
			case POSS_ASSIGN_1:
				parsePoss_Assign_1(tn);
				break;
			case POSS_ASSIGN_2:
				parsePoss_Assign_2(tn);
				break;
			case PRINT_1:
				parsePrint_1(tn);
				break;
			case TYPE:
				switch (lookahead.getValue().get()) {
					case "int":
						parseType_1(tn);
						break;
					case "boolean":
						parseType_2(tn);
						break;
					case "char":
						parseType_3(tn);
						break;
					default:
						throw new SyntaxException("Wrong");
				}
			case EXPR_1:
				parseExpr_1(tn);
				break; 
			case EXPR_2:
				parseExpr_2(tn);
				break;
			case CHAR_EXPR_1:
				parseChar_Expr_1(tn);
				break;
			case BOOL_EXPR_1:
				parseBool_Expr_1(tn);
				break;
			case BOOL_EXPR_2:
				parseBool_Expr_2(tn);
				break;
			case BOOL_OP_1:
				parseBool_Op_1(tn);
				break;
			case BOOL_OP_2:
				parseBool_Op_2(tn);
				break;
			case BOOL_EQ_1:
				parseBool_Eq_1(tn);
				break;
			case BOOL_EQ_2:
				parseBool_Eq_2(tn);
				break;
			case BOOL_LOG_1:
				parseBool_Log_1(tn);
				break;
			case BOOL_LOG_2:
				parseBool_Log_2(tn);
				break;
			case REL_EXPR_1:
				parseRel_Expr_1(tn);
				break;
			case REL_EXPR_2:
				parseRel_Expr_2(tn);
				break;  
			case REL_EXPR_3:
				parseRel_Expr_3(tn);
				break;
			case REL_EXPRR_1:
				parseRel_Exprr_1(tn);
				break;
			case REL_EXPRR_2:
				parseRel_Exprr_2(tn);
				break;
			case REL_OP_1:
				parseRel_Op_1(tn);
				break;
			case REL_OP_2:
				parseRel_Op_2(tn);
				break;
			case REL_OP_3:
				parseRel_Op_3(tn);
				break; 
			case REL_OP_4:
				parseRel_Op_4(tn);
				break;
			case ARITH_EXPR_1:
				parseArith_Expr_1(tn);
				break;
			case ARITH_EXPRR_1:
				parseArith_Exprr_1(tn);
				break;
			case ARITH_EXPRR_2:
				parseArith_Exprr_2(tn);
				break;
			case ARITH_EXPRR_3:
				parseArith_Exprr_3(tn);
				break;
			case TERM_1:
				parseTerm_1(tn);
				break;
			case TERMM_1:
				parseTermm_1(tn);
				break;
			case TERMM_2:
				parseTermm_2(tn);
				break;
			case TERMM_3:
				parseTermm_3(tn);
				break;
			case TERMM_4:
				parseTermm_4(tn);
				break;  
			case FACTOR_1:
				parseFactor_1(tn);
				break;
			case FACTOR_2:
				parseFactor_2(tn);
				break;
			case FACTOR_3:
				parseFactor_3(tn);
				break;
			case PRINT_EXPR_1:
				parsePrint_Expr_1(tn);
				break;
			case PRINT_EXPR_2:
				parsePrint_Expr_2(tn);
				break;
			default:
				throw new SyntaxException("No rule found for: " + lookahead.getType());
		}   
	}

	public void parseProg_1(TreeNode tn) throws SyntaxException {
		expect(Token.TokenType.PUBLIC);
		TreeNode pub1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.PUBLIC), tn);
		
		expect(Token.TokenType.CLASS);
		TreeNode cla = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.CLASS), tn);

		Token classname = lookahead;
		expect(Token.TokenType.ID);
		TreeNode id=new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.ID,classname.getValue().orElse("")), tn);
		
		expect(Token.TokenType.LBRACE);
		TreeNode lbr1=new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LBRACE), tn);
		
		expect(Token.TokenType.PUBLIC);
		TreeNode pub2 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.PUBLIC), tn);
		
		expect(Token.TokenType.STATIC);
		TreeNode sta = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.STATIC), tn);
		
		expect(Token.TokenType.VOID);
		TreeNode voi = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.VOID), tn);
		
		expect(Token.TokenType.MAIN);
		TreeNode mai = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.MAIN), tn);
		
		expect(Token.TokenType.LPAREN);
		TreeNode lpa1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LPAREN), tn);
		
		expect(Token.TokenType.STRINGARR);
		TreeNode str = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.STRINGARR), tn);
		
		expect(Token.TokenType.ARGS);
		TreeNode arg = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.ARGS), tn);

		expect(Token.TokenType.RPAREN);
		TreeNode rpa1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.RPAREN), tn);

		expect(Token.TokenType.LBRACE);
		TreeNode lbra2 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LBRACE), tn);

		TreeNode losNode=new TreeNode(TreeNode.Label.los, tn);

		parse(losNode);
		
		expect(Token.TokenType.RBRACE);
		TreeNode rbra1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LBRACE), tn);
		
		expect(Token.TokenType.RBRACE);
		TreeNode rbra2 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LBRACE), tn);
		
		
		tn.addChild(pub1);
		tn.addChild(cla);
		tn.addChild(id);
		tn.addChild(lbr1);
		tn.addChild(pub2);
		tn.addChild(sta);
		tn.addChild(voi);
		tn.addChild(mai);
		tn.addChild(lpa1);
		tn.addChild(str);
		tn.addChild(arg);
		tn.addChild(rpa1);
		tn.addChild(lbra2);
		tn.addChild(losNode);
		tn.addChild(rbra1);
		tn.addChild(rbra2);
	}

	public void parseLos_1(TreeNode tn) throws SyntaxException{
		TreeNode statNode=new TreeNode(TreeNode.Label.stat, tn);
		parse(statNode);

		TreeNode losNode=new TreeNode(TreeNode.Label.los, tn);
		parse(losNode);

		tn.addChild(statNode);
		tn.addChild(losNode);
	}


	public void parseLos_2(TreeNode tn) throws SyntaxException{
		TreeNode epsilonNode = new TreeNode(TreeNode.Label.epsilon, tn);

    	// Add the epsilon node as a child to the parent node
    	tn.addChild(epsilonNode);
	}
	
	public void parseStat_1(TreeNode tn) throws SyntaxException{
		TreeNode whileNode=new TreeNode(TreeNode.Label.whilestat, tn);
		parse(whileNode);

		tn.addChild(whileNode);
	}

	public void parseStat_2(TreeNode tn) throws SyntaxException{
		TreeNode forNode=new TreeNode(TreeNode.Label.forstat, tn);
		parse(forNode);

		tn.addChild(forNode);
	}

	public void parseStat_3(TreeNode tn) throws SyntaxException{
		TreeNode ifNode=new TreeNode(TreeNode.Label.ifstat, tn);
		parse(ifNode);

		tn.addChild(ifNode);
	}

	public void parseStat_4(TreeNode tn) throws SyntaxException{
		TreeNode assignNode=new TreeNode(TreeNode.Label.assign, tn);
		parse(assignNode);

		tn.addChild(assignNode);
	}

	public void parseStat_5(TreeNode tn) throws SyntaxException{
		TreeNode declNode=new TreeNode(TreeNode.Label.decl, tn);
		parse(declNode);

		tn.addChild(declNode);
	}

	public void parseStat_6(TreeNode tn) throws SyntaxException{
		TreeNode printNode=new TreeNode(TreeNode.Label.print, tn);
		parse(printNode);

		tn.addChild(printNode);
	}

	public void parseStat_7(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.SEMICOLON);
		TreeNode semi1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.SEMICOLON), tn);

		tn.addChild(semi1);
	}

	public void parseWhile_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.WHILE);
		TreeNode while_1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.WHILE), tn);

		expect(Token.TokenType.LPAREN);
		TreeNode lpa1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LPAREN), tn);

		TreeNode bool_exprNode=new TreeNode(TreeNode.Label.boolexpr, tn);
		parse(bool_exprNode);
		

		
		TreeNode rel_exprNode=new TreeNode(TreeNode.Label.relexpr, tn);
		parse(rel_exprNode);
		

		expect(Token.TokenType.RPAREN);
		TreeNode rpa1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.RPAREN), tn);

		expect(Token.TokenType.LBRACE);
		TreeNode lbra1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LBRACE), tn);
		
		TreeNode losNode=new TreeNode(TreeNode.Label.los, tn);
		parse(losNode);

		expect(Token.TokenType.RBRACE);
		TreeNode rbra1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.RBRACE), tn);
		
		tn.addChild(while_1);
		tn.addChild(lpa1);
		tn.addChild(bool_exprNode);
		tn.addChild(rel_exprNode);
		tn.addChild(rpa1);
		tn.addChild(lbra1);
		tn.addChild(losNode);
		tn.addChild(rbra1);
	}

	public void parseFor_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.FOR);
		TreeNode for_1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.FOR), tn);

		expect(Token.TokenType.LPAREN);
		TreeNode lpa1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LPAREN), tn);

		TreeNode for_startNode=new TreeNode(TreeNode.Label.forstart, tn);
		parse(for_startNode);

		expect(Token.TokenType.SEMICOLON);
		TreeNode semi_1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.SEMICOLON), tn);

		TreeNode rel_exprNode=new TreeNode(TreeNode.Label.relexpr, tn);
		parse(rel_exprNode);

		TreeNode bool_exprNode=new TreeNode(TreeNode.Label.boolexpr, tn);
		parse(bool_exprNode);

		expect(Token.TokenType.SEMICOLON);
		TreeNode semi_2 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.SEMICOLON), tn);

		TreeNode for_arithNode=new TreeNode(TreeNode.Label.forarith, tn);
		parse(for_arithNode);

		expect(Token.TokenType.RPAREN);
		TreeNode rpa1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.RPAREN), tn);

		expect(Token.TokenType.LBRACE);
		TreeNode lbra1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LBRACE), tn);

		TreeNode losNode=new TreeNode(TreeNode.Label.los, tn);
		parse(losNode);	

		expect(Token.TokenType.RBRACE);
		TreeNode rbra1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.RBRACE), tn);

		tn.addChild(for_1);
		tn.addChild(lpa1);
		tn.addChild(for_startNode);
		tn.addChild(semi_1);
		tn.addChild(rel_exprNode);
		tn.addChild(bool_exprNode);
		tn.addChild(semi_2);
		tn.addChild(for_arithNode);
		tn.addChild(rpa1);
		tn.addChild(lbra1);
		tn.addChild(losNode);
		tn.addChild(rbra1);
	}

	public void parseFor_Start_1(TreeNode tn) throws SyntaxException{
		TreeNode declNode=new TreeNode(TreeNode.Label.decl, tn);
		parse(declNode);

		tn.addChild(declNode);
	}

	public void parseFor_Start_2(TreeNode tn) throws SyntaxException{
		TreeNode assignNode=new TreeNode(TreeNode.Label.assign, tn);
		parse(assignNode);

		tn.addChild(assignNode);
	}

	public void parseFor_Start_3(TreeNode tn) throws SyntaxException{
		TreeNode epsilonNode = new TreeNode(TreeNode.Label.epsilon, tn);

    	// Add the epsilon node as a child to the parent node
    	tn.addChild(epsilonNode);
	}

	public void parseFor_Arith_1(TreeNode tn) throws SyntaxException{
		TreeNode arith_exprNode=new TreeNode(TreeNode.Label.arithexpr, tn);
		parse(arith_exprNode);

		tn.addChild(arith_exprNode);
	}

	public void parseFor_Arith_2(TreeNode tn) throws SyntaxException{
		TreeNode epsilonNode = new TreeNode(TreeNode.Label.epsilon, tn);

    	// Add the epsilon node as a child to the parent node
    	tn.addChild(epsilonNode);
	}

	public void parseIf_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.IF);
		TreeNode if_1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.IF), tn);

		expect(Token.TokenType.LPAREN);
		TreeNode lpa1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LPAREN), tn);

		TreeNode rel_exprNode=new TreeNode(TreeNode.Label.relexpr, tn);
		parse(rel_exprNode);

		TreeNode bool_exprNode=new TreeNode(TreeNode.Label.boolexpr, tn);
		parse(bool_exprNode);

		expect(Token.TokenType.RPAREN);
		TreeNode rpa1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.RPAREN), tn);

		expect(Token.TokenType.LBRACE);
		TreeNode lbra1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LBRACE), tn);

		TreeNode losNode=new TreeNode(TreeNode.Label.los, tn);
		parse(losNode);

		expect(Token.TokenType.RBRACE);
		TreeNode rbra1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.RBRACE), tn);

		TreeNode else_ifNode=new TreeNode(TreeNode.Label.los, tn);
		parse(else_ifNode);

		tn.addChild(if_1);
		tn.addChild(lpa1);
		tn.addChild(rel_exprNode);
		tn.addChild(bool_exprNode);
		tn.addChild(rpa1);
		tn.addChild(lbra1);
		tn.addChild(losNode);
		tn.addChild(rbra1);
		tn.addChild(else_ifNode);

	}

	public void parseElse_If_1(TreeNode tn) throws SyntaxException{
		TreeNode else_iffNode=new TreeNode(TreeNode.Label.elseorelseif, tn);
		parse(else_iffNode);

		expect(Token.TokenType.LBRACE);
		TreeNode lbra1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LBRACE), tn);

		TreeNode losNode=new TreeNode(TreeNode.Label.los, tn);
		parse(losNode);


		expect(Token.TokenType.RBRACE);
		TreeNode rbra1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.RBRACE), tn);

		TreeNode else_ifNode=new TreeNode(TreeNode.Label.elseifstat, tn);
		parse(else_ifNode);

		tn.addChild(else_ifNode);
		tn.addChild(lbra1);
		tn.addChild(losNode);
		tn.addChild(rbra1);
		tn.addChild(else_ifNode);
	}

	public void parseElse_If_2(TreeNode tn) throws SyntaxException{
		TreeNode epsilonNode = new TreeNode(TreeNode.Label.epsilon, tn);

    	// Add the epsilon node as a child to the parent node
    	tn.addChild(epsilonNode);
	}

	public void parseElse_Iff_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.ELSE);
		TreeNode else_1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.ELSE), tn);

		TreeNode poss_ifNode=new TreeNode(TreeNode.Label.possif, tn);
		parse(poss_ifNode);

		tn.addChild(else_1);
		tn.addChild(poss_ifNode);
	}

	public void parsePoss_If_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.IF);
		TreeNode if_1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.IF), tn);

		expect(Token.TokenType.LPAREN);
		TreeNode lpa1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LPAREN), tn);

		TreeNode rel_exprNode=new TreeNode(TreeNode.Label.relexpr, tn);
		parse(rel_exprNode);

		TreeNode bool_exprNode=new TreeNode(TreeNode.Label.boolexpr, tn);
		parse(bool_exprNode);

		expect(Token.TokenType.RPAREN);
		TreeNode rpa1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.RPAREN), tn);

		tn.addChild(if_1);
		tn.addChild(lpa1);
		tn.addChild(rel_exprNode);
		tn.addChild(bool_exprNode);
		tn.addChild(rpa1);
	}



	// Poss_If_2
	public void parsePoss_If_2(TreeNode tn) {
		TreeNode epsilonNode = new TreeNode(TreeNode.Label.epsilon, tn);
		tn.addChild(epsilonNode);
	}

	// assign_1
	public void parseAssign_1(TreeNode tn) throws SyntaxException{
		Token value = lookahead;
		expect(Token.TokenType.ID);
        TreeNode id =new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.ID,value.getValue().orElse("")), tn);
		
		expect(Token.TokenType.ASSIGN);
		TreeNode assign = new TreeNode (TreeNode.Label.terminal, new Token(Token.TokenType.ASSIGN),tn);

		TreeNode expr = new TreeNode(TreeNode.Label.expr, tn);
		parse(expr);

		tn.addChild(id);
		tn.addChild(assign);
		tn.addChild(expr);
	}

	// decl_1
	public void parseDecl_1(TreeNode tn) throws SyntaxException{
		TreeNode type = new TreeNode(TreeNode.Label.type, tn);
		parse(type);
	
		Token value = lookahead;
		expect(Token.TokenType.ID);
        TreeNode id =new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.ID,value.getValue().orElse("")), tn);
		
		TreeNode poss_asign = new TreeNode (TreeNode.Label.possassign,tn);
		parse(poss_asign);

		tn.addChild(type);
		tn.addChild(id);
		tn.addChild(poss_asign);
	}

	// poss_assign_1
	public void parsePoss_Assign_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.ASSIGN);
		TreeNode assign = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.ASSIGN),tn);

		TreeNode expr = new TreeNode(TreeNode.Label.expr, tn);
		parse(expr);

		tn.addChild(assign);
		tn.addChild(expr);
	}

	// poss_assign_2
	public void parsePoss_Assign_2(TreeNode tn) {
		TreeNode epsilonNode = new TreeNode(TreeNode.Label.epsilon, tn);
		tn.addChild(epsilonNode);
	}

	// print_1
	public void parsePrint_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.PRINT);
		TreeNode pri = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.PRINT),tn);

		TreeNode print_expr = new TreeNode(TreeNode.Label.printexpr,tn);
		parse(print_expr);

		tn.addChild(pri);
		tn.addChild(print_expr);
	}

	// type_1
	public void parseType_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.TYPE);
		TreeNode integer = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.TYPE,"int"),tn);

		tn.addChild(integer);
	}
	// type_2
	public void parseType_2(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.TYPE);
		TreeNode bool = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.TYPE,"boolean"),tn);

		tn.addChild(bool);
	}
	// type_3
	public void parseType_3(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.TYPE);
		TreeNode character = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.TYPE,"char"),tn);

		tn.addChild(character);
	}

	// expr_1
	public void parseExpr_1(TreeNode tn) throws SyntaxException{
		TreeNode rel_expr = new TreeNode(TreeNode.Label.relexpr, tn);
		parse(rel_expr);

		TreeNode bool_expr = new TreeNode(TreeNode.Label.boolexpr, tn);
		parse(bool_expr);

		tn.addChild(rel_expr);
		tn.addChild(bool_expr);

	}

	// expr_2
	public void parseExpr_2(TreeNode tn) throws SyntaxException{
		TreeNode char_expr = new TreeNode(TreeNode.Label.charexpr, tn);
		parse(char_expr);

		tn.addChild(char_expr);
	}

	// char_expr_1
	public void parseChar_Expr_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.SQUOTE);
		TreeNode l_quote = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.SQUOTE), tn);

		Token character = lookahead;
		expect(Token.TokenType.CHARLIT);
		TreeNode character_lit = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.CHARLIT, character.getValue().orElse("")), tn);

		expect(Token.TokenType.SQUOTE);
		TreeNode r_quote = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.SQUOTE), tn);

		tn.addChild(l_quote);
		tn.addChild(character_lit);
		tn.addChild(r_quote);
	}

	// bool_expr_1
	public void parseBool_Expr_1(TreeNode tn) throws SyntaxException{
		TreeNode bool_op = new TreeNode(TreeNode.Label.boolop, tn);
		parse(bool_op);

		TreeNode rel_expr = new TreeNode(TreeNode.Label.relexpr, tn);
		parse(rel_expr);

		TreeNode bool_expr = new TreeNode(TreeNode.Label.boolexpr, tn);
		parse(bool_expr);

		tn.addChild(bool_op);
		tn.addChild(rel_expr);
		tn.addChild(bool_expr);
	}

	// bool_expr_2
	public void parseBool_Expr_2(TreeNode tn) {
		TreeNode epsilon = new TreeNode(TreeNode.Label.epsilon, tn);
		tn.addChild(epsilon);
	}

	// bool_op_1
	public void parseBool_Op_1(TreeNode tn) throws SyntaxException{
		TreeNode bool_eq = new TreeNode (TreeNode.Label.booleq,tn);
		parse(bool_eq);

		tn.addChild(bool_eq);
	}

	// bool_op_2
	public void parseBool_Op_2(TreeNode tn) throws SyntaxException{
		TreeNode bool_log = new TreeNode(TreeNode.Label.boollog, tn);
		parse(bool_log);
	}

	// bool_eq_1
	public void parseBool_Eq_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.EQUAL);
		TreeNode equal = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.EQUAL),tn);
		tn.addChild(equal);
	}

	// bool_eq_2
	public void parseBool_Eq_2(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.NEQUAL);
		TreeNode n_equal = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.NEQUAL),tn);
		tn.addChild(n_equal);
	}

	// bool_log_1
	public void parseBool_Log_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.AND);
		TreeNode and = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.AND),tn);
		tn.addChild(and);
	}
	// bool_log_2
	public void parseBool_Log_2(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.OR);
		TreeNode or = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.OR),tn);
		tn.addChild(or);
	}
	// rel_expr_1
	public void parseRel_Expr_1(TreeNode tn) throws SyntaxException{
		TreeNode arith_expr = new TreeNode(TreeNode.Label.arithexpr, tn);
		parse(arith_expr);

		TreeNode rel_exprr = new TreeNode(TreeNode.Label.relexprprime, tn);
		parse(rel_exprr);

		tn.addChild(arith_expr);
		tn.addChild(rel_exprr);
	}
	// rel_expr_2
	public void parseRel_Expr_2(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.TRUE);
		TreeNode t = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.TRUE),tn);
		tn.addChild(t);
	}
	// rel_expr_3
	public void parseRel_Expr_3(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.FALSE);
		TreeNode f = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.FALSE),tn);
		tn.addChild(f);
	}
	
	public void parseRel_Exprr_1(TreeNode tn) throws SyntaxException{
		TreeNode relOpNode = new TreeNode(TreeNode.Label.relop, tn);
		parse(relOpNode);

		TreeNode arithExprNode = new TreeNode(TreeNode.Label.arithexpr, tn);
		parse(arithExprNode);

		tn.addChild(relOpNode);
		tn.addChild(arithExprNode);
	}

	public void parseRel_Exprr_2(TreeNode tn) throws SyntaxException{
		TreeNode epsilonNode = new TreeNode(TreeNode.Label.epsilon, tn);
		tn.addChild(epsilonNode);
	}

	public void parseRel_Op_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.LT);
		TreeNode lt = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LT), tn);

		tn.addChild(lt);
	}

	public void parseRel_Op_2(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.LE);
		TreeNode le = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LE), tn);

		tn.addChild(le);
	}

	public void parseRel_Op_3(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.GT);
		TreeNode gt = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.GT), tn);

		tn.addChild(gt);
	}

	public void parseRel_Op_4(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.GE);
		TreeNode ge = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.GE), tn);

		tn.addChild(ge);
	}

	public void parseArith_Expr_1(TreeNode tn) throws SyntaxException{
		TreeNode termNode = new TreeNode(TreeNode.Label.term, tn);
		parse(termNode);

		TreeNode arithExprrNode = new TreeNode(TreeNode.Label.arithexprprime, tn);
		parse(arithExprrNode);
		
		tn.addChild(termNode);
		tn.addChild(arithExprrNode);
	}

	public void parseArith_Exprr_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.PLUS);
		TreeNode plu = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.PLUS), tn);

		TreeNode termNode = new TreeNode(TreeNode.Label.term, tn);
		parse(termNode);

		TreeNode arithExprrNode = new TreeNode(TreeNode.Label.arithexprprime, tn);
		parse(arithExprrNode);
		
		tn.addChild(termNode);
		tn.addChild(arithExprrNode);
		tn.addChild(plu);
	}
	
	public void parseArith_Exprr_2(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.MINUS);
		TreeNode min = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.MINUS), tn);

		TreeNode termNode = new TreeNode(TreeNode.Label.term, tn);
		parse(termNode);

		TreeNode arithExprrNode = new TreeNode(TreeNode.Label.arithexprprime, tn);
		parse(arithExprrNode);
		
		tn.addChild(termNode);
		tn.addChild(arithExprrNode);
		tn.addChild(min);
	}

	public void parseArith_Exprr_3(TreeNode tn) throws SyntaxException{
		TreeNode epsilonNode = new TreeNode(TreeNode.Label.epsilon, tn);
		tn.addChild(epsilonNode);
	}

	public void parseTerm_1(TreeNode tn) throws SyntaxException{
		TreeNode factNode = new TreeNode(TreeNode.Label.factor, tn);
		parse(factNode);
		
		TreeNode termmNode = new TreeNode(TreeNode.Label.termprime, tn);
		parse(termmNode);

		tn.addChild(factNode);
		tn.addChild(termmNode);
	}

	public void parseTermm_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.TIMES);
		TreeNode tim = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.TIMES), tn);

		TreeNode factNode = new TreeNode(TreeNode.Label.factor, tn);
		parse(factNode);
		
		TreeNode termmNode = new TreeNode(TreeNode.Label.termprime, tn);
		parse(termmNode);

		tn.addChild(tim);
		tn.addChild(factNode);
		tn.addChild(termmNode);
	}

	public void parseTermm_2(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.DIVIDE);
		TreeNode div = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.DIVIDE), tn);

		TreeNode factNode = new TreeNode(TreeNode.Label.factor, tn);
		parse(factNode);
		
		TreeNode termmNode = new TreeNode(TreeNode.Label.termprime, tn);
		parse(termmNode);

		tn.addChild(div);
		tn.addChild(factNode);
		tn.addChild(termmNode);
	}

	public void parseTermm_3(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.MOD);
		TreeNode mod = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.MOD), tn);

		TreeNode factNode = new TreeNode(TreeNode.Label.factor, tn);
		parse(factNode);
		
		TreeNode termmNode = new TreeNode(TreeNode.Label.termprime, tn);
		parse(termmNode);

		tn.addChild(mod);
		tn.addChild(factNode);
		tn.addChild(termmNode);
	}

	public void parseTermm_4(TreeNode tn) throws SyntaxException{
		TreeNode epsilonNode = new TreeNode(TreeNode.Label.epsilon, tn);
		tn.addChild(epsilonNode);
	}

	public void parseFactor_1(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.LPAREN);
		TreeNode lpa = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.LPAREN),tn);

		TreeNode arithExprNode = new TreeNode(TreeNode.Label.arithexpr, tn);
		parse(arithExprNode);

		expect(Token.TokenType.RPAREN);
		TreeNode rpa = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.RPAREN),tn);

		tn.addChild(lpa);
		tn.addChild(arithExprNode);
		tn.addChild(rpa);
	}

	public void parseFactor_2(TreeNode tn) throws SyntaxException{
		Token classname = lookahead;
		expect(Token.TokenType.ID);
        TreeNode id=new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.ID, classname.getValue().orElse("")), tn);

		tn.addChild(id);
	}

	public void parseFactor_3(TreeNode tn) throws SyntaxException{
		Token classname = lookahead;
		expect(Token.TokenType.NUM);
        TreeNode num = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.NUM, classname.getValue().orElse("")), tn);
 
		tn.addChild(num);
	}

	public void parsePrint_Expr_1(TreeNode tn) throws SyntaxException{
		TreeNode relExprNode = new TreeNode(TreeNode.Label.relexpr, tn);
		parse(relExprNode);
		
		TreeNode booExprNode = new TreeNode(TreeNode.Label.boolexpr, tn);
		parse(booExprNode);

		tn.addChild(relExprNode);
		tn.addChild(booExprNode);
	}

	public void parsePrint_Expr_2(TreeNode tn) throws SyntaxException{
		expect(Token.TokenType.DQUOTE);
		TreeNode dqu = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.DQUOTE), tn);

		Token classname = lookahead;
		TreeNode strLitNode = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.NUM, classname.getValue().orElse("")), tn);

		expect(Token.TokenType.DQUOTE);
		TreeNode dqu1 = new TreeNode(TreeNode.Label.terminal, new Token(Token.TokenType.DQUOTE), tn); 

		tn.addChild(dqu);
		tn.addChild(strLitNode);
		tn.addChild(dqu1);
	}
}

// The following class may be helpful.

class Pair<A, B> {
	private final A a;
	private final B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public A fst() {
		return a;
	}

	public B snd() {
		return b;
	}

	@Override
	public int hashCode() {
		return 3 * a.hashCode() + 7 * b.hashCode();
	}

	@Override
	public String toString() {
		return "{" + a + ", " + b + "}";
	}

	@Override
	public boolean equals(Object o) {
		if ((o instanceof Pair<?, ?>)) {
			Pair<?, ?> other = (Pair<?, ?>) o;
			return other.fst().equals(a) && other.snd().equals(b);
		}

		return false;
	}

}
