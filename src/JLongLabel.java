import java.awt.*;
import javax.swing.*;

public class JLongLabel extends JTextArea
{
	private static Color BG_COLOUR = null;
	private static Font FONT = null;
	
	public JLongLabel(String txt)
	{
		this(txt,true);
	}
	public JLongLabel(String txt, boolean bold)
	{
		super(txt);
		if(BG_COLOUR==null)
		{
		// make it look like a regular label
			JLabel lbl = new JLabel("");
			BG_COLOUR = lbl.getBackground();
			FONT = lbl.getFont();
		}
		//this.setBackground(BG_COLOUR);
		this.setFont(FONT);
		this.setLineWrap(true);
		this.setWrapStyleWord(true);
		this.setOpaque(false);
		this.setFocusable(false);
		this.setEditable(false);
		
		this.setBold(bold);
	}
	public void setBold(boolean bold)
	{
		Font f = getFont();
		if(bold)
			setFont(f.deriveFont(f.getStyle() & Font.BOLD));
		else 
			setFont(f.deriveFont(f.getStyle() & ~Font.BOLD));
	}

}