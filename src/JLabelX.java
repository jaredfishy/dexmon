
import javax.swing.*;
import java.awt.*;

class JLabelX extends JLabel
{
	public JLabelX(String text)
	{
		this(text,true);
	}
	public JLabelX(String text, boolean bold)
	{
		super(text);
		setBold(bold);
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