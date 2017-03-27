import java.util.Vector;

class DexResult
{
	private Vector<DexMon> mons;

	public DexResult(DexMon [] mons)
	{
		if(mons!=null)
		{
			this.mons = new Vector<DexMon>();
			for(int i=0;i<mons.length;i++)
			{
				this.mons.add(mons[i]);
			}
		}
	}
	public DexResult(Vector<DexMon> mons)
	{
		this.mons = mons;
	}
	
	public int size()
	{
		return mons==null?0:mons.size();
	}
	public DexMon get(int index)
	{
		return mons==null?null:mons.get(index);
	}
	public DexResult applySHOFilter(int index, int state)
	{
		if(mons==null) return null;
		int cnt = mons.size();
		
		Vector <DexMon> results = new Vector<DexMon>();
		for(int i=0;i<cnt;i++)
		{
			if(state==2 && mons.get(i).attribs[index]==1)
			{
				results.add(mons.get(i));
			}
			else if(state==1 && mons.get(i).attribs[index]!=1)
			{
				results.add(mons.get(i));
			}
			
		}
		return new DexResult(results);
	}
	public DexResult filterName(String txt)
	{
		if(mons==null) return null;
		txt = txt.toLowerCase();
		int cnt = mons.size();
		
		Vector <DexMon> results = new Vector<DexMon>();
		for(int i=0;i<cnt;i++)
		{
			if(mons.get(i).name.toLowerCase().indexOf(txt)>=0)
				results.add(mons.get(i));
			else if(mons.get(i).getSid().toLowerCase().indexOf(txt)>=0)
				results.add(mons.get(i));
			
		}
		return new DexResult(results);
	}
	
	public DexResult filterCaptured(int state)
	{
		if(mons==null) return null;
		int cnt = mons.size();
		
		Vector <DexMon> results = new Vector<DexMon>();
		for(int i=0;i<cnt;i++)
		{
			if(state==2 && mons.get(i).captured)
			{
				results.add(mons.get(i));
			}
			else if(state==1 && !mons.get(i).captured)
			{
				results.add(mons.get(i));
			}
		}
		return new DexResult(results);
	}
	
}