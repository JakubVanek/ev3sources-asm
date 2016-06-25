package logo;

public class LogoCommandRunner implements Runnable {
	private final LContext context;
	private final Object[] listtorun;
	private final boolean silent;
	private boolean running;

	public LogoCommandRunner(Object[] list, LContext ctx) {
		this(list, ctx, false);
	}

	public LogoCommandRunner(String line, LContext ctx) {
		this(Logo.parse(line, ctx), ctx, false);
	}

	public LogoCommandRunner(String line, LContext ctx, boolean silent) {
		this(Logo.parse(line, ctx), ctx, silent);
	}

	public LogoCommandRunner(Object[] list, LContext ctx, boolean silent) {
		this.running = true;
		this.silent = silent;
		this.listtorun = list;
		this.context = ctx;
	}

	public void run() {
		synchronized (context) {
			String error = Logo.runToplevel(listtorun, context);
			if (context.stdout != null) {
				if (error != null) {
					context.stdout.println(error);
					context.errormessage = error;
				}

				if (!context.stop_requested && !this.silent) {
					context.stdout.println("ok");
				}
			}

			running = false;

		}
	}
}
