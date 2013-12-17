package com.royxue.one;


public class SQLDemo {

	@com.google.gson.annotations.SerializedName("id")
	private String Id;
	
	private String Name;
	
	public SQLDemo()
	{}
	
	public SQLDemo(String name,String id )
	{
		this.setName(name);
		this.setId(id);		
	}
	
	public final void setName(String name)
	{
		Name = name ;
	}
	
	public final void setId(String id)
	{
		Id = id;
	}
	
	public String getName()
	{
		return Name;
	}
	
	public String getId()
	{
		return Id;
	}
	
	public String toString()
	{
		return getName();
	}
	
}
