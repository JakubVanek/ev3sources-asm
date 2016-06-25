package logo.prims;

import logo.LContext;
import logo.Logo;

import java.io.*;

@SuppressWarnings("unused")
public class FilePrims implements Primitives {
	private static String[] primlist = new String[]{
			"filetostring", "1", // filetostring <path> // read file to string
			"resourcetostring", "1", // resourcetostring <name> // read resource to string
			"load", "1", // load <name> // load functions from file
			"reload", "0", // reload // reload last file
			"stringtofile", "2", // stringtofile <path> <string> // write string to file
			"file?", "1", // file <path> // check if file exists
			"setread", "1", // setread <string> // set the string buffer
			"getc", "0", // getc // get next character from the string buffer
			"peek", "0", // peek // look at next character in the string buffer
			"readline", "0", // readline // get line from the string buffer
			"eot?", "0", // eot? // check if the string buffer is empty
			"lineback", "0", // lineback // jump one line back in the string buffer
			"lineread", "0", // lineread // return the string buffer
			"filenamefrompath", "1", // filenamefrompath <path> // get filename part from path
			"dirnamefrompath", "1", // dirnamefrompath <path> // get parent part (directory) from path
			"dir", "1", // dir <path> // list directory entries
			"setfread", "1", // setfread <path> // set file read source
			"freadline", "0", // freadline // read line from file
			"feot?", "0", // feot? // check if EOF is reached
			"fclose", "0", // fclose // close file input
			"erfile", "1", // erfile <path> // delete file
			"files", "1", // files <path> // list files a directory
			"logopen", "1", // logopen <path> // open log file
			"logprint", "1", // logprint <message> // print log message
			"logclose", "0", // logclose // close log
			"serialize", "2", // serialize <path> <object> // serialize Java object into binary dump
			"bytestofile", "2", // bytestofile <path> <bytes> // write byte array to a file
			"filetobytes", "1", // filetobytes <path> // read file to byte array
			"setmoddate", "2", // setmoddate <path> <date> // set modification time
			"mkdir", "1"}; // mkdir <path> // create directory
	private String readtext;
	private int textoffset;
	private BufferedReader freader;
	private PrintWriter logwriter;

	public String[] primitives() {
		return primlist;
	}

	public Object dispatch(int code, Object[] args, LContext ctx) {
		switch (code) {
			case 0:
				return this.prim_filetostring(args[0], ctx);
			case 1:
				return this.prim_resourcetostring(args[0], ctx);
			case 2:
				return this.prim_load(args[0], ctx);
			case 3:
				return this.prim_reload(ctx);
			case 4:
				return this.prim_stringtofile(args[0], args[1], ctx);
			case 5:
				return this.prim_file(args[0]);
			case 6:
				return this.prim_setread(args[0]);
			case 7:
				return this.prim_getc();
			case 8:
				return this.prim_peek();
			case 9:
				return this.prim_readline();
			case 10:
				return this.prim_eot();
			case 11:
				return this.prim_lineback();
			case 12:
				return this.prim_lineread();
			case 13:
				return this.prim_filenamefrompath(args[0]);
			case 14:
				return this.prim_dirnamefrompath(args[0]);
			case 15:
				return this.prim_dir(args[0]);
			case 16:
				return this.prim_setfread(args[0], ctx);
			case 17:
				return this.prim_freadline();
			case 18:
				return this.prim_feot();
			case 19:
				return this.prim_fclose(ctx);
			case 20:
				return this.prim_erfile(args[0], ctx);
			case 21:
				return this.prim_files(args[0], ctx);
			case 22:
				return this.prim_logopen(args[0], ctx);
			case 23:
				return this.prim_logprint(args[0]);
			case 24:
				return this.prim_logclose();
			case 25:
				return this.prim_serialize(args[0], args[1], ctx);
			case 26:
				return this.prim_bytestofile(args[0], args[1], ctx);
			case 27:
				return this.prim_filetobytes(args[0], ctx);
			case 28:
				return this.prim_setmoddate(args[0], args[1], ctx);
			case 29:
				return this.prim_mkdir(args[0]);
			default:
				return null;
		}
	}

