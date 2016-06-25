package logo.prims;

import logo.LContext;
import logo.Logo;

import java.util.Arrays;

@SuppressWarnings("unused")
public class WordListPrims implements Primitives {
	private static String[] primlist = new String[]{
			"first", "1", // first <list> // get first item
			"last", "1", // last <list> // get last item
			"word", "i2", // word <a> <b> | (word a b ...) // make word from a list
			"butfirst", "1", // butfirst <list> // take away the first item
			"bf", "1", // bf <list> // take away the first item (same as above)
			"butlast", "1", // butlast <list> // take away the last item
			"bl", "1", // bl <list> // take away the last item (same as above)
			"fput", "2", // fput <elem> <list> // prepend list with element
			"lput", "2", // lput <elem> <list> // append element to the list
			"item", "2", // item <num> <list> // get item by the 1-based index
			"nth", "2", // nth <num> <list> // get n-th thing by the 0-based index
			"empty?", "1", // empty <list> // check if list is empty
			"count", "1", // count <list> // get size of list
			"word?", "1", // word? <value> // check if value is a word
			"list?", "1", // list? <value> // check if value is a list
			"member?", "2", // member? <item> <list> // check if item is at list
			"itempos", "2", // itempos <item> <list> // get position of item
			"setitem", "3", // setitem <pos> <list> <value> // set item by a 1-based index
			"setnth", "3", // setnth <pos> <list> <value> // set item by a 0-based index
			"removeitem", "2", // removeitem <item> <list> // remove item by its value
			"removeitempos", "2", // removeitempos <pos> <list> // remove item by its position
			"sentence", "2", // sentence <a> <b> // join lists
			"se", "i2", // se <a> <b> | (se a b ...) // join lists
			"list", "i2", // list <a> <b> | (list a b ...) // create a list from items
			"makelist", "1", // makelist <length> // create list of specified size
			"copylist", "1", // copylist <list> // create a copy of a list
			"parse", "1", // parse <str> // parse string to a list
			"char", "1", // char <ch> // create string from char
			"ascii", "1", // ascii <str> // get ascii code of first char
			"reverse", "1", // reverse <list> // reverse a list
			"substring", "3", // substring <list> <begin> <length> // cut a part of string
			"ucase", "1", // ucase <obj> // convert object to uppercase string
			"replace", "3", // replace <str> <regex> <replacement> // replace parts of string by a regex
			"split", "2", // split <str> <regex> // split a string
			"bytearray", "1"}; // bytearray <length> // create a byte array

	/**
	 * Add a new element to an array.
	 * The old array is copied to a new, larger one and the new element is placed at the end.
	 *
	 * @param element New element to add.
	 * @param oldList Old array.
	 * @return New array.
	 */
	private static Object[] lput(Object element, Object[] oldList) {
		Object[] newList = new Object[oldList.length + 1];

		System.arraycopy(oldList, 0, newList, 0, oldList.length);

		newList[oldList.length] = element;
		return newList;
	}

	/**
	 * Get 1-based index of member in list.
	 * If list is array and member is object, a lookup is performed.
	 * If both member and list are arrays, 0 is returned.
	 * If neither list nor member are arrays, they'll be serialized and a string lookup will be performed.
	 *
	 * @param member Object to search.
	 * @param list   Object where to search.
	 * @return 0 if not found, 1-based index of member otherwise.
	 */
	private static int memberp(Object member, Object list) {
		if (list instanceof Object[]) {
			Object[] array = (Object[]) list;
			String str = Logo.serialize(member);

			for (int i = 0; i < array.length; i++) {
				if (str.equals(Logo.serialize(array[i]))) {
					return i + 1;
				}
			}

			return 0;
		} else if (member instanceof Object[]) {
			return 0;
		} else {
			String sub_str = Logo.serialize(member);
			String main_str = Logo.serialize(list);
			/*
			for (int i = 0; i < main_str.length(); ++i) {
				if (sub_str.regionMatches(true, 0, main_str, i, sub_str.length())) {
					return i + 1;
				}
			}

			return 0;
			*/
			int location = main_str.indexOf(sub_str);
			if (location == -1)
				return 0;
			else
				return location + 1;
		}
	}

	/**
	 * Create new array from old one as a sub-array.
	 *
	 * @param oldList   Old array.
	 * @param beginning Index in old array from which the copying should start.
	 * @param newLength Length of new array.
	 * @return New array.
	 */
	private static Object copyList(Object[] oldList, int beginning, int newLength) {
		Object[] newList = new Object[newLength];

		System.arraycopy(oldList, beginning, newList, 0, newLength);

		return newList;
	}

	/**
	 * Create new list with added element(s) to it.
	 *
	 * @param oldList List to add in.
	 * @param element Element(s) to add.
	 * @return New list.
	 */
	private static Object[] addToList(Object[] oldList, Object element) {
		if (!(element instanceof Object[])) {
			return lput(element, oldList);
		} else {
			Object[] addList = (Object[]) element;
			Object[] result = new Object[oldList.length + addList.length];

			System.arraycopy(oldList, 0, result, 0, oldList.length);
			System.arraycopy(addList, 0, result, oldList.length, addList.length);

			return result;
		}
	}

