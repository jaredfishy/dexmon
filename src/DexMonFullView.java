
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.File;
import javax.imageio.ImageIO;

class DexMonFullView extends JPanel
{
	public DexMonFullView(final DexMon mon, DexMonFilter [] filters)
	{		
		//this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setLayout(new GridBagLayout());
		this.setLayout(new BorderLayout());
		//this.setBackground(Color.RED);
		
		final JImage monImg = new JImage();
		
		String path = Dexter.IMG_CACHE +  "\\" + mon.getSid() + ".png";
		File f = new File(path);
		BufferedImage img = null;
		if(!f.exists())
		{
			// load the default image
			try
			{
				img = JImage.loadFromFile("res/img/default.png");
				monImg.setImage(img);
			}
			catch(Exception err)
			{
				System.out.println("Failed to load the thing");
			}
			new Thread()
			{
				public void run()
				{
					Dex.DownloadImage(mon, monImg);
				}
			}.start();
		}
		else
		{
			// load the image
			System.out.println("Found image \""+path+"\".");
			try
			{
				img = ImageIO.read(f);
				monImg.setImage(img);
			}
			catch(Exception err)
			{
				System.out.println("Failed to load the thing");
			}
		}
		
		JPanel imgPanel = new JPanel();
		imgPanel.setLayout(new FlowLayout());
		imgPanel.add(monImg);
		this.add(imgPanel, BorderLayout.NORTH);
				
		// details pane
		
		
		GridBagConstraints c_left = new GridBagConstraints();
		c_left.anchor = GridBagConstraints.NORTHWEST;
		c_left.gridwidth = 1;
		c_left.fill = GridBagConstraints.NONE;
		c_left.insets = new Insets(2,4,2,4);
		
		GridBagConstraints c_right = (GridBagConstraints)c_left.clone();
		GridBagConstraints c_both = (GridBagConstraints)c_left.clone();
		
		c_left.gridx = 0;
		c_right.gridx = 1;
		c_right.weightx = 1.0;
		c_right.fill = GridBagConstraints.HORIZONTAL;
		
		c_both.gridx = 0;
		c_both.gridwidth = GridBagConstraints.REMAINDER;
		c_both.fill = GridBagConstraints.HORIZONTAL;
		
		JPanel monDetail = new JPanel();
		monDetail.setLayout(new GridBagLayout());
		int row = 0;
		
		monDetail.add(new JLabel("Id: "), adjust(c_left, row));
		monDetail.add(new JLabelX(String.valueOf(mon.id), false), adjust(c_right, row));
		row++;
		
		monDetail.add(new JLabel("Name: "), adjust(c_left, row));
		monDetail.add(new JLabelX(mon.name, false), adjust(c_right, row));
		row++;
		monDetail.add(new JLabel("Link: "), adjust(c_left, row));
		JLabel btnLink = new JLabel("Open Serebii");
		btnLink.setText("<html><u><bold><b1><a href=''>Open Serebii</a></u></bold></b1></html>");
		btnLink.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				Dexter.openWebpage("http://www.serebii.net/pokedex-xy/"+mon.getSid()+".shtml");
			}
		});
		monDetail.add(btnLink, adjust(c_right, row));
		row++;
		monDetail.add(new JLabel("Obtained:"), adjust(c_left, row));
		row++;
		monDetail.add(new JLongLabel(mon.location, false), adjust(c_both, row));
		row++;
		monDetail.add(new JLabel(" "), adjust(c_left, row));
		row++;
		monDetail.add(new JLabel("Filters:"), adjust(c_left, row));
		row++;
		
		
		for(int i=0;i<filters.length;i++)
		{			
			monDetail.add(new JLabel(filters[i].name), adjust(c_left, row));
			monDetail.add(new JLabelX(filters[i].getClearText(mon.attribs[i]), false), adjust(c_right, row));
			row++;
		}
		
		this.add(monDetail, BorderLayout.CENTER);
		
		/*
		JPanel monDetailSplitter = new JPanel();
		monDetailSplitter.setLayout(new BoxLayout(monDetailSplitter, BoxLayout.Y_AXIS));
		monDetailSplitter.add(monDetail);
		monDetailSplitter.add(new JLongLabel(mon.location));
		
		this.add(monDetailSplitter, BorderLayout.CENTER);*/
		
	}
	
	private GridBagConstraints adjust(GridBagConstraints c, int y)
	{
		GridBagConstraints ret = (GridBagConstraints)c.clone();
		ret.gridy = y;
		return ret;
	}
}