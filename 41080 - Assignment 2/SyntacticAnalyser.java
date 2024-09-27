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
		IF_1, ELSE_IF_1, ELSE_IF_2, ELSE_IFF_1, ELSE_IFF_2, POSS_IF_1, ASSIGN_1, DECL_1, 
		POSS_ASSIGN_1, POSS_ASSIGN_2, PRINT_1, TYPE_1, TYPE_2, TYPE_3, EXPR_1, EXPR_2, 
		CHAR_EXPR_1, BOOL_EXPR_1, BOOL_EXPR_2, BOOL_OP_1, BOOL_OP_2, BOOL_EQ_1, BOOL_EQ_2,
		BOOL_LOG_1, BOOL_LOG_2, REL_EXPR_1, REL_EXPR_2, REL_EXPR_3, REL_EXPRR_1, REL_EXPRR_2,
		REL_OP_1, REL_OP_2, REL_OP_3, REL_OP_4, ARITH_EXPR_1, ARITH_EXPRR_1, ARITH_EXPRR_2,
		ARITH_EXPRR_3, TERM_1, TERMM_1, TERMM_2, TERMM_3, TERMM_4, FACTOR_1, FACTOR_2,FACTOR_3,
		PRINT_EXPR_1, PRINT_EXPR_2
	}

	private Deque<Token> tokens;  // Token stream
    private Token lookahead;      // Current token being examined
	private Map<Pair<TreeNode.Label, Token.TokenType>, Rule> parsingTable = new HashMap<>();

    public SyntacticAnalyser(List<Token> tokens) {
        this.tokens = new ArrayDeque<>(tokens);
        this.lookahead = this.tokens.poll();  // Initialize the first token
    }

    // Move to the next token
    private void consume() {
        this.lookahead = this.tokens.poll();
    }

    // Expect a specific token and consume it if it matches
    private void expect(Token.TokenType expectedType) throws SyntaxException {
        if (lookahead.getType() != expectedType) {
            throw new SyntaxException("wrong");
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
        parsingTable.put(new Pair<>(TreeNode.Label.possif, Token.TokenType.LBRACE), Rule.POSS_IF_1);

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
        parsingTable.put(new Pair<>(TreeNode.Label.type, Token.TokenType.TYPE), Rule.TYPE_1);

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

		return new ParseTree();
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