	/**
	 * Create new list without one element at index.
	 *
	 * @param list  Old list.
	 * @param index Element to remove.
	 * @return New list.
	 */
	private static Object removeItem(Object[] list, int index) {
		int newLength = list.length - 1;
		Object[] newList = new Object[newLength];

		System.arraycopy(list, 0, newList, 0, index);
		System.arraycopy(list, index + 1, newList, index, newLength - index);

		return newList;
	}

	public String[] primitives() {
		return primlist;
	}

	public Object dispatch(int code, Object[] args, LContext ctx) {
		switch (code) {
			case 0:
				return this.prim_first(args[0]);
			case 1:
				return this.prim_last(args[0]);
			case 2:
				return this.prim_word(args);
			case 3:
			case 4:
				return this.prim_butfirst(args[0]);
			case 5:
			case 6:
				return this.prim_butlast(args[0]);
			case 7:
				return this.prim_fput(args[0], args[1], ctx);
			case 8:
				return this.prim_lput(args[0], args[1], ctx);
			case 9:
				return this.prim_item(args[0], args[1], ctx);
			case 10:
				return this.prim_nth(args[0], args[1], ctx);
			case 11:
				return this.prim_emptyp(args[0]);
			case 12:
				return this.prim_count(args[0]);
			case 13:
				return this.prim_wordp(args[0]);
			case 14:
				return this.prim_listp(args[0]);
			case 15:
				return this.prim_memberp(args[0], args[1]);
			case 16:
				return this.prim_itempos(args[0], args[1], ctx);
			case 17:
				return this.prim_setitem(args[0], args[1], args[2], ctx);
			case 18:
				return this.prim_setnth(args[0], args[1], args[2], ctx);
			case 19:
				return this.prim_removeitem(args[0], args[1], ctx);
			case 20:
				return this.prim_removeitempos(args[0], args[1], ctx);
			case 21:
			case 22:
				return this.prim_sentence(args);
			case 23:
				return this.prim_list(args);
			case 24:
				return this.prim_makelist(args[0], ctx);
			case 25:
				return this.prim_copylist(args[0], ctx);
			case 26:
				return this.prim_parse(args[0], ctx);
			case 27:
				return this.prim_char(args[0], ctx);
			case 28:
				return this.prim_ascii(args[0], ctx);
			case 29:
				return this.prim_reverse(args[0], ctx);
			case 30:
				return this.prim_substring(args[0], args[1], args[2], ctx);
			case 31:
				return this.prim_ucase(args[0]);
			case 32:
				return this.prim_replace(args[0], args[1], args[2]);
			case 33:
				return this.prim_split(args[0], args[1]);
			case 34:
				return new byte[Logo.toInt(args[0], ctx)];
			default:
				return null;
		}
	}

	private Object prim_first(Object list) {
		if (list instanceof Object[])
			return ((Object[]) list)[0];
		else
			return Logo.serialize(list).substring(0, 1);
	}

	private Object prim_last(Object list) {
		if (list instanceof Object[]) {
			Object[] arr = (Object[]) list;
			return arr[arr.length - 1];
		} else {
			String str = Logo.serialize(list);
			return str.substring(str.length() - 1, str.length());
		}
	}

	private Object prim_word(Object[] list) {
		StringBuilder builder = new StringBuilder();

		for (Object elem : list) {
			builder.append(Logo.serialize(elem));
		}

		return builder.toString();
	}

	private Object prim_butfirst(Object list) {
		if (list instanceof Object[]) {
			Object[] arr = (Object[]) list;
			return copyList(arr, 1, arr.length - 1);
		} else {
			String str = Logo.serialize(list);
			return str.substring(1, str.length());
		}
	}

	private Object prim_butlast(Object list) {
		if (list instanceof Object[]) {
			Object[] arr = (Object[]) list;
			return copyList(arr, 0, arr.length - 1);
		} else {
			String str = Logo.serialize(list);
			return str.substring(0, str.length() - 1);
		}
	}

	private Object prim_fput(Object elem, Object list, LContext ctx) {
		Object[] arr = Logo.toList(list, ctx);
		Object[] newArr = new Object[arr.length + 1];
		newArr[0] = elem;

		System.arraycopy(arr, 0, newArr, 1, arr.length);

		return newArr;
	}

	private Object prim_lput(Object elem, Object list, LContext ctx) {
		return lput(elem, Logo.toList(list, ctx));
	}

	private Object prim_item(Object pos, Object list, LContext ctx) {
		int index = Logo.toInt(pos, ctx) - 1;
		if (list instanceof Object[])
			return ((Object[]) list)[index];
		else
			return Logo.serialize(list).substring(index, index + 1);
	}