	private Object prim_filetostring(Object path, LContext ctx) {
		String str = Logo.serialize(path);
		return this.fileToString(str, ctx);
	}

	private Object prim_resourcetostring(Object name, LContext ctx) {
		String str = Logo.serialize(name);
		return this.resourceToString(str, ctx);
	}

	private Object prim_reload(LContext ctx) {
		if (ctx.fload_filename == null) {
			Logo.error("No file loaded yet!", ctx);
			return null;
		}
		ctx.stdout.println("reloading " + ctx.fload_filename);
		return this.prim_load(ctx.fload_filename, ctx);
	}

	private Object prim_load(Object path, LContext ctx) {
		String str = Logo.serialize(path);
		Logo.readAllFunctions(this.fileToString(str + ".logo", ctx), ctx);
		if (ctx.fload_filename == null) {
			ctx.fload_filename = str;
		}

		return null;
	}

	private String resourceToString(String name, LContext ctx) {
		InputStream istream = FilePrims.class.getResourceAsStream(name);
		BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
		StringWriter ostream = new StringWriter();
		PrintWriter writer = new PrintWriter(new BufferedWriter(ostream), true);

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				writer.println(line);
			}

			return ostream.toString();
		} catch (IOException ex) {
			Logo.error("Can\'t open file " + name, ctx);
			return null;
		}
	}

	private String fileToString(String path, LContext ctx) {
		byte[] result;

		try {
			result = readFile(path);
		} catch (IOException ex) {
			Logo.error("Can\'t open file " + path, ctx);
			return null;
		}

		return new String(result);
	}

	private Object prim_stringtofile(Object path, Object string, LContext ctx) {
		String dst = Logo.serialize(path);
		String src = string instanceof String ? (String) string : Logo.serialize(string);

		try {
			FileWriter writer = new FileWriter(dst);
			writer.write(src, 0, src.length());
			writer.close();
		} catch (IOException ex) {
			Logo.error("Can\'t write file " + dst, ctx);
		}

		return null;
	}

	private Object prim_file(Object path) {
		String str = Logo.serialize(path);
		return (new File(str)).exists();
	}

	private Object prim_setread(Object str) {
		this.readtext = Logo.serialize(str);
		this.textoffset = 0;
		return null;
	}

	private Object prim_getc() {
		if (this.textoffset >= this.readtext.length())
			return "";
		this.textoffset++;
		return this.readtext.substring(this.textoffset - 1, this.textoffset);
	}

	private Object prim_peek() {
		if (this.textoffset >= this.readtext.length())
			return "";
		return this.readtext.substring(this.textoffset, this.textoffset + 1);
	}

	private Object prim_readline() {
		String result = "";
		int newlineIndex = this.readtext.indexOf("\n", this.textoffset);
		if (newlineIndex == -1) {
			if (this.textoffset < this.readtext.length()) {
				result = this.readtext.substring(this.textoffset, this.readtext.length());
				this.textoffset = this.readtext.length();
			}
		} else {
			result = this.readtext.substring(this.textoffset, newlineIndex);
			this.textoffset = newlineIndex + 1;
		}

		if (result.length() == 0) {
			return result;
		} else {
			if (result.charAt(result.length() - 1) == 13) {
				result = result.substring(0, result.length() - 1);
			}

			return result;
		}
	}

	private Object prim_eot() {
		return this.textoffset >= this.readtext.length();
	}

	private Object prim_lineback() {
		int lastNewlineIndex = this.readtext.lastIndexOf("\n", this.textoffset - 2);
		if (lastNewlineIndex < 0) {
			this.textoffset = 0;
		} else {
			this.textoffset = lastNewlineIndex + 1;
		}

		return null;
	}

	private Object prim_lineread() {
		return this.readtext;
	}

	private Object prim_filenamefrompath(Object path) {
		return (new File(Logo.serialize(path))).getName();
	}

	private Object prim_dirnamefrompath(Object path) {
		File file = new File(Logo.serialize(path));
		return file.isDirectory() ? file.getPath() : file.getParent();
	}

	private Object prim_dir(Object path) {
		String[] list = (new File(Logo.serialize(path))).list();
		return list == null ? new Object[0] : list;
	}

	private Object prim_setfread(Object path, LContext ctx) {
		String str = Logo.serialize(path);

		try {
			this.freader = new BufferedReader(new FileReader(str));
		} catch (IOException ex) {
			Logo.error("Can\'t fread " + str, ctx);
		}

		return null;
	}

	private Object prim_freadline() {
		try {
			return this.freader.readLine();
		} catch (IOException ex) {
			return null;
		}
	}

	private Object prim_feot() {
		String str = null;

		try {
			this.freader.mark(1000);
			str = this.freader.readLine();
			this.freader.reset();
		} catch (IOException ex) {
		}

		return str == null;
	}

	private Object prim_fclose(LContext ctx) {
		if (this.freader == null) {
			return null;
		} else {
			try {
				this.freader.close();
				this.freader = null;
			} catch (IOException ex) {
				Logo.error("fclose error", ctx);
			}

			return null;
		}
	}

	private Object prim_logopen(Object path, LContext ctx) {
		String str = Logo.serialize(path);

		try {
			this.logwriter = new PrintWriter(new BufferedWriter(new FileWriter(str)));
		} catch (IOException ex) {
			Logo.error("Can\'t open log for " + str, ctx);
		}

		return null;
	}

	private Object prim_logprint(Object str) {
		this.logwriter.println(Logo.serialize(str));
		return null;
	}

	private Object prim_logclose() {
		this.logwriter.close();
		this.logwriter = null;
		return null;
	}

	private Object prim_erfile(Object path, LContext ctx) {
		String str = Logo.serialize(path);
		File file = new File(str);

		try {
			file.delete();
		} catch (Exception ex) {
			Logo.error("Can\'t delete file " + str, ctx);
		}

		return null;
	}

	private Object prim_files(Object path, LContext ctx) {
		String str = Logo.serialize(path);
		File file = new File(str);

		try {
			File[] list = file.listFiles();
			String[] pathList = new String[list.length];

			for (int i = 0; i < list.length; ++i) {
				pathList[i] = list[i].getCanonicalPath();
			}

			return pathList;
		} catch (Exception ex) {
			Logo.error("Can\'t list directory for " + str, ctx);
			return null;
		}
	}

	private Object prim_serialize(Object path, Object object, LContext ctx) {
		String str = Logo.serialize(path);

		try {
			FileOutputStream ostream = new FileOutputStream(str);
			ObjectOutputStream data = new ObjectOutputStream(ostream);
			data.writeObject(object);
			data.flush();
			ostream.close();
		} catch (IOException ex) {
			Logo.error("Can\'t write file " + str, ctx);
		}

		return null;
	}

	private Object prim_bytestofile(Object path, Object array, LContext ctx) {
		String dst = Logo.serialize(path);
		byte[] src = (byte[]) array;

		try {
			FileOutputStream ostream = new FileOutputStream(dst);
			ostream.write(src);
			ostream.close();
		} catch (IOException var7) {
			Logo.error("Can\'t write file " + dst, ctx);
		}

		return null;
	}

	private Object prim_filetobytes(Object path, LContext ctx) {
		String src = Logo.serialize(path);
		byte[] dst = null;

		try {
			dst = readFile(src);
		} catch (IOException ex) {
			Logo.error("Can\'t open file " + src, ctx);
		}

		return dst;
	}

	private Object prim_setmoddate(Object path, Object date, LContext ctx) {
		String str = Logo.serialize(path);
		long value = Logo.toLong(date, ctx);

		try {
			File file = new File(str);
			file.setLastModified(value);
		} catch (Exception var8) {
			Logo.error("Can\'t set mod date " + str, ctx);
		}

		return null;
	}

	private Object prim_mkdir(Object path) {
		String str = Logo.serialize(path);
		(new File(str)).mkdir();
		return null;
	}

	private byte[] readFile(String filename) throws IOException {
		File file = new File(filename);
		int size = (int) file.length();
		FileInputStream istream = new FileInputStream(file);
		DataInputStream data = new DataInputStream(istream);
		byte[] dst = new byte[size];
		data.readFully(dst);
		istream.close();
		return dst;
	}
}
