package logo.prims;

import logo.LContext;
import logo.Logo;
import logo.Symbol;

public class Ufun implements Primitives {
	private final Object[] arglist;
	private final Object[] body;

	public Ufun(Object[] args, Object[] body) {
		this.arglist = args;
		this.body = body;
	}

	public Object dispatch(int code, Object[] args, LContext ctx) {
		Object retval = null;
		Object[] argsym_backup = new Object[this.arglist.length];
		Symbol ufun_backup = ctx.ufun;
		ctx.ufun = ctx.current_function;
		Object[] locals_backup = ctx.locals;
		ctx.locals = null;

		int i;
		for (i = 0; i < this.arglist.length; i++) {
			argsym_backup[i] = ((Symbol) this.arglist[i]).value;
			((Symbol) this.arglist[i]).value = args[i];
		}

		boolean failed = false;

		try {
			failed = true;
			Logo.runCommand(this.body, ctx);
			failed = false;
			if (ctx.ufunresult != null && ctx.ufunresult != ctx.juststop) {
				retval = ctx.ufunresult;
			}
		} finally {
			if (failed) {
				ctx.ufun = ufun_backup;

				for (i = 0; i < this.arglist.length; i++) {
					((Symbol) this.arglist[i]).value = argsym_backup[i];
				}

				ctx.locals = locals_backup;
				ctx.ufunresult = null;
			}
		}

		ctx.ufun = ufun_backup;

		for (i = 0; i < this.arglist.length; i++) {
			((Symbol) this.arglist[i]).value = argsym_backup[i];
		}

		if (ctx.locals != null) {
			for (i = 0; i < ctx.locals.length; i += 2) {
				((Symbol) ctx.locals[i]).value = ctx.locals[i + 1];
			}
		}

		ctx.locals = locals_backup;
		ctx.ufunresult = null;
		return retval;
	}
}
