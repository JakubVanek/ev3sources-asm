package logo;

public class DottedSymbol {
	public Symbol sym;

	DottedSymbol(Symbol var1) {
		this.sym = var1;
	}

	public String toString() {
		return ":" + this.sym.toString();
	}
}
