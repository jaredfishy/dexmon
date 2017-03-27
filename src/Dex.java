
import java.io.*;
import java.net.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Vector;

 
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

class Dex
{
	private int load_limit = 30000;
	private DexMon [] dex;
	private DexMonFilter [] filters;
	private Vector<Integer> user_data;
	private boolean user_data_changed = false;
	private boolean dex_config_loaded = false;
	public boolean isConfigLoaded()
	{
		return dex_config_loaded;
	}
	
	public int dex_count = 0;
	public int user_count_these = 0; // how many of the currently loaded dex the user has
	public int user_count_extra = 0; // how many the user has that are not in the current dex
	
	public boolean hasChanged()
	{
		return user_data_changed;
	}
	
	public Dex(){}
	public Dex(Vector<DexMon> dex)
	{
		this.dex = new DexMon[dex.size()];
		for(int i=0;i<this.dex.length;i++)
		{
			this.dex[i] = dex.get(i);
		}
	}
	
	public Dex(String dex_file, String user_file)
	{
		try
		{
			dex_config_loaded = LoadDex(dex_file);
			if(dex_config_loaded)
			{
				LoadUserFile(user_file);
			}
		}
		catch(Exception err)
		{
			System.out.println("Error:" + err.toString());
		}
	}
	
	public DexMonFilter [] getFilters()
	{
		return filters;
	}
	
	public DexResult getAll()
	{
		return new DexResult(dex);
	}
	
	public void setCaptured(int id)
	{
		if(user_data==null)
		{
			user_data = new Vector<Integer>();
			user_data.add(id);
			user_data_changed = true;
			user_count_these++;
			System.out.println("Added " + id + ".");
		}
		else
		{
			int cnt = user_data.size();
			for(int i=0;i<cnt;i++)
			{
				int cand_id = user_data.get(i);
				if(cand_id==id)
				{
					// already added
					return;
				}
				else if(cand_id>id)
				{
					// we have passed where it should be, insert it
					user_data.add(i, id);
					user_data_changed = true;
					user_count_these++;
					System.out.println("Added " + id + ".");
					return;
				}
			}
			
			// if we get here, we are not added
			user_data.add(id);
			user_data_changed = true;
					user_count_these++;
			System.out.println("Added " + id + ".");
		}
	}
	public void setNotCaptured(int id)
	{
		int index = userIndexOf(id);
		if(index>=0)
		{
			user_count_these--;
			user_data.remove(index);
			user_data_changed = true;
			System.out.println("Removed " + id + ".");
		}
	}
	private int dexIndexOf(int id)
	{
		if(dex==null) return -1;
		int cnt = dex.length;
		for(int i=0;i<cnt;i++)
		{
			if(dex[i].id==id) return i;
		}
		return -1;
		
	}
	
	private int userIndexOf(int id)
	{
		if(user_data==null) return -1;
		int cnt = user_data.size();
		for(int i=0;i<cnt;i++)
		{
			if(user_data.get(i)==id) return i;
		}
		return -1;
	}
	private boolean LoadDex(String filepath)
	{
		try
		{
			File fXmlFile = new File(filepath);
			if(!fXmlFile.exists())
			{
				return false;
			}
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			// load the config
			Node nNodeFilters = doc.getElementsByTagName("filters").item(0);
			NodeList nListFilters = ((Element)nNodeFilters).getElementsByTagName("filter");
			
			System.out.println("Found  " + nListFilters.getLength() + " filters.");
			int f_cnt = nListFilters.getLength();
			
			filters = new DexMonFilter[f_cnt];
			for(int i=0;i<f_cnt;i++)
			{
				Node nNodeFilter = nListFilters.item(i);
				filters[i] = DexMonFilter.parse(nNodeFilter);
			}
			
			// load the mons
			Node nNodeDex = doc.getElementsByTagName("mons").item(0);
			NodeList nListDex = ((Element)nNodeDex).getElementsByTagName("mon");
			System.out.println("Loading  " + nListDex.getLength() + " mons.");
			
			//Vector<DexMon> v_mons = new Vector<DexMon>();
			
			int cnt = nListDex.getLength();
			if(cnt>load_limit) cnt = load_limit;
			dex = new DexMon[cnt];
			dex_count = cnt;
			for(int i=0;i<cnt;i++)
			{
				Node nNodeMon = nListDex.item(i);
				
				if (nNodeMon.getNodeType() == Node.ELEMENT_NODE)
				{
					dex[i] = DexMon.parse(nNodeMon, filters);
				}
			}
			
			return true;
		}
		catch (Exception err)
		{
			err.printStackTrace();
			return false;
		}
	}
	
	private void LoadUserFile(String filepath)
	{
		try
		{
			File file = new File(filepath);
			if(!file.exists())
			{
				file.createNewFile();
			}
			else
			{
				user_data = new Vector<Integer>();
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null)
				{
					// process the line.

					try
					{
						int id = Integer.parseInt(line);
						user_data.add(id);
						
						int index = dexIndexOf(id);
						if(index!=-1)
						{
							dex[index].captured = true;
							user_count_these++;
						}
						else
						{
							user_count_extra++;
						}
						
							
					}
					catch(Exception err)
					{
						err.printStackTrace();
					}
				}
				br.close();
				
			}
		}
		catch(Exception err)
		{
			err.printStackTrace();
		}
	}
	public void SaveUserData(String filepath)
	{
		try
		{
			int cnt = user_data==null?0:user_data.size();
			System.out.println("Writing " + cnt + " entries");
			if(cnt>0)
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
				String newLine = System.getProperty("line.separator");
				for(int i=0;i<cnt;i++)
				{
					writer.write(String.valueOf(user_data.get(i)) + newLine);
				}
				
				writer.close();
			user_data_changed = false;
			}
		}
		catch(Exception err)
		{
			err.printStackTrace();
		}
	}
	
	
	public static void DownloadImage(DexMon mon, JImage imgField)
	{
		String sid = mon.getSid();
		System.out.println("Attempting to download img for \""+sid+"\"");
		try
		{
			URL url = new URL("http://pokeapi.co/media/img/"+mon.id+".png");
			
			//InputStream in = new BufferedInputStream(url.openStream());
			
			URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			urlConnection.connect();
			InputStream in = urlConnection.getInputStream();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while (-1!=(n=in.read(buf)))
			{
			   out.write(buf, 0, n);
			}
			out.close();
			in.close();
			byte[] response = out.toByteArray();
			
			// check directory exists
			File dir = new File(Dexter.IMG_CACHE);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			
			String savePath = Dexter.IMG_CACHE + "\\";
			savePath += sid + ".png";
			
			FileOutputStream fos = new FileOutputStream(savePath);
			fos.write(response);
			fos.close();
			
			if(imgField!=null)
			{
				BufferedImage img = ImageIO.read(new File(savePath));
				imgField.setImage(img);
				imgField.repaint();
			}
			
		}
		catch(Exception err)
		{
			err.printStackTrace();
			System.out.println("Failed to download image \""+sid+"\". Exception: " + err.toString());
		}
	}	
}