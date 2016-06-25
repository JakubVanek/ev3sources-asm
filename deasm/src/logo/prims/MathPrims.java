package logo.prims;

import logo.LContext;
import logo.Logo;
import logo.LogoError;

@SuppressWarnings("unused")
public class MathPrims implements Primitives {
	private static final double degtor = 57.29577951308232D;
	private static String[] primlist = new String[]{
			"sum", "i2", // sum <a> <b> | (sum 1 2 ...) // add numbers
			"remainder", "2", // remainder <dividend> <divisor> // perform modulo (dividend % divisor)
			"difference", "2", // diffference <minuend> <subtrahend> // subtract number
			"diff", "2", // diff <minuend> <subtrahend> // subtract number (same as above)
			"product", "i2", // product <a> <b> | (product 1 2 ...) // multiply numbers
			"quotient", "2", // quotient <dividend> <divisor> // divide numbers
			"greater?", "2", // greater? <a> <b> // check if a > b
			"less?", "2", // less? <a> <b> // check if a < b
			"int", "1", // int <num> // truncate decimal number
			"minus", "1", // minus <num> // negate number
			"round", "1", // round <num> // round decimal number
			"sqrt", "1", // sqrt <num> // calculate square root
			"sin", "1", // sin <num> // calculate sine of num in degrees
			"cos", "1", // cos <num> // calculate cosine of num in degrees
			"tan", "1", // tan <num> // calculate tangent of num in degrees
			"abs", "1", // abs <num> // calculate absolute value
			"power", "2", // power <base> <exponent> // raise base to the power of exponent
			"arctan", "1", // arctan <num> // calculate arc tangent in degrees
			"pi", "0", // pi // get the value of PI
			"exp", "1", // exp <num> // exponentiate e over num
			"arctan2", "2", // arctan2 <x> <y> // calculate arctan2 in degrees
			"ln", "1", // ln <num> // calculate natural logarithm
			"logand", "2", // logand <a> <b> // logical multiply on integers
			"logior", "2", // logior <a> <b> // logical inclusive addition on integers
			"logxor", "2", // logxor <a> <b> // logical exclusive addition on integers
			"lsh", "2", // lsh <num> <shift> // shift num shift times to the left
			"and", "i2", // and <a> <b> | (and 1 2 ...) // logical multiply on booleans
			"or", "i2", // or <a> <b> | (or 1 2 ...) // logical inclusive addition on booleans
			"not", "1", // not <a> // negate boolean
			"random", "1", // random <max> // return random number in range ⟨0; max)
			"min", "i2", // min <a> <b> | (min 1 2 ...) // get minimum value
			"max", "i2", // max <a> <b> | (max 1 2 ...) // get maximum value
			"number?", "1", // number <value> // check if value is a number
			"+", "-2", // <a> + <b> // infix addition
			"-", "-2", // <a> - <b> // infix subtraction
			"*", "-3", // <a> * <b> // infix multiplication
			"/", "-3", // <a> / <b> // infix division
			"<", "-1", // <a> < <b> // infix less
			">", "-1", // <a> > <b> // infix greater
			"=", "-1", // <a> = <b> // infix equal
			"equal?", "i2", // equal <a> <b> | (equal 1 2 ...) // check if items are equal
			"%", "-3"}; // <a> % <b> // infix modulo

	public String[] primitives() {
		return primlist;
	}

