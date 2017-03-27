import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;  
import javax.swing.*;

public class JImage extends JPanel{

    private BufferedImage image = null;
	private int width;
	private int height;

	public JImage()
	{
		this(null);
	}
    public JImage(BufferedImage image)
	{
		setLayout(new GridLayout(0,1));
		setOpaque(false);
		if(image!=null)setImage(image);
    }
    public JImage(BufferedImage image, int w, int h)
	{
		setLayout(new GridLayout(0,1));
		setOpaque(false);
		if(image!=null)setImage(image,w,h);
    }
	
	public void setImage(BufferedImage image)
	{
		setImage(image, image.getWidth(), image.getHeight());/*
		this.image = image;
		width = this.image.getWidth();
		height = this.image.getHeight();
		this.setPreferredSize(new Dimension(width, height));
		this.revalidate();*/
	}
	public void setImage(BufferedImage image,int width,int height)
	{
		this.image = image;
		this.width = width;
		this.height = height;
		this.setPreferredSize(new Dimension(width, height));
		this.revalidate();
	}

    @Override
    protected void paintComponent(Graphics g)
	{
        super.paintComponent(g);
		if(image!=null)
			g.drawImage(image, 0, 0, width, height, null); // see javadoc for more info on the parameters            
    }
	
	public void setImageSize(int w, int h)
	{
		if(image!=null)
		{
			width = w;
			height = h;
		}
	}
	
	public static BufferedImage loadFromFile(String path)
	{
		try
		{
			BufferedImage img = ImageIO.read(new File(path));
			return img;
		}
		catch(Exception err)
		{
			System.out.println("Failed to load the image at \""+path+"\". Exception: " + err.toString());
		}
		return null;
	}

}