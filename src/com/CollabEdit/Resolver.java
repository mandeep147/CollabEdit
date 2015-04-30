package com.CollabEdit;

public class Resolver 
{
	String priority;
	userData user1,user2;
	String file;
	public Resolver(String priority,String file,String userID1,String userID2)
	{
		this.priority = priority;  //prioirty = name of the creator
		System.out.println("^^^^CREATING userData now^^^^^^^^");
		System.out.println("for user1: "+userID1+"   user2: "+userID2); 
		user1 = new userData(userID1);
		user2 = new userData(userID2);
		this.file = file;
	}
	public void setPrev(String user,int line,int ch)
	{
		int prev[] = {line,ch};
		
		if(user1.getUser().equals(user))
		{
			user1.setPrev(prev);
		}
		else if(user2.getUser().equals(user))
		{
			user2.setPrev(prev);
		}	
	}
	public int[] getPrev(String user)
	{
		int prev[] = null;
		System.out.println("in the getPRevv  for: "+user);
		if(user1.getUser().equals(user))
		{
			System.out.println("inside one: ");
			prev = user1.getPrev();
			displayArr(prev);
		}
		else if(user2.getUser().equals(user))
		{
			System.out.println("inside two: ");
			prev = user2.getPrev();
			displayArr(prev);
		}
		
		return prev;
	}
	
	void displayArr(int[] prev)
	{
		for(int i=0;i<prev.length;i++)
			System.out.println("prev: i:"+i+" value: "+prev[i]);
	}
	
	Boolean checkPriority(String user)
	{
		if(user.equals(priority))
			return true;
		return false;
	}
}
