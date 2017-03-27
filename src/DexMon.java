
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

class DexMon
{
	public int id = -1;
	public String name = null;
	public String location = null;
	public boolean captured = false;
	
	public int [] attribs = null;	
	
	// colour is based off the first filter that matches this pokemon
	public java.awt.Color colour = null;	
	
	private String sid = null;
	public String getSid()
	{
		if(sid==null)
		{
			sid = String.valueOf(id);
			while(sid.length()<3) sid = "0" + sid;
		}
		return sid;
	}
	
	
	public static DexMon parse(Node nNodeMon, DexMonFilter [] filters)
	{
		Element eElement = (Element) nNodeMon;
					
		DexMon mon = new DexMon();
		mon.id = Integer.parseInt(eElement.getAttribute("id"));
		mon.name = eElement.getAttribute("name");
		
		NodeList nListLocation = eElement.getElementsByTagName("location");
		if(nListLocation!=null && nListLocation.getLength()>0)
		{
			mon.location = nListLocation.item(0).getTextContent();
		}
		
		
		// process filter attributes
		mon.attribs = new int[filters.length];
		for(int f=0;f<filters.length;f++)
		{			
			NodeList nNode = eElement.getElementsByTagName(filters[f].tag);
			if(nNode!=null && nNode.getLength()>0)
			{
				mon.attribs[f] = Integer.parseInt(nNode.item(0).getTextContent());
				
				// check if this should be the default mob colour
				if(mon.colour==null && filters[f].has_colour)
				{
					if(filters[f].type.equals("sho") && mon.attribs[f]==1)
					{
						mon.colour = new java.awt.Color(filters[f].colour_red, filters[f].colour_green,filters[f].colour_blue);
					}
				}
			}
			else
			{
				mon.attribs[f] = 0;
			}
		}
		return mon;
	}
}