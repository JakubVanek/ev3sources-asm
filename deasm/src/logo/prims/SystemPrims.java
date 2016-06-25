package logo.prims;

import logo.*;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class SystemPrims implements Primitives {
	private static String[] primlist = new String[]{
			"resett", "0", // resett // reset timer
			"timer", "0", // timer // get timer value from last reset
			"eq", "2", // eq <a> <b> // check if references are equal
			"(", "0", // ( // left parenthesis - run subblock
			")", "0", // ) // right parenthesis - error (valid one is processed with the left one)
			"wait", "1", // wait <time> // wait in tenths of second
			"true", "0", // true // get true
			"false", "0", // false // get false
			"hexw", "2", // hexw <num> <length> // convert number to hexadecimal and pad with zeros to the right length
			"octw", "2", // octw <num> <length> // convert number to octal and pad with zeros to the right length
			"tab", "0", // tab // get tab character
			"classof", "1", // classof <value> // get the class of value
			"class", "1", // class <name> // get class from name
			"string", "1", // string <value> // convert value to string
			"%nothing%", "0", // %nothing% // return special null-like type
			"print", "1", // print <value> // print to stdout
			"hexparse", "1", // hexparse <str> // parse hex string to a number
			"scanhex", "3", // scanhex <str> :dstlist [start1 len1 start2 len2 ...] // multiparse hex string
			"floatbits", "1"};

	public String[] primitives() {
		return primlist;
	}

	public Object dispatch(int code, Object[] args, LContext ctx) {
		switch (code) {
			case 0:
				return this.prim_resett(ctx);
			case 1:
				return this.prim_timer(ctx);
			case 2:
				return this.prim_eq(args[0], args[1], ctx);
			case 3:
				return this.prim_parleft(ctx);
			case 4:
				return this.prim_parright(ctx);
			case 5:
				return this.prim_wait(args[0], ctx);
			case 6:
				return this.prim_true();
			case 7:
				return this.prim_false();
			case 8:
				return this.prim_hexw(args[0], args[1], ctx);
			case 9:
				return this.prim_octw(args[0], args[1], ctx);
			case 10:
				return this.prim_tab();
			case 11:
				return this.prim_classof(args[0]);
			case 12:
				return this.prim_class(args[0]);
			case 13:
				return this.prim_string(args[0]);
			case 14:
				return Nothing.INSTANCE;
			case 15:
				ctx.stdout.println(Logo.serialize(args[0]));
				return null;
			case 16:
				return this.prim_hexparse(args[0], ctx);
			case 17:
				return this.prim_scanhex(args[0], args[1], args[2], ctx);
			case 18:
				return this.prim_floatbits(args[0], ctx);
			default:
				return null;
		}
	}

	private Object prim_resett(LContext ctx) {
		Logo.starttime = System.currentTimeMillis();
		return null;
	}

	private Object prim_timer(LContext ctx) {
		return (double) (System.currentTimeMillis() - Logo.starttime);
	}

	private Object prim_eq(Object a, Object b, LContext ctx) {
		return a.equals(b);
	}

	private Object prim_parright(LContext ctx) {
		Logo.error("Missing \"(\"", ctx);
		return null;
	}

	private Object prim_parleft(LContext ctx) {
		if (this.ipmFollows(ctx.iline)) {
			return this.ipmCall(ctx);
		} else {
			Object value = Logo.eval(ctx);
			Object next = ctx.iline.next();
			if (next instanceof Symbol && ((Symbol) next).pname.equals(")")) {
				return value;
			} else {
				Logo.error("Missing \")\"", ctx);
				return null;
			}
		}
	}

	private boolean ipmFollows(TokenChain chain) {
		try {
			return ((Symbol) chain.peek()).fcn.multiArgument;
		} catch (Exception ex) {
			return false;
		}
	}

	private Object ipmCall(LContext ctx) {
		List<Object> args = new ArrayList<>();
		ctx.current_function = (Symbol) ctx.iline.next();

		while (!this.ipmFinished(ctx.iline)) {
			args.add(Logo.evalOneArg(ctx.iline, ctx));
		}

		Object[] argarray = args.toArray();
		return Logo.evalSym(ctx.current_function, argarray, ctx);
	}

	private boolean ipmFinished(TokenChain chain) {
		if (chain.eof()) {
			return true;
		} else {
			Object next = chain.peek();
			if (next instanceof Symbol && ((Symbol) next).pname.equals(")")) {
				chain.next();
				return true;
			} else {
				return false;
			}
		}
	}

	private Object prim_wait(Object time, LContext ctx) {
		double hundreths = 10.0D * Logo.toDouble(time, ctx);
		int count = (int) hundreths;

		for (int i = 0; i < count; ++i) {
			if (ctx.stop_requested) {
				return null;
			}

			try {
				Thread.sleep(10L);
			} catch (InterruptedException ignored) {
			}
		}

		return null;
	}

	private Object prim_hexw(Object number, Object length, LContext ctx) {
		Logo.toInt(number, ctx);
		String str = Logo.serialize(number, 16);
		int len = Logo.toInt(length, ctx);
		String prefix = "00000000".substring(8 - len + str.length());
		return prefix + str;
	}

	private Object prim_octw(Object number, Object length, LContext ctx) {
		Logo.toInt(number, ctx);
		String str = Logo.serialize(number, 8);
		int len = Logo.toInt(length, ctx);
		String prefix = "00000000".substring(8 - len + str.length());
		return prefix + str;
	}

	private Object prim_true() {
		return true;
	}

	private Object prim_false() {
		return false;
	}

	private Object prim_tab() {
		return "\t";
	}

	private Object prim_classof(Object obj) {
		return obj.getClass();
	}

	private Object prim_class(Object name) {
		try {
			return Class.forName(Logo.serialize(name));
		} catch (Exception e) {
			return "";
		} catch (Error e) {
			return "";
		}
	}

	private Object prim_string(Object obj) {
		return this.prstring(obj);
	}

	private String prstring(Object obj) {
		if (obj instanceof Number && Logo.isInteger((Number) obj)) {
			return Long.toString(((Number) obj).longValue(), 10);
		} else if (obj instanceof String) {
			return "\'" + obj + "\'";
		} else if (obj instanceof Object[]) {
			StringBuilder str = new StringBuilder();
			Object[] list = (Object[]) obj;

			for (int i = 0; i < list.length; i++) {
				boolean array_level = list[i] instanceof Object[];
				if (array_level) {
					str.append('[');
				}

				str.append(this.prstring(list[i]));
				if (array_level) {
					str.append(']');
				}

				if (i != list.length - 1) {
					str.append(' ');
				}
			}

			return str.toString();
		} else {
			return obj.toString();
		}
	}

	private Object prim_hexparse(Object value, LContext ctx) {
		TokenStream stream = new TokenStream(Logo.serialize(value), true);
		return stream.readList(ctx);
	}

	private Object prim_scanhex(Object toScan, Object destination, Object ranges_obj, LContext ctx) {
		String str = (String) toScan;
		Object[] dst = (Object[]) destination;
		Object[] ranges = (Object[]) ranges_obj;

		for (int i = 0; i < dst.length; i++) {
			int start = ((Number) ranges[i * 2]).intValue();
			int end = ((Number) ranges[i * 2 + 1]).intValue() + start;
			String num_str = str.substring(start, end);
			dst[i] = Long.parseLong(num_str, 16);
		}

		return null;
	}

	private Object prim_floatbits(Object number, LContext ctx) {
		float flt = (float) Logo.toDouble(number, ctx);
		return (double) Float.floatToIntBits(flt);
	}
}
