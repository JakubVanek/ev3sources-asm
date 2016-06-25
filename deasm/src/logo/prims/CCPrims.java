package logo.prims;

import logo.LContext;
import logo.Logo;
import logo.MainClass;
import logo.gui.LogoConsole;

@SuppressWarnings("unused")
public class CCPrims implements Primitives {
	private final static String[] primlist = new String[]{
			"setstatus", "1", // setstatus <expr> // set status field
			"status", "0", // status // get status field
			"setfile-field", "1", // set contents of file text field
			"file-field", "0", // get contents of file text field
			"dirname", "0", // return current directory name
			"showcc", "0", // show interpreter window
			"clargs", "0"}; // // console arguments

	public String[] primitives() {
		return primlist;
	}

	public Object dispatch(int code, Object[] args, LContext ctx) {
		switch (code) {
			case 0:
				return this.prim_setStatus(args[0], ctx);
			case 1:
				return this.prim_getStatus(ctx);
			case 2:
				return this.prim_setFilename(args[0], ctx);
			case 3:
				return this.prim_getFilename(ctx);
			case 4:
				return this.prim_getDirname(ctx);
			case 5:
				return this.prim_showcc();
			case 6:
				return ctx.console_arguments;
			default:
				return null;
		}
	}

	private Object prim_setStatus(Object status, LContext ctx) {
		ctx.status_field.setText(Logo.serialize(status));
		return null;
	}

	private Object prim_getStatus(LContext ctx) {
		return ctx.status_field.getText();
	}

	private Object prim_setFilename(Object name, LContext ctx) {
		ctx.file_field.setText(Logo.serialize(name));
		return null;
	}

	private Object prim_getFilename(LContext ctx) {
		return ctx.file_field_path + ctx.file_field.getText();
	}

	private Object prim_getDirname(LContext ctx) {
		return ctx.file_field_path;
	}

	private Object prim_showcc() {
		(MainClass.logoconsole = new LogoConsole("Logo interpreter")).init();
		return null;
	}
}
