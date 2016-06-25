package logo;

public class TokenChain {
	private Object[] tokens;
	private int offset = 0;

	public TokenChain(Object[] tokens) {
		this.tokens = tokens;
	}

	public Object next() {
		return this.tokens[this.offset++];
	}

	public Object peek() {
		return this.tokens[this.offset];
	}

	public boolean eof() {
		return this.offset == this.tokens.length;
	}
}
