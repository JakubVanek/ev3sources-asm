package logo.prims;

import logo.*;

import java.util.*;

@SuppressWarnings("unused")
public class DefiningPrims implements Primitives {
	private static String[] primlist = new String[]{
			"make", "2", // make <variable name> <value> // assignment operator
			"define", "3", // define <name> [argument list] [body] // define a new function
			"let", "1", // let [<var1> <value> <var2> <value> ...] // series of assignments with scope???
			"thing", "1", // thing <variable name> // resolve a variable name to its value
			"put", "3", // put <map name> <key> <value> // put a value in a named hashmap
			"get", "2", // get <map name> <key> // get a value from a named hashmap
			"getp", "2", // getp <map name> <key> // the same as above (get)
			"plist", "1", // list <map name> // get hashmap contents as a [key1 value1 key2 value2 ...] list
			"erplist", "1", // erplist <map name> // deletes specified hashmap
			"name?", "1", // name <variable name> // check if variable is defined
			"defined?", "1", // name <function name> // check if function is defined
			"clearname", "1", // undefine symbol value
			"quote", "1", // quote <something> // convert number -> string ??
			"intern", "1", // intern <name> // returns symbol or creates an empty one
			"nargs", "1"}; // nargs <function name> // get number of arguments of function

	public String[] primitives() {
		return primlist;
	}

	public Object dispatch(int code, Object[] args, LContext ctx) {
		switch (code) {
			case 0:
				return this.prim_make(args[0], args[1], ctx);
			case 1:
				return this.prim_define(args[0], args[1], args[2], ctx);
			case 2:
				return this.prim_let(args[0], ctx);
			case 3:
				return this.prim_thing(args[0], ctx);
			case 4:
				return this.prim_put(args[0], args[1], args[2], ctx);
			case 5:
				return this.prim_get(args[0], args[1], ctx);
			case 6:
				return this.prim_get(args[0], args[1], ctx);
			case 7:
				return this.prim_plist(args[0], ctx);
			case 8:
				return this.prim_erplist(args[0], ctx);
			case 9:
				return this.prim_namep(args[0], ctx);
			case 10:
				return this.prim_definedp(args[0], ctx);
			case 11:
				return this.prim_clearname(args[0], ctx);
			case 12:
				return this.prim_quote(args[0], ctx);
			case 13:
				return this.prim_intern(args[0], ctx);
			case 14:
				return this.prim_nargs(args[0], ctx);
			default:
				return null;
		}
	}

	private Object prim_make(Object name, Object value, LContext ctx) {
		Logo.setValue(Logo.toSymbol(name, ctx), value);
		return null;
	}

	private Object prim_clearname(Object name, LContext ctx) {
		Logo.setValue(Logo.toSymbol(name, ctx), null);
		return null;
	}

	private Object prim_define(Object name, Object arglist, Object code, LContext ctx) {
		Symbol symbol = Logo.toSymbol(name, ctx);
		Object[] args = Logo.toList(arglist, ctx);
		Object[] body = Logo.toList(code, ctx);
		Ufun function = new Ufun(args, body);
		symbol.fcn = new Function(function, args.length, 0);
		return null;
	}

	private Object prim_let(Object assignments, LContext ctx) {
		if (ctx.locals == null)
			ctx.locals = new Object[0];

		List<Object> oldLocals = Arrays.asList(ctx.locals);
		List<Object> newLocals = new ArrayList<>(oldLocals);

		TokenChain definition = new TokenChain(Logo.toList(assignments, ctx));

		while (!definition.eof()) {
			Symbol variable = Logo.toSymbol(definition.next(), ctx);
			// backup old balue
			newLocals.add(variable);
			newLocals.add(variable.value); // because value in variable changes
			Logo.setValue(variable, Logo.evalOneArg(definition, ctx));
		}

		ctx.locals = newLocals.toArray();
		return null;
	}

	private Object prim_thing(Object name, LContext ctx) {
		return Logo.getValue(Logo.toSymbol(name, ctx), ctx);
	}

	private Object prim_put(Object mapName, Object key, Object value, LContext ctx) {
		HashMap<Object, Object> map = ctx.props.get(mapName);
		if (map == null) {
			map = new HashMap<>();
			ctx.props.put(mapName, map);
		}

		map.put(key, value);
		return null;
	}

	private Object prim_get(Object mapName, Object key, LContext ctx) {
		HashMap<Object, Object> map = ctx.props.get(mapName);
		if (map == null) {
			return new Object[0];
		} else {
			Object value = map.get(key);
			return value == null ? new Object[0] : value;
		}
	}

	private Object prim_plist(Object mapName, LContext ctx) {
		HashMap<Object, Object> map = ctx.props.get(mapName);
		if (map == null) {
			return new Object[0];
		} else {
			List<Object> pairs = new ArrayList<>();
			Set<Object> keys = map.keySet();

			for (Object key : keys) {
				pairs.add(key);
				pairs.add(map.get(key));
			}

			return pairs.toArray();
		}
	}

	private Object prim_erplist(Object mapName, LContext ctx) {
		ctx.props.remove(mapName);
		return null;
	}

	private Object prim_namep(Object name, LContext ctx) {
		return Logo.toSymbol(name, ctx).value != null;
	}

	private Object prim_definedp(Object name, LContext ctx) {
		return Logo.toSymbol(name, ctx).fcn != null;
	}

	private Object prim_quote(Object name, LContext ctx) {
		if (name instanceof Object[])
			return name;
		return new QuotedSymbol(Logo.toSymbol(name, ctx));
	}

	private Object prim_intern(Object name, LContext ctx) {
		return Logo.toSymbol(name, ctx);
	}

	private Object prim_nargs(Object name, LContext ctx) {
		return Logo.toSymbol(name, ctx).fcn.nargs;
	}
}
