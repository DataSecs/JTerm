package main.java.com.jterm;

import java.util.ArrayList;
import javax.swing.JFrame;

public class Window {

	public static int windowCount = 0;
	
	private int id;
	private JFrame window;
	
	/*
	* Window() void
	* 
	* Create a new JFrame window.
	* 
	* ArrayList<String> options - command options
	* 
	* -h
	*     Prints help information
	*     
	* -t title
	* 	  Sets the window title in 
	* 	  the header
	* 
	* -w width
	* 	  Sets the width of the window
	* 
	* -l height
	* 	  Sets the height of the window
	* 
	* -r
	* 	  Sets the window to be able to
	*	  be resized  
	* 	  
	*/
	Window(ArrayList<String> options) {
		
		String title = "";
		int width = 500, height = 500;
		boolean resizable = false;
		
		boolean titleNext = false, heightNext = false, widthNext = false;
		
		for (String option: options)
		{
			if (option.equals("-h"))
			{
				System.out.println("Command syntax:\t\nwindow [-h] [-r] [-w width] [-l height] [-t title]\n\nCreates a new programmable GUI window.\nDefault title is \"JTerm Window\", and the default width and height of the window is 500 x 500.");
				return;
				
			}
			else if(option.equals("-t"))
			{
				titleNext = true;
				
			}
			else if(option.equals("-w"))
			{
				widthNext = true;
				
			}
			else if(option.equals("-l"))
			{
				heightNext = true;
				
			}
			else if(widthNext)
			{
				width = Integer.parseInt(option);
				widthNext = false;
				
			}
			
			else if(heightNext)
			{
				height = Integer.parseInt(option);
				heightNext = false;
				
			}
			else if (option.equals("-r"))
			{
				resizable = true;
				
			}
			else if(titleNext)
			{
				title += option + " ";
				
			}
			
		}
		
		windowCount += 1;
		id = windowCount;
		
		JFrame window = new JFrame(title);
		window.setSize(width, height);
		window.setVisible(true);
		window.setResizable(resizable);
		
		this.window = window;
		
	}
	
	/*
	* GetId() int
	* 
	* Returns the process ID of the window
	*/
	public int GetId() {
		
		return id;
		
	}
	
	/*
	* GetId() JFrame
	* 
	* Returns the JFrame object displaying
	* the window
	*/
	public JFrame GetFrame() {
		
		return this.window;
		
	}
	
}
