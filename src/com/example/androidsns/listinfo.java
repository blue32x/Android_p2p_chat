package com.example.androidsns;

public class listinfo {
	private int image;
	private String text;

	listinfo(int image,String text)
	{
		this.image=image;
		this.text=text;
	}
	public int getImage_ID(){return image;}
	public void setImage_ID(int i){image=i;}
	public String  getString(){return text;}
	public void setString(String s){text=s;}
}
