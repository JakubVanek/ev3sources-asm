package logo;

import logo.gui.LogoConsole;

import java.io.PrintWriter;

public class MainClass {
	public static LogoConsole logoconsole;
	public static LContext lc;
	private static String[] primclasses = new String[]{
			"logo.prims.SystemPrims",
			"logo.prims.MathPrims",
			"logo.prims.ControlPrims",
			"logo.prims.DefiningPrims",
			"logo.prims.WordListPrims",
			"logo.prims.FilePrims",
			"logo.prims.StringBufferPrims",
			"logo.prims.CCPrims"};

	public static void main(String[] args) {
		lc = new LContext();
		lc.stdout = new PrintWriter(System.out, true);
		lc.console_arguments = args;
		Logo.setupPrimitives(primclasses, lc);
		(new LogoCommandRunner("load \"startup startup", lc, true)).run();
	}
}