	public Object dispatch(int code, Object[] args, LContext ctx) {
		switch (code) {
			case 0:
				return this.prim_sum(args, ctx);
			case 1:
				return this.prim_remainder(args[0], args[1], ctx);
			case 2:
			case 3:
				return this.prim_diff(args[0], args[1], ctx);
			case 4:
				return this.prim_product(args, ctx);
			case 5:
				return this.prim_quotient(args[0], args[1], ctx);
			case 6:
				return this.prim_greaterp(args[0], args[1], ctx);
			case 7:
				return this.prim_lessp(args[0], args[1], ctx);
			case 8:
				return this.prim_int(args[0], ctx);
			case 9:
				return this.prim_minus(args[0], ctx);
			case 10:
				return this.prim_round(args[0], ctx);
			case 11:
				return this.prim_sqrt(args[0], ctx);
			case 12:
				return this.prim_sin(args[0], ctx);
			case 13:
				return this.prim_cos(args[0], ctx);
			case 14:
				return this.prim_tan(args[0], ctx);
			case 15:
				return this.prim_abs(args[0], ctx);
			case 16:
				return this.prim_power(args[0], args[1], ctx);
			case 17:
				return this.prim_arctan(args[0], ctx);
			case 18:
				return this.prim_pi();
			case 19:
				return this.prim_exp(args[0], ctx);
			case 20:
				return this.prim_arctan2(args[0], args[1], ctx);
			case 21:
				return this.prim_ln(args[0], ctx);
			case 22:
				return this.prim_logand(args[0], args[1], ctx);
			case 23:
				return this.prim_logior(args[0], args[1], ctx);
			case 24:
				return this.prim_logxor(args[0], args[1], ctx);
			case 25:
				return this.prim_lsh(args[0], args[1], ctx);
			case 26:
				return this.prim_and(args, ctx);
			case 27:
				return this.prim_or(args, ctx);
			case 28:
				return this.prim_not(args[0], ctx);
			case 29:
				return this.prim_random(args[0], ctx);
			case 30:
				return this.prim_min(args, ctx);
			case 31:
				return this.prim_max(args, ctx);
			case 32:
				return this.prim_numberp(args[0], ctx);
			case 33:
				return this.prim_sum(args, ctx);
			case 34:
				return this.prim_diff(args[0], args[1], ctx);
			case 35:
				return this.prim_product(args, ctx);
			case 36:
				return this.prim_quotient(args[0], args[1], ctx);
			case 37:
				return this.prim_lessp(args[0], args[1], ctx);
			case 38:
				return this.prim_greaterp(args[0], args[1], ctx);
			case 39:
			case 40:
				return this.prim_equalp(args, ctx);
			case 41:
				return this.prim_remainder(args[0], args[1], ctx);
			default:
				return null;
		}
	}

	private Object prim_sum(Object[] addends, LContext ctx) {
		double sum = 0.0D;

		for (Object value : addends) {
			sum += Logo.toDouble(value, ctx);
		}

		return sum;
	}

	private Object prim_remainder(Object dividend, Object divisor, LContext ctx) {
		return Logo.toDouble(dividend, ctx) % Logo.toDouble(divisor, ctx);
	}

	private Object prim_diff(Object minuend, Object subtrahend, LContext ctx) {
		return Logo.toDouble(minuend, ctx) - Logo.toDouble(subtrahend, ctx);
	}

	private Object prim_product(Object[] factors, LContext ctx) {
		double product = 1.0D;

		for (Object value : factors) {
			product *= Logo.toDouble(value, ctx);
		}

		return product;
	}

	private Object prim_quotient(Object dividend, Object divisor, LContext ctx) {
		return Logo.toDouble(dividend, ctx) / Logo.toDouble(divisor, ctx);
	}

	private Object prim_greaterp(Object first, Object second, LContext ctx) {
		return Logo.toDouble(first, ctx) > Logo.toDouble(second, ctx);
	}

	private Object prim_lessp(Object first, Object second, LContext ctx) {
		return Logo.toDouble(first, ctx) < Logo.toDouble(second, ctx);
	}

	private Object prim_int(Object value, LContext ctx) {
		return (double) (new Double(Logo.toDouble(value, ctx))).longValue();
	}

	private Object prim_minus(Object value, LContext ctx) {
		return 0.0D - Logo.toDouble(value, ctx);
	}

	private Object prim_round(Object value, LContext ctx) {
		return (double) Math.round(Logo.toDouble(value, ctx));
	}

	private Object prim_sqrt(Object value, LContext ctx) {
		return Math.sqrt(Logo.toDouble(value, ctx));
	}

