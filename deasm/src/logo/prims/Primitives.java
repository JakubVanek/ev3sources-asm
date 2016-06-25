package logo.prims;

import logo.LContext;

/**
 * Function implementation interface.
 */
public interface Primitives {
	/**
	 * Get a list of primitives with their offsets.<br>
	 * <p>
	 * The format is following: {"name1", "nargs1", "name2", "nargs2", ...}
	 * Name is the name of the primitive. Nargs is the number of arguments, which can have multiple meanings:
	 * <ul>
	 * <li>
	 * <b>nargs is positive number.</b>
	 * Then it represents number of required arguments.
	 * </li>
	 * <li>
	 * <b>nargs is positive number prefixed with "i".</b>
	 * Then the substring(1) represents number of required arguments. "i" signalizes that this function can work even in
	 * inline list mode - that is, you can pass arbitrary number of arguments to it by writing (name1 arg1 arg2 arg3)
	 * </li>
	 * <li>
	 * <b>nargs is negative number.</b>
	 * Then the nargs doesn't tell you the number of arguments, but it tells you that this operator is primarily infix.
	 * The lower the nargs, the higher the evaluation priority is.
	 * </li>
	 * </ul>
	 *
	 * @return Primitive description. If this operation is not defined, it returns {@code null}.
	 */
	default String[] primitives() {
		return null;
	}

	/**
	 * Call a function implementation.
	 *
	 * @param code Call offset. It is the index of name from {@link #primitives()} divided by 2.
	 *             For user functions this has no effect.
	 * @param args Array of arguments. Length should be equal to nargs for specified function, except for
	 *             inline list functions.
	 * @param ctx  Parser context.
	 * @return Return value of the function.
	 */
	default Object dispatch(int code, Object[] args, LContext ctx) {
		return null;
	}
}
