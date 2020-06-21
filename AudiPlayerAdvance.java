import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.NonWritableChannelException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageTypeSpecifier;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.omg.CORBA.FloatSeqHelper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class AudiPlayerAdvance implements MouseMotionListener,ActionListener  {

	private  JFrame frame;
	private  JPanel mainPanel; 
	private  JPanel panelTitle;
	private  JPanel panelStatus;
	private  JLabel labelListening;
	private  JButton exitButton;
	private  JButton pauseButton;
	private  JButton playButton;
	private  JButton stopButton;
	private  JButton openButton;
	private InputStream updatedSelectedFile;
	private AdvancedPlayer playMp3;
	
	
	private static int dragX=0;
	private static int dragY=0;
	private String playingSong="Playing";
	private File selectedFile;
	private long totalLength;
	private long pause=0;
	
	//Number of threads managing from executor
	ExecutorService executorService=Executors.newFixedThreadPool(4);
	
	public void displayFrame()
	{
		frame=new JFrame();
		
		frame.setUndecorated(true);
		frame.setSize(400, 170);
		
		
		//Declare GridBagConstraint and GridBagLayout for location of component
		GridBagConstraints gbc=new GridBagConstraints();
		
	    
		//*Title panel and exit button decleration for quit app
		panelTitle=new JPanel(new GridBagLayout());
		frame.getContentPane().add(panelTitle, BorderLayout.NORTH);		
		panelTitle.setBackground(new Color(7,137,111));
		panelTitle.setPreferredSize(new Dimension(400, 30));
		
		
		exitButton=new JButton();
		exitButton.setPreferredSize(new Dimension(30, 30));
		try 
		{
			
			exitButton.setIcon(new ImageIcon(findPath("quitButton")));
		} 
		catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		catch (SAXException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		gbc.weightx=1.0;
		gbc.weighty=0.0;
		gbc.anchor=GridBagConstraints.FIRST_LINE_END;
		
		panelTitle.add(exitButton, gbc);
		
		
		//Exit from application
				exitButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
						
					}
				});
	   
		
				
		//Status panel,Listening Label and Status buttons configuration		
		panelStatus=new JPanel(new GridBagLayout());
		frame.getContentPane().add(panelStatus,BorderLayout.CENTER);
		panelStatus.setBackground(new Color(7,137,111));
		panelStatus.setPreferredSize(new Dimension(400,140));
		labelListening=new JLabel(playingSong);
		labelListening.setBackground(Color.BLACK);

		gbc.gridx=0;
		gbc.gridy=0;
		gbc.gridwidth=4;
		gbc.gridheight=1;
		gbc.weightx=0.5;
		gbc.weighty=0.0;
	    gbc.anchor=GridBagConstraints.CENTER;
		
		panelStatus.add(labelListening, gbc);
		
		
		pauseButton=new JButton();
		pauseButton.setBackground(new Color(7,63,86));
		try 
		{
			pauseButton.setIcon(new ImageIcon(findPath("pauseButton")));
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		pauseButton.setPreferredSize(new Dimension(67,65));
		

		gbc.gridx=0;
		gbc.gridy=1;
		gbc.gridheight=1;
		gbc.gridwidth=1;
		gbc.weightx=0.5;
		gbc.weighty=0.0;
		gbc.insets=new Insets(30, 10, 0, 0);
		gbc.anchor=GridBagConstraints.FIRST_LINE_START;
		
		
		panelStatus.add(pauseButton, gbc);
		
		pauseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				playButton.setEnabled(true);
				
				executorService.submit(pauseFile);
			}
		});
	
	    
		playButton=new JButton();
	    playButton.setBackground(new Color(7,63,86));
	    try 
	    {
			playButton.setIcon(new ImageIcon(findPath("playButton")));
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    playButton.setPreferredSize(new Dimension(67,65));
	    
		gbc.gridx=1;
		gbc.gridy=1;
		gbc.gridheight=1;
		gbc.gridwidth=1;
        
		
		panelStatus.add(playButton, gbc);
		
		playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				pauseButton.setEnabled(true);
				
				executorService.submit(playFile);
				
			}
		});
		
		
		
		stopButton=new JButton();
	    stopButton.setBackground(new Color(7,63,86));
	    try 
	    {
			stopButton.setIcon(new ImageIcon(findPath("stopButton")));
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    stopButton.setPreferredSize(new Dimension(67,65));
		
		gbc.gridx=2;
		gbc.gridy=1;
        gbc.gridheight=1;
        gbc.gridwidth=1;

		
		panelStatus.add(stopButton, gbc);
		
		stopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				playButton.setEnabled(true);
				pauseButton.setEnabled(true);
				
				executorService.submit(stopFile);
			}
		});
		
		

		openButton=new JButton();
	    openButton.setBackground(new Color(7,63,86));
	    try 
	    {
			openButton.setIcon(new ImageIcon(findPath("openFileButton")));
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    openButton.setPreferredSize(new Dimension(67,65));
		
		gbc.gridx=3;
		gbc.gridy=1;
        gbc.gridheight=1;
        gbc.gridwidth=1;
		
		panelStatus.add(openButton, gbc);
		
		
		openButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
			    executorService.submit(openFile);
				
			}
		});
		
		
		frame.setVisible(true);
		frame.addMouseMotionListener(this);
	}
	
	public static void main(String[] args)
	{
		AudiPlayerAdvance audioPlayer=new AudiPlayerAdvance();
		audioPlayer.displayFrame();
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		dragX=dragX+e.getX();
		dragY=dragY+e.getY();
		
		frame.setBounds(dragX, dragY, 400, 170);
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	Runnable openFile =()->
	{
		
		playButton.setEnabled(true);
		pauseButton.setEnabled(true);
		
		JFileChooser chooser = null;
		try 
		{
			chooser = new JFileChooser(findPath("mp3File"));
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		chooser.setFileFilter(new FileNameExtensionFilter("mp3 extension", "mp3"));
		chooser.showSaveDialog(null);
	
		
		
		try 
		{
		 
			System.out.println("******");
			
			selectedFile=chooser.getSelectedFile();
			playingSong=selectedFile.getName().split("\\.")[0];
			labelListening.setText(playingSong);
			playMp3.close();
			pause=0;
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
		}
		
	};
	
	
	
	//Play choosing file using with thread executor
	Runnable playFile =()->
	{
		playButton.setEnabled(false);
		
		try 
		{
			updatedSelectedFile=new FileInputStream(selectedFile);
			
			if(pause==0)
			{
				try 
				{
					playMp3=new AdvancedPlayer(updatedSelectedFile);
					try 
					{
						totalLength=updatedSelectedFile.available();
					} 
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					playMp3.play();
				} 
				catch (JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			else 
			{
			  try 
			  {
				playMp3=new AdvancedPlayer(updatedSelectedFile);
				
				try 
				{
					updatedSelectedFile.skip(totalLength-pause);
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				playMp3.play();
			  } 
			  catch (JavaLayerException e) 
			  {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			}
			
			
			
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	};
	
	//Stop playing file using with thread executor
	Runnable stopFile=()->
	{
		if(playMp3!=null)
		{
			pause=0;
			playMp3.close();
			
		}
	};
	
	
	//Pause playing file and save file length until playing piece.
	Runnable pauseFile=()->
	{
		
		pauseButton.setEnabled(false);
		
		if(playMp3!=null)
		{
			try 
			{
				pause=updatedSelectedFile.available();
				playMp3.close();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	public String findPath(String xmlTag) throws ParserConfigurationException, SAXException, IOException
	{
		File file=new File("./mp3ver2/mp3Config.xml");
		
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		
	    DocumentBuilder dBuilder=dbf.newDocumentBuilder();
			
			
	    Document doc=dBuilder.parse(file);
				
		String stopIcon=doc.getElementsByTagName(xmlTag).item(0).getTextContent();
						
		return stopIcon;
	}
}

