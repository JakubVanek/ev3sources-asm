package logo.prims;

import logo.*;

@SuppressWarnings("unused")
public class ControlPrims implements Primitives {
	private static final String[] primlist = new String[]{
			"repeat", "2", // repeat <count> [code]
			"if", "2", // if <condition> [code]
			"ifelse", "3", // if <condition> [code if true] [code if false]
			"stop", "0", // stop // stop program by setting ufunresult to juststop
			"output", "1", // output <value> // set function result
			"dotimes", "2", // dotimes [<indexer> <times>] [code] // shorthand for a simple for loop from i=0 to destination
			"dolist", "2", // dolist [<indexer> <list>] [code] // foreach
			"carefully", "2", // carefully [code] [code on fail] // catch any exception, put msg in errormessage
			"errormessage", "0", // get error message
			"unwind-protect", "2", // unwind-protect [code] [finally code] // always runs the finally code
			"error", "1", // error <message> //triggers an error
			"dispatch", "2", // dispatch <index> [[code1] [code2] ...] // method selector
			"run", "1", // run [code] // run piece of code
			"loop", "1", // loop [code] // run forever
			"forever", "1", // forever [code] // run forever
			"selectq", "2", // selectq <selector> [<id1> [code1] <id2> [code2] ...] // selects the right code and runs it
			"stopme", "0"}; // stopme // trigger an empty error

	public String[] primitives() {
		return primlist;
	}

	public Object dispatch(int code, Object[] args, LContext ctx) {
		switch (code) {
			case 0:
				return this.prim_repeat(args[0], args[1], ctx);
			case 1:
				return this.prim_if(args[0], args[1], ctx);
			case 2:
				return this.prim_ifelse(args[0], args[1], args[2], ctx);
			case 3:
				return this.prim_stop(ctx);
			case 4:
				return this.prim_output(args[0], ctx);
			case 5:
				return this.prim_dotimes(args[0], args[1], ctx);
			case 6:
				return this.prim_dolist(args[0], args[1], ctx);
			case 7:
				return this.prim_carefully(args[0], args[1], ctx);
			case 8:
				return ctx.errormessage;
			case 9:
				return this.prim_unwindprotect(args[0], args[1], ctx);
			case 10:
				return this.prim_error(args[0], ctx);
			case 11:
				return this.prim_dispatch(args[0], args[1], ctx);
			case 12:
				return this.prim_run(args[0], ctx);
			case 13:
				return this.prim_loop(args[0], ctx);
			case 14:
				return this.prim_loop(args[0], ctx);
			case 15:
				return this.prim_selectq(args[0], args[1], ctx);
			case 16:
				return this.prim_stopme(ctx);
			default:
				return null;
		}
	}

	private Object prim_repeat(Object count, Object code, LContext ctx) {
		int times = Logo.toInt(count, ctx);
		Object[] lines = Logo.toList(code, ctx);

		for (int i = 0; i < times; ++i) {
			Logo.runCommand(lines, ctx);
			if (ctx.ufunresult != null) {
				return null;
			}
		}

		return null;
	}

	private Object prim_if(Object predicate, Object code, LContext ctx) {
		if (Logo.toBool(predicate, ctx)) {
			Logo.runCommand(Logo.toList(code, ctx), ctx);
		}

		return null;
	}

	private Object prim_ifelse(Object predicate, Object if_true, Object if_false, LContext ctx) {
		boolean cond = Logo.toBool(predicate, ctx);
		Object[] code_true = Logo.toList(if_true, ctx);
		Object[] code_false = Logo.toList(if_false, ctx);
		return Logo.runList(cond ? code_true : code_false, ctx);
	}

	private Object prim_stop(LContext ctx) {
		ctx.ufunresult = ctx.juststop;
		return null;
	}

	private Object prim_output(Object output, LContext ctx) {
		ctx.ufunresult = output;
		return null;
	}

	private Object prim_dotimes(Object param_list, Object code, LContext ctx) {
		TokenChain params = new TokenChain(Logo.toList(param_list, ctx));
		Object[] lines = Logo.toList(code, ctx);
		Symbol indexer = Logo.toSymbol(params.next(), ctx);
		int count = Logo.toInt(Logo.evalOneArg(params, ctx), ctx);
		Logo.checkChainEmpty(params, ctx);
		Object original = indexer.value;

		try {
			for (int i = 0; i < count; i++) {
				indexer.value = (double) i;
				Logo.runCommand(lines, ctx);
			}

			if (ctx.ufunresult != null) {
				return null;
			}
		} finally {
			indexer.value = original;
		}

		return null;
	}

	private Object prim_dolist(Object param_list, Object code, LContext ctx) {
		TokenChain params = new TokenChain(Logo.toList(param_list, ctx));
		Object[] lines = Logo.toList(code, ctx);
		Symbol indexer = Logo.toSymbol(params.next(), ctx);
		Object[] list = Logo.toList(Logo.evalOneArg(params, ctx), ctx);
		Logo.checkChainEmpty(params, ctx);
		Object original = indexer.value;

		try {
			for (Object element : list) {
				indexer.value = element;
				Logo.runCommand(lines, ctx);
				if (ctx.ufunresult != null) {
					return null;
				}
			}
		} finally {
			indexer.value = original;
		}

		return null;
	}

	private Object prim_carefully(Object code, Object onfail, LContext ctx) {
		Object[] lines = Logo.toList(code, ctx);
		Object[] failLines = Logo.toList(onfail, ctx);

		try {
			return Logo.runList(lines, ctx);
		} catch (Exception ex) {
			ctx.errormessage = ex.getMessage();
			return Logo.runList(failLines, ctx);
		}
	}

	private Object prim_unwindprotect(Object code, Object safe, LContext ctx) {
		Object[] lines = Logo.toList(code, ctx);
		Object[] lines_always = Logo.toList(safe, ctx);

		try {
			Logo.runCommand(lines, ctx);
		} finally {
			Logo.runCommand(lines_always, ctx);
		}

		return null;
	}

	private Object prim_error(Object msg, LContext ctx) {
		Logo.error(Logo.serialize(msg), ctx);
		return null;
	}

	private Object prim_dispatch(Object method_index, Object methods, LContext ctx) {
		int code = Logo.toInt(method_index, ctx);
		Object[] method_list = Logo.toList(methods, ctx);
		Object[] method = Logo.toList(method_list[code], ctx);
		return Logo.runList(method, ctx);
	}

	private Object prim_run(Object code, LContext ctx) {
		return Logo.runList(Logo.toList(code, ctx), ctx);
	}

	private Object prim_loop(Object code, LContext ctx) {
		Object[] lines = Logo.toList(code, ctx);

		do {
			Logo.runCommand(lines, ctx);
		} while (ctx.ufunresult == null);

		return null;
	}

	private Object prim_selectq(Object selector, Object map, LContext ctx) {
		Object[] method_list = Logo.toList(map, ctx);
		int offset = 0;

		while (true) {
			if (offset >= method_list.length) {
				return null;
			}

			Object identifier = method_list[offset];
			if (identifier instanceof DottedSymbol) {
				if (Logo.getValue((DottedSymbol) identifier, ctx).equals(selector)) {
					break;
				}
			} else if (identifier.equals(selector)) {
				break;
			}

			offset += 2;
		}

		return Logo.runList((Object[]) method_list[offset + 1], ctx);
	}

	private Object prim_stopme(LContext ctx) {
		Logo.error("", ctx);
		return null;
	}
}
