package logo;

import java.util.ArrayList;
import java.util.List;

public class TokenStream {
	private String str;
	private int offset;
	private boolean hexread;

	public TokenStream(String contents) {
		this(contents, false);
	}

	public TokenStream(String contents, boolean hexread) {
		this.offset = 0;
		this.hexread = hexread;
		this.str = contents;
		this.skipSpace();
	}

	public Object[] readList(LContext ctx) {
		List<Object> tokens = new ArrayList<>();

		Object token;
		while (!this.eof() && (token = this.readToken(ctx)) != null) {
			tokens.add(token);
		}

		return tokens.toArray();
	}

	Object readToken(LContext ctx) {
		String str = this.next();
		int len = str.length();

		if (this.hexread) {
			try {
				return (double) Long.parseLong(str, 16);

			} catch (NumberFormatException ignored) {
			}
		}
		if (len > 2 && str.charAt(0) == '0' && str.charAt(1) == 'x') {
			try {
				return (double) Long.parseLong(str.substring(2), 16);
			} catch (NumberFormatException ignored) {
			}
		}

		if (len > 1 && str.charAt(0) == '$') {
			try {
				return (double) Long.parseLong(str.substring(1), 16);
			} catch (NumberFormatException ignored) {
			}
		}

		if (len > 1 && str.charAt(0) == '0') {
			try {
				return (double) Long.parseLong(str.substring(1), 8);
			} catch (NumberFormatException ignored) {
			}
		}

		if (str.equals("]"))
			return null;

		if (Logo.isValidNumber(str)) {
			try {
				return Double.valueOf(str);
			} catch (NumberFormatException ignored) {
			}
		}
		if (str.charAt(0) == '\"')
			return new QuotedSymbol(Logo.querySymbol(str.substring(1), ctx));
		else if (str.charAt(0) == ':')
			return new DottedSymbol(Logo.querySymbol(str.substring(1), ctx));
		else if (str.equals("["))
			return this.readList(ctx);
		else if (str.charAt(0) == '\'')
			return str.substring(1);
		else
			return Logo.querySymbol(str, ctx);
	}

	boolean startsWith(String str) {
		return this.str.startsWith(str, this.offset);
	}

	void skipToNextLine() {
		while (!this.eof() && !Logo.charIs(this.str.charAt(this.offset), "\n\r")) {
			this.offset++;
		}

		this.skipSpace();
	}

	private void skipSpace() {
		while (!this.eof() && Logo.charIs(this.str.charAt(this.offset), " ;\t\r\n")) {
			if (this.peekChar().equals(";")) {
				while (!this.eof() && !Logo.charIs(this.str.charAt(this.offset), "\n\r")) {
					this.offset++;
				}
			} else {
				this.offset++;
			}
		}

	}

	String nextLine() {
		StringBuilder builder = new StringBuilder();
		while (!this.eof() && !";\n\r".contains(this.peekChar()))
			builder.append(this.nextChar());

		this.skipSpace();
		return builder.toString();
	}

	private String next() {
		StringBuilder builder = new StringBuilder();

		if (!this.delim(this.peekChar())) {
			while (!this.eof() && !this.delim(this.peekChar())) {
				if (this.peekChar().equals("\'")) {
					builder.insert(0, '\'');
					builder.append(this.getQuoteString());
					this.skipSpace();
					return builder.toString();
				}
				builder.append(this.nextChar());
			}
		} else {
			builder.append(this.nextChar());
		}

		this.skipSpace();
		return builder.toString();
	}

	private String getQuoteString() {
		StringBuilder str = new StringBuilder();
		this.nextChar();

		while (!this.eof()) {
			if (this.peekChar().equals("\'")) {
				this.nextChar();
				if (this.eof() || !this.peekChar().equals("\'")) {
					break;
				}

				str.append(this.nextChar());
			} else {
				str.append(this.nextChar());
			}
		}

		return str.toString();
	}

	private boolean delim(String str) {
		char ch = str.charAt(0);
		return "()[] \t\r\n".indexOf(ch) != -1;
	}

	private String peekChar() {
		return String.valueOf(this.str.charAt(this.offset));
	}

	private String nextChar() {
		return String.valueOf(this.str.charAt(this.offset++));
	}

	boolean eof() {
		return this.str.length() == this.offset;
	}
}
