package logo;

import javax.swing.text.JTextComponent;
import java.io.PrintWriter;
import java.util.HashMap;

public class LContext {
	public final Object juststop = new Object();
	public HashMap<String, Symbol> oblist = new HashMap<>();
	public HashMap<Object, HashMap<Object, Object>> props = new HashMap<>();
	public TokenChain iline;
	public Symbol current_function;
	public Symbol ufun;
	public Object ufunresult;
	public boolean willOutput;
	public boolean stop_requested;
	public int priority = 0;
	public Object[] locals;
	public String errormessage;
	public PrintWriter stdout;
	public String fload_filename;
	public String file_field_path = "";
	public JTextComponent status_field;
	public JTextComponent file_field;
	public Object[] console_arguments;
}
