

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
class FilterButton_SHO extends JPanel
{
	class JButtonColoured extends JButton
	{
		public boolean focused = false;
		public Color focus_colour;
		public JButtonColoured(String text, Color focus_colour)
		{
			super(text);
			this.focus_colour = focus_colour;
			this.setMargin(new Insets(0, 0, 0, 0));
			this.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e) { onClick(); }
			});
		}
		public void setFocused(boolean focused)
		{
			this.focused = focused;
			if(focused) this.setBackground(focus_colour);
			else this.setBackground(DEFAULT_COLOUR);
		}
		public void onClick(){}
	}
	private static final Color SHOW_COLOUR = new Color(153,255,153);
	private static final Color HIDE_COLOUR = new Color(255,153,153);
	private static final Color ONLY_COLOUR = new Color(153,204,255);
	private static Color DEFAULT_COLOUR = null;
	
	private JButtonColoured btnShow, btnHide, btnOnly;
	public int getState()
	{
		if(btnShow.focused) return 0;
		if(btnHide.focused) return 1;
		if(btnOnly.focused) return 2;
		return -1;
	}
	public FilterButton_SHO(String label)
	{
		this.setLayout(new BorderLayout());
		//this.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		JPanel panel_butts = new JPanel();
		panel_butts.setLayout(new GridLayout(0,3));
		
		btnShow = new JButtonColoured("Show", SHOW_COLOUR)
		{
			public void onClick()
			{
				updateColours(0);
				onStateChange(0);
			}
		};
		panel_butts.add(btnShow);
		
		btnHide = new JButtonColoured("Hide", HIDE_COLOUR)
		{
			public void onClick()
			{
				updateColours(1);
				onStateChange(1);
			}
		};
		panel_butts.add(btnHide);
		
		btnOnly = new JButtonColoured("Only", ONLY_COLOUR)
		{
			public void onClick()
			{
				updateColours(2);
				onStateChange(2);
			}
		};
		panel_butts.add(btnOnly);
		
		
		this.add(new JLabel(label), BorderLayout.WEST);
		this.add(panel_butts, BorderLayout.EAST);
		
		if(DEFAULT_COLOUR==null) DEFAULT_COLOUR = btnShow.getBackground();
		updateColours(0);
	}
	
	private void updateColours(int index)
	{
		btnShow.setFocused(index==0);
		btnHide.setFocused(index==1);
		btnOnly.setFocused(index==2);
	}
	
	public void onStateChange(int new_state){}
}