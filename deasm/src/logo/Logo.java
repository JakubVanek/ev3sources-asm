package logo;

import logo.prims.Primitives;
import logo.prims.Ufun;

public class Logo {
	public static long starttime = System.currentTimeMillis();

	static String runToplevel(Object[] tokens, LContext context) {
		context.iline = new TokenChain(tokens);
		context.stop_requested = false;

		try {
			processAllTokens(context);
		} catch (LogoError lErr) {
			if (lErr.getMessage() != null) {
				return lErr.getMessage();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ex.toString();
		} catch (Error err) {
			return err.toString();
		}

		return null;
	}

	private static void processAllTokens(LContext ctx) {
		while (!ctx.iline.eof() && ctx.ufunresult == null) {
			Object result = eval(ctx);
			if (result != null) {
				error("You don\'t say what to do with " + serialize(result), ctx);
			}
		}

	}

	public static Object eval(LContext ctx) {
		Object result;
		for (result = evalToken(ctx); infixNext(ctx.iline, ctx); result = evalInfix(result, ctx)) {
			if (result instanceof Nothing) {
				error(ctx.iline.peek() + " needs more inputs", ctx);
			}
		}

		return result;
	}

	private static Object evalToken(LContext ctx) {
		Object token = ctx.iline.next();
		if (token instanceof QuotedSymbol)
			return ((QuotedSymbol) token).sym;
		if (token instanceof DottedSymbol)
			return getValue((DottedSymbol) token, ctx);
		if (token instanceof Symbol)
			return evalSym((Symbol) token, null, ctx);
		return token;
	}

	public static Object evalSym(Symbol function, Object[] custom_args, LContext ctx) {
		if (ctx.stop_requested) {
			ctx.stop_requested = false;
			error("Stopped!!!", ctx);
		}

		if (function.fcn == null) {
			error("I don\'t know how to " + function, ctx);
		}

		Symbol parent_function = ctx.current_function;
		ctx.current_function = function;
		int old_priority = ctx.priority;
		ctx.priority = 0;
		Object result = null;

		try {
			Function func = function.fcn;
			int nargs = func.nargs;
			if (custom_args == null) {
				custom_args = evalArgs(nargs, ctx);
			}

			result = func.implementation.dispatch(func.dispatchOffset, custom_args, ctx);
		} catch (RuntimeException ex) {
			errorHandler(function, custom_args, ex, ctx);
		} finally {
			ctx.current_function = parent_function;
			ctx.priority = old_priority;
		}

		if (ctx.willOutput && result == null) {
			error(function + " didn\'t output to " + ctx.current_function, ctx);
		}

		return result;
	}

	private static Object[] evalArgs(int nargs, LContext ctx) {
		boolean old_output = ctx.willOutput;
		ctx.willOutput = true;
		Object[] args = new Object[nargs];

		try {
			for (int i = 0; i < nargs; ++i) {
				if (ctx.iline.eof()) {
					error(ctx.current_function + " needs more inputs", ctx);
				}

				args[i] = eval(ctx);
				if (args[i] instanceof Nothing) {
					error(ctx.current_function + " needs more inputs", ctx);
				}
			}
		} finally {
			ctx.willOutput = old_output;
		}

		return args;
	}

	public static void runCommand(Object[] tokens, LContext ctx) {
		boolean temp = ctx.willOutput;
		ctx.willOutput = false;

		try {
			runList(tokens, ctx);
		} finally {
			ctx.willOutput = temp;
		}

	}

	public static Object runList(Object[] tokens, LContext ctx) {
		TokenChain oldCode = ctx.iline;
		ctx.iline = new TokenChain(tokens);
		Object result = null;

		try {
			if (ctx.willOutput) {
				result = eval(ctx);
			} else {
				processAllTokens(ctx);
			}

			checkChainEmpty(ctx.iline, ctx);
		} finally {
			ctx.iline = oldCode;
		}

		return result;
	}

	public static Object evalOneArg(TokenChain code, LContext ctx) {
		boolean tempOutput = ctx.willOutput;
		ctx.willOutput = true;
		TokenChain oldCode = ctx.iline;
		ctx.iline = code;

		Object result;
		try {
			result = eval(ctx);
		} finally {
			ctx.iline = oldCode;
			ctx.willOutput = tempOutput;
		}

		return result;
	}

	private static boolean infixNext(TokenChain chain, LContext ctx) {
		Object token;
		if (!chain.eof() && (token = chain.peek()) instanceof Symbol) {
			Function func = ((Symbol) token).fcn;
			if (func != null && func.nargs < ctx.priority) {
				return true;
			}
		}

		return false;
	}

	private static Object evalInfix(Object prev, LContext ctx) {
		Symbol op = (Symbol) ctx.iline.next();
		Function func = op.fcn;
		Symbol old_function = ctx.current_function;
		ctx.current_function = op;
		int old_priority = ctx.priority;
		ctx.priority = func.nargs;
		Object result = null;
		Object[] args = new Object[]{prev, null};

		try {
			Object[] arg = evalArgs(1, ctx);
			args[1] = arg[0];
			result = func.implementation.dispatch(func.dispatchOffset, args, ctx);
		} catch (RuntimeException ex) {
			errorHandler(op, args, ex, ctx);
		} finally {
			ctx.current_function = old_function;
			ctx.priority = old_priority;
		}

		if (ctx.willOutput && result == null) {
			error(op + " didn\'t output to " + ctx.current_function, ctx);
		}

		return result;
	}

	static Symbol querySymbol(String name, LContext ctx) {
		Symbol existing = ctx.oblist.get(name);
		if (existing == null) {
			ctx.oblist.put(name, existing = new Symbol(name));
		}

		return existing;
	}

	public static Object[] parse(String str_list, LContext ctx) {
		TokenStream stream = new TokenStream(str_list);
		return stream.readList(ctx);
	}

	public static String serialize(Object obj) {
		return serialize(obj, 10);
	}

	private static void serialize_array(Object[] array, StringBuilder builder) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] instanceof Object[]) {
				builder.append('[');
				serialize_array((Object[]) array[i], builder);
				builder.append(']');
			} else {
				builder.append(serialize(array[i]));
			}
			if (i != array.length - 1) {
				builder.append(' ');
			}
		}
	}

	public static String serialize(Object obj, int base) {
		if (obj instanceof Number) {
			Number num = (Number) obj;
			switch (base) {
				case 8:
					return Long.toString(num.longValue(), 8);
				case 16:
					return Long.toString(num.longValue(), 16).toUpperCase();
			}
			if (isInteger(num))
				return Long.toString(num.longValue(), 10);
		} else if (obj instanceof Object[]) {
			StringBuilder builder = new StringBuilder();

			serialize_array((Object[]) obj, builder);

			return builder.toString();
		}
		return obj.toString();
	}

	public static boolean isInteger(Number number) {
		return number.doubleValue() == (new Long(number.longValue())).doubleValue();
	}

	static boolean charIs(char ch, String str) {
		return str.indexOf(ch) != -1;
	}

	static boolean isValidNumber(String toTest) {
		if (toTest.length() == 0)
			return false;
		char first = toTest.charAt(0);
		if (toTest.length() == 1 && !charIs(first, "0123456789"))
			return false;
		else if (!charIs(first, "eE.+-0123456789"))
			return false;
		else {
			for (int i = 1; i < toTest.length(); ++i) {
				if (!charIs(toTest.charAt(i), "eE.0123456789")) {
					return false;
				}
			}

			return true;
		}
	}

	public static Object getValue(DottedSymbol envelope, LContext ctx) {
		return getValue(envelope.sym, ctx);
	}

	public static Object getValue(Symbol envelope, LContext ctx) {
		Object value = envelope.value;
		if (value != null) {
			return value;
		} else {
			error(envelope + " has no value", ctx);
			return null;
		}
	}

	public static void setValue(Symbol envelope, Object value) {
		envelope.value = value;
	}

	public static double toDouble(Object obj, LContext ctx) {
		if (obj instanceof Double) {
			return (double) obj;
		} else {
			String str = serialize(obj);
			if (str.length() > 0 && isValidNumber(str)) {
				return Double.valueOf(str);
			} else {
				error(ctx.current_function + " doesn\'t like " + serialize(obj) + " as input", ctx);
				return 0.0D;
			}
		}
	}

	public static int toInt(Object obj, LContext ctx) {
		if (obj instanceof Double) {
			return ((Double) obj).intValue();
		} else {
			String str = serialize(obj);
			if (isValidNumber(str)) {
				return Double.valueOf(str).intValue();
			} else {
				error(ctx.current_function + " doesn\'t like " + str + " as input", ctx);
				return 0;
			}
		}
	}

	public static long toLong(Object obj, LContext ctx) {
		if (obj instanceof Double) {
			return ((Double) obj).longValue();
		} else {
			String str = serialize(obj);
			if (isValidNumber(str)) {
				return Double.valueOf(str).longValue();
			} else {
				error(ctx.current_function + " doesn\'t like " + str + " as input", ctx);
				return 0L;
			}
		}
	}

	public static boolean toBool(Object obj, LContext ctx) {
		if (obj instanceof Boolean) {
			return (boolean) obj;
		} else if (obj instanceof Symbol) {
			return ((Symbol) obj).pname.equals("true");
		} else {
			error(ctx.current_function + " doesn\'t like " + serialize(obj) + " as input", ctx);
			return false;
		}
	}

	/*
		public static Object[] getListOfTwoDoubles(Object obj, LContext ctx) {
			if (obj instanceof Object[]) {
				Object[] arr = (Object[]) obj;
				if (arr.length == 2 && arr[0] instanceof Double && arr[1] instanceof Double) {
					return (Object[]) obj;
				}

				error(ctx.current_function + " doesn\'t like " + serialize(obj) + " as input", ctx);
			}

			return null;
		}
	*/
	public static Object[] toList(Object obj, LContext ctx) {
		if (obj instanceof Object[]) {
			return (Object[]) obj;
		} else {
			error(ctx.current_function + " doesn\'t like " + serialize(obj) + " as input", ctx);
			return null;
		}
	}

	public static Symbol toSymbol(Object obj, LContext ctx) {
		if (obj instanceof Symbol) {
			return (Symbol) obj;
		} else if (obj instanceof String) {
			return querySymbol((String) obj, ctx);
		} else if (obj instanceof Number) {
			String str = String.valueOf(((Number) obj).longValue());
			return querySymbol(str, ctx);
		} else {
			error(ctx.current_function + " doesn\'t like " + serialize(obj) + " as input", ctx);
			return null;
		}
	}

	public static String toString(Object obj, LContext ctx) {
		if (obj instanceof String) {
			return (String) obj;
		} else if (obj instanceof Symbol) {
			return obj.toString();
		} else {
			error(ctx.current_function + " doesn\'t like " + serialize(obj) + " as input", ctx);
			return null;
		}
	}

	static void setupPrimitives(String[] groups, LContext ctx) {
		for (String clazzname : groups) {
			setupPrimitives(clazzname, ctx);
		}
	}

	private static void setupPrimitives(String clazzname, LContext ctx) {
		try {
			Class clazz = Class.forName(clazzname);
			Primitives instance = (Primitives) clazz.newInstance();
			String[] list = instance.primitives();

			for (int i = 0; i < list.length; i += 2) {
				String nargs = list[i + 1];
				boolean multiarg = nargs.startsWith("i");
				if (multiarg) {
					nargs = nargs.substring(1);
				}

				Symbol sym = querySymbol(list[i], ctx);
				sym.fcn = new Function(instance, Integer.parseInt(nargs), i / 2, multiarg);
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}

	}

	public static void checkChainEmpty(TokenChain chain, LContext ctx) {
		if (!chain.eof() && ctx.ufunresult == null) {
			error("You don\'t say what to do with " + serialize(chain.next()), ctx);
		}

	}

	private static void errorHandler(Symbol sym, Object[] args, RuntimeException ex, LContext ctx) {
		if (!(ex instanceof ArrayIndexOutOfBoundsException ||
				ex instanceof StringIndexOutOfBoundsException ||
				ex instanceof NegativeArraySizeException)) {
			throw ex;
		} else {
			error(sym + " doesn\'t like {" + serialize(args) + "} as inputs", ctx);
		}
	}

	public static void error(String message, LContext ctx) {
		if (message.equals("")) {
			throw new LogoError(null);
		} else {
			message = message + (ctx.ufun == null ? "" : " in " + ctx.ufun);
			throw new LogoError(message);
		}
	}

	public static void readAllFunctions(String code, LContext ctx) {
		TokenStream stream = new TokenStream(code);

		while (true) {
			switch (findKeyWord(stream)) {
				case 0:
					return;
				case 1:
					doDefine(stream, ctx);
					break;
				case 2:
					doTo(stream, ctx);
					break;
			}
		}
	}

	private static int findKeyWord(TokenStream stream) {
		while (!stream.eof()) {
			if (stream.startsWith("define ")) {
				return 1;
			}

			if (stream.startsWith("to ")) {
				return 2;
			}

			stream.skipToNextLine();
		}

		return 0;
	}

	private static void doDefine(TokenStream stream, LContext ctx) {
		stream.readToken(ctx);
		Symbol func = toSymbol(stream.readToken(ctx), ctx);
		Object[] args = toList(stream.readToken(ctx), ctx);
		Object[] body = toList(stream.readToken(ctx), ctx);
		Ufun impl = new Ufun(args, body);
		func.fcn = new Function(impl, args.length, 0);
	}

	private static void doTo(TokenStream stream, LContext ctx) {
		Object[] title = parse(stream.nextLine(), ctx);
		Object[] body = parse(readBody(stream, ctx), ctx);
		Object[] args = getArglistFromTitle(title);
		Symbol func = toSymbol(title[1], ctx);
		Ufun impl = new Ufun(args, body);
		func.fcn = new Function(impl, args.length, 0);
	}

	private static String readBody(TokenStream stream, LContext ctx) {
		StringBuilder builder = new StringBuilder();

		String line;
		while (!stream.eof()) {
			line = stream.nextLine();
			if (line.startsWith("end") && "end".equals(((Symbol) parse(line, ctx)[0]).pname)) {
				return builder.toString();
			}
			builder.append(" ").append(line);
		}

		return builder.substring(0, builder.length() - 1);
	}

	private static Object[] getArglistFromTitle(Object[] line) {
		Object[] args = new Object[line.length - 2];

		for (int i = 0; i < args.length; i++) {
			args[i] = ((DottedSymbol) line[i + 2]).sym;
		}

		return args;
	}
}
