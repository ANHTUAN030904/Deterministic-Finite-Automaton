import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Rule;

public class SyntacticAnalyser {

	enum Rule {
		PROG_RULE, LOS_RULE, STAT_RULE, WHILE_RULE, FOR_RULE, IF_RULE, ELSE_IF_RULE, ASSIGN_RULE, DECL_RULE, PRINT_RULE,
		REL_EXPR_RULE, BOOL_EXPR_RULE, CHAR_EXPR_RULE, ARITH_EXPR_RULE, TERM_RULE, FACTOR_RULE, PRINT_EXPR_RULE
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
		eeeeeeeeee sdfasbfdsf
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
