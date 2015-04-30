package com.CollabEdit;

public class userData {
	String user;
	int prev[] = {-1,-1}; //0th loc: line; 1st loc: char
	userData(String user)
	{
		this.user = user;
	//	this.prev = prev;
	}
	public void setUser(String user)
	{
		this.user = user;
	}
	public void setPrev(int[] prev)
	{
		this.prev = prev;
	}
	
	public int[] getPrev()
	{
		System.out.println("returning prev array: "+prev[0]+"  "+prev[1]);
		return prev;
	}
	public String getUser()
	{
		return user;
	}
}
