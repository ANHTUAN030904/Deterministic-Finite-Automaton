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
		parsingTable.put(new Pair<>(TreeNode.Label.prog, Token.TokenType.PUBLIC), Rule.PROG_RULE);
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