	private Object prim_nth(Object pos, Object list, LContext ctx) {
		int index = Logo.toInt(pos, ctx);
		if (list instanceof byte[])
			return (double) (0xFF & ((byte[]) list)[index]);
		else if (list instanceof String)
			return ((String) list).substring(index, index + 1);
		else if (list instanceof Object[])
			return ((Object[]) list)[index];
		else
			return Logo.serialize(list).substring(index, index + 1);
	}

	private Object prim_emptyp(Object list) {
		if (list instanceof Object[])
			return ((Object[]) list).length == 0;
		else
			return Logo.serialize(list).length() == 0;
	}

	private Object prim_count(Object list) {
		if (list instanceof byte[])
			return (double) ((byte[]) list).length;
		else if (list instanceof Object[])
			return (double) ((Object[]) list).length;
		else
			return (double) Logo.serialize(list).length();
	}

	private Object prim_wordp(Object test) {
		return !(test instanceof Object[]);
	}

	private Object prim_listp(Object test) {
		return test instanceof Object[];
	}

	private Object prim_memberp(Object item, Object list) {
		if (item instanceof String && list instanceof String) {
			return ((String) item).contains((String) list);
		}
		return memberp(item, list) != 0;
	}

	private Object prim_itempos(Object item, Object list, LContext ctx) {
		int pos = memberp(item, list);
		if (pos != 0) {
			return (long) pos;
		} else {
			Logo.error(ctx.current_function + " doesn\'t like " + Logo.serialize(item) + " as input", ctx);
			return null;
		}
	}

	private Object prim_setitem(Object pos, Object list, Object value, LContext ctx) {
		(Logo.toList(list, ctx))[Logo.toInt(pos, ctx) - 1] = value;
		return null;
	}

	private Object prim_setnth(Object ind_str, Object list, Object value, LContext ctx) {
		int index = Logo.toInt(ind_str, ctx);
		if (list instanceof byte[]) {
			((byte[]) list)[index] = (byte) ((Number) value).intValue();
		} else {
			(Logo.toList(list, ctx))[index] = value;
		}

		return null;
	}

	private Object prim_removeitem(Object item, Object list, LContext ctx) {
		Object[] arr = Logo.toList(list, ctx);
		return removeItem(arr, memberp(item, arr));
	}

	private Object prim_removeitempos(Object pos, Object list, LContext ctx) {
		return removeItem(Logo.toList(list, ctx), Logo.toInt(pos, ctx));
	}

	private Object prim_sentence(Object[] list) {
		Object[] result = new Object[0];

		for (Object item : list) {
			result = addToList(result, item);
		}

		return result;
	}

	private Object prim_list(Object[] list) {
		Object[] newList = new Object[list.length];

		System.arraycopy(list, 0, newList, 0, list.length);

		return newList;
	}

	private Object prim_makelist(Object length, LContext ctx) {
		int len = Logo.toInt(length, ctx);
		Object[] arr = new Object[len];

		Arrays.fill(arr, new Object[0]);

		return arr;
	}

	private Object prim_copylist(Object list, LContext ctx) {
		Object[] arr = Logo.toList(list, ctx);
		return copyList(arr, 0, arr.length);
	}

	private Object prim_parse(Object str, LContext ctx) {
		return Logo.parse(Logo.toString(str, ctx), ctx);
	}

	private Object prim_char(Object ch, LContext ctx) {
		char[] arr = new char[]{(char) Logo.toInt(ch, ctx)};
		return new String(arr);
	}

	private Object prim_ascii(Object str, LContext ctx) {
		return (long) Logo.toString(str, ctx).charAt(0);
	}

	private Object prim_reverse(Object list, LContext ctx) {
		Object[] oldList = Logo.toList(list, ctx);
		Object[] newList = new Object[oldList.length];

		for (int i = 0; i < oldList.length; ++i) {
			newList[i] = oldList[oldList.length - i - 1];
		}

		return newList;
	}

	private Object prim_substring(Object baseStr, Object startIndex, Object length, LContext ctx) {
		String str = Logo.serialize(baseStr);
		int start = Logo.toInt(startIndex, ctx);
		int len = Logo.toInt(length, ctx);
		if (start == -1)
			return str.substring(str.length() - len, str.length());
		else if (len == -1)
			return str.substring(start, str.length());
		else
			return str.substring(start, start + len);
	}

	private Object prim_ucase(Object obj) {
		return Logo.serialize(obj).toUpperCase();
	}

	private Object prim_replace(Object base, Object regex, Object replacement) {
		String str = Logo.serialize(base);
		String pattern = Logo.serialize(regex);
		String replace = Logo.serialize(replacement);
		return str.replaceAll(pattern, replace);
	}

	private Object prim_split(Object base, Object regex) {
		String str = Logo.serialize(base);
		String sep = Logo.serialize(regex);
		return str.split(sep);
	}
}
