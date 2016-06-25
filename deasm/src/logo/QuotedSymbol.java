package logo;

public class QuotedSymbol {
	Symbol sym;

	public QuotedSymbol(Symbol value) {
		this.sym = value;
	}

	public String toString() {
		return "\"" + this.sym.toString();
	}
}