	private Object prim_sin(Object value, LContext ctx) {
		return Math.sin(Logo.toDouble(value, ctx) / degtor);
	}

	private Object prim_cos(Object value, LContext ctx) {
		return Math.cos(Logo.toDouble(value, ctx) / degtor);
	}

	private Object prim_tan(Object value, LContext ctx) {
		return Math.tan(Logo.toDouble(value, ctx) / degtor);
	}

	private Object prim_abs(Object value, LContext ctx) {
		return Math.abs(Logo.toDouble(value, ctx));
	}

	private Object prim_power(Object base, Object power, LContext ctx) {
		return Math.pow(Logo.toDouble(base, ctx), Logo.toDouble(power, ctx));
	}

	private Object prim_arctan(Object value, LContext ctx) {
		return degtor * Math.atan(Logo.toDouble(value, ctx));
	}

	private Object prim_pi() {
		return 3.141592653589793D;
	}

	private Object prim_exp(Object value, LContext ctx) {
		return Math.exp(Logo.toDouble(value, ctx));
	}

	private Object prim_arctan2(Object x, Object y, LContext ctx) {
		return degtor * Math.atan2(Logo.toDouble(x, ctx), Logo.toDouble(y, ctx));
	}

	private Object prim_ln(Object value, LContext ctx) {
		return Math.log(Logo.toDouble(value, ctx));
	}

	private Object prim_logand(Object a, Object b, LContext ctx) {
		return (double) (Logo.toLong(a, ctx) & Logo.toLong(b, ctx));
	}

	private Object prim_logior(Object a, Object b, LContext ctx) {
		return (double) (Logo.toLong(a, ctx) | Logo.toLong(b, ctx));
	}

	private Object prim_logxor(Object a, Object b, LContext ctx) {
		return (double) (Logo.toLong(a, ctx) ^ Logo.toLong(b, ctx));
	}

	private Object prim_lsh(Object value, Object places, LContext ctx) {
		int shifter = Logo.toInt(places, ctx);
		long shiftend = Logo.toLong(value, ctx);
		if (shifter > 0)
			return (double) (shiftend << shifter);
		else
			return (double) (shiftend >> -shifter);
	}

	private Object prim_and(Object[] values, LContext ctx) {
		boolean result = true;

		for (Object value : values) {
			result &= Logo.toBool(value, ctx);
		}

		return result;
	}

	private Object prim_or(Object[] values, LContext ctx) {
		boolean result = false;

		for (Object value : values) {
			result |= Logo.toBool(value, ctx);
		}

		return result;
	}

	private Object prim_not(Object value, LContext ctx) {
		return !Logo.toBool(value, ctx);
	}

	private Object prim_random(Object max, LContext ctx) {
		return Math.floor(Math.random() * (double) Logo.toInt(max, ctx));
	}

	private Object prim_min(Object[] values, LContext ctx) {
		if (values.length == 0) {
			Logo.error("Min needs at least one input", ctx);
		}

		double result = Logo.toDouble(values[0], ctx);

		for (int i = 1; i < values.length; i++) {
			result = Math.min(result, Logo.toDouble(values[i], ctx));
		}

		return result;
	}

	private Object prim_max(Object[] values, LContext ctx) {
		if (values.length == 0) {
			Logo.error("Max needs at least one input", ctx);
		}

		double result = Logo.toDouble(values[0], ctx);

		for (int i = 1; i < values.length; ++i) {
			result = Math.max(result, Logo.toDouble(values[i], ctx));
		}

		return result;
	}

	private Object prim_numberp(Object test, LContext ctx) {
		try {
			Logo.toDouble(test, ctx);
			return true;
		} catch (LogoError ex) {
			return false;
		}
	}

	private Object prim_equalp(Object[] values, LContext ctx) {
		if (values.length == 0) {
			Logo.error("Equal needs at least one input", ctx);
		}

		Object reference = values[0];

		for (int i = 1; i < values.length; ++i) {
			if (!Logo.serialize(reference).equals(Logo.serialize(values[i]))) {
				return false;
			}
		}

		return true;
	}
}
