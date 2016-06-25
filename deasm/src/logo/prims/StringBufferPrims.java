package logo.prims;

import logo.LContext;
import logo.Logo;

@SuppressWarnings("unused")
public class StringBufferPrims implements Primitives {
	private static String[] primlist = new String[]{
			"clearstringbuffer", "0", // clearstringbuffer // empty the string buffer
			"addtostringbuffer", "1", // addtostringbuffer // add value to the string buffer
			"stringbuffer", "0", // stringbuffer // get the string buffer
			"addtostringbufferstart", "1"}; // addtostringbufferstart // prepend the string value with value
	private StringBuffer stringbuffer;

	public String[] primitives() {
		return primlist;
	}

	public Object dispatch(int code, Object[] args, LContext ctx) {
		switch (code) {
			case 0:
				this.stringbuffer = new StringBuffer();
				return null;
			case 1:
				this.stringbuffer.append(Logo.serialize(args[0]));
				return null;
			case 2:
				return this.stringbuffer.toString();
			case 3:
				this.stringbuffer.insert(0, Logo.serialize(args[0]));
				return null;
			default:
				return null;
		}
	}
}
