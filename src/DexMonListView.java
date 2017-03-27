
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EtchedBorder;


class DexMonListView extends JPanel
{	
	public static DexMonListView selectedMon = null;
	private static Color DEFAULT_COLOUR = null;
	private static final int ICON_W = 24;
	private static final int ICON_H = 24;
	
	private static final String IMG_GOT = "res/img/tick.png";
	private static final String IMG_NOT = "res/img/cross.png";
	
	private static BufferedImage img_tick = null;
	private static BufferedImage img_cross = null;
	
	private JPanel inner;
	private JLabel lblDexNum;
	private JLabel lblName;
	private JImage imgGot;
	
	private DexMon mon;
	
	
	public DexMonListView(final DexMon mon)
	{
		if(DEFAULT_COLOUR==null)
		{
			DEFAULT_COLOUR = this.getBackground();
		}
		this.mon = mon;
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(1,4,1,4));
		
		String dexnum = String.valueOf(mon.id);
		while(dexnum.length()<3) dexnum = "0" + dexnum;
		lblDexNum = new JLabel(dexnum + ". ");
		lblName = new JLabel(mon.name);
		
		if(mon.captured)
		{
			if(img_tick==null) img_tick = JImage.loadFromFile(IMG_GOT);
			imgGot = new JImage(img_tick,ICON_W,ICON_H);
		}
		else
		{
			if(img_cross==null) img_cross = JImage.loadFromFile(IMG_NOT);
			imgGot = new JImage(img_cross,ICON_W,ICON_H);
		}
		imgGot.addMouseListener(new MouseListener()
		{
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseClicked(MouseEvent e)
			{
				mon.captured = !mon.captured;
				BufferedImage img = null;
				if(mon.captured)
				{
					if(img_tick==null) img_tick = JImage.loadFromFile(IMG_GOT);
					imgGot.setImage(img_tick,ICON_W,ICON_H);
				}
				else
				{
					if(img_cross==null) img_cross = JImage.loadFromFile(IMG_NOT);
					imgGot.setImage(img_cross,ICON_W,ICON_H);
				}
				imgGot.repaint();
				onToggleClick(mon);
            }
        });
		
		inner = new JPanel();
		inner.setLayout(new BorderLayout());
		inner.add(lblDexNum, BorderLayout.WEST);
		inner.add(lblName, BorderLayout.CENTER);
		inner.add(imgGot, BorderLayout.EAST);
		add(inner);
		
		
		setFocused(false);
		
		// configure mouse listener
		this.addMouseListener(new MouseListener()
		{
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseClicked(MouseEvent e)
			{
				onClick(mon);
            }
        });
	}
	
	public void setFocused(boolean focused)
	{
		if(!focused)
		{
			Color col = getColour();
			//inner.setBackground(col);
			inner.setBackground(col);
			inner.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, col, col));
		}
		else
		{
			Color col = getColour();
			col = makeDarker(col);
			Color col2 = makeDarker(col);
			
			//inner.setBackground(col);
			inner.setBackground(col);
			inner.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, col2, col));
		}
	}
	public void setSelected()
	{	
		if(selectedMon!=null) selectedMon.setFocused(false);
		selectedMon = this;
		setFocused(true);
	}
	
	protected void onClick(DexMon mon){};
	protected void onToggleClick(DexMon mon){};
	
	private Color getColour()
	{
		if(mon.colour!=null) return mon.colour;
		return DEFAULT_COLOUR;
	}

	public Color makeDarker(Color originalColour)
	{
		float hsbVals[] = Color.RGBtoHSB( originalColour.getRed(), originalColour.getGreen(), originalColour.getBlue(), null );
		//Color highlight = Color.getHSBColor( hsbVals[0], hsbVals[1], 0.5f * ( 1f + hsbVals[2] ));
		Color shadow = Color.getHSBColor( hsbVals[0], hsbVals[1], 0.9f * hsbVals[2] );
		return shadow;
	}
}