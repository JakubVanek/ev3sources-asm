package logo;

public class Symbol {
	public String pname;
	public Function fcn;
	public Object value;

	Symbol(String name) {
		this.pname = name;
	}

	public String toString() {
		return this.pname;
	}
}
