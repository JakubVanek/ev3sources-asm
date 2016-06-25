package logo;

import logo.prims.Primitives;

public class Function {
	public final Primitives implementation;
	public final int dispatchOffset;
	public final int nargs;
	public final boolean multiArgument;

	public Function(Primitives implementation, int nargs, int offset) {
		this(implementation, nargs, offset, false);
	}

	public Function(Primitives implementation, int nargs, int offset, boolean multiArgument) {
		this.implementation = implementation;
		this.nargs = nargs;
		this.dispatchOffset = offset;
		this.multiArgument = multiArgument;
	}
}
