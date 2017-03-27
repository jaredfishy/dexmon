
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public abstract class DexMonFilter
{
	public static final String FILTER_TYPE_SHO = "sho";
	String name;
	String tag;
	String type;
	
	boolean has_colour = false;
	int colour_red;
	int colour_green;
	int colour_blue;
	
	public abstract String getClearText(int state);
	
	public static DexMonFilter parse(Node config_node)
	{
		DexMonFilter filter = new DexMonFilter_SHO();
		
		String type = ((Element)config_node).getElementsByTagName("type").item(0).getTextContent();
		
		if(type.equals(FILTER_TYPE_SHO))
		{
			filter = new DexMonFilter_SHO();
		}
		else
		{
			return null;
		}
		
		filter.name = ((Element)config_node).getElementsByTagName("name").item(0).getTextContent();
		filter.tag = ((Element)config_node).getElementsByTagName("tag").item(0).getTextContent();
		filter.has_colour = false;
		
		NodeList nListColour = ((Element)config_node).getElementsByTagName("colour");
		if(nListColour!=null && nListColour.getLength()>0)
		{
			//Node nNodeColour = nListColour.item(0);
			Element eColour = (Element)nListColour.item(0);
			try
			{
				
				filter.colour_red = Integer.parseInt(eColour.getAttribute("red"));
				filter.colour_green = Integer.parseInt(eColour.getAttribute("green"));
				filter.colour_blue = Integer.parseInt(eColour.getAttribute("blue"));
				
				filter.has_colour = true;
			}
			catch(Exception err)
			{
				System.out.println("Cannot process colour configured for filter " + filter.name + ".");
				
			}
		}
		return filter;
	}
}