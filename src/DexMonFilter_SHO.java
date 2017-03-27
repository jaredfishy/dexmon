class DexMonFilter_SHO extends DexMonFilter
{
	public DexMonFilter_SHO()
	{
		this.type = FILTER_TYPE_SHO;
	}
	public String getClearText(int value)
	{
		switch(value)
		{
			case 1: return "Yes";
			default: return "No";
		}
	}
}