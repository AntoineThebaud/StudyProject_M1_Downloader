package downloader.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import downloader.fc.Downloader;

public class Main {

	public static void main(String[] args) {
		
		JFrame myFrame = new JFrame();
		myFrame.setMinimumSize(new Dimension(400, 400));
		myFrame.setLocationRelativeTo(null);
		
		//partie HAUTE : barres de téléchargement
		JPanel jpDL = new JPanel();
		jpDL.setLayout(new StackLayout());
		
		//partie BASSE : textfield + bouton add
		JPanel jpBottom = new JPanel();

		jpBottom.setLayout(new BorderLayout());
		
		myFrame.add(jpDL, BorderLayout.NORTH);
		myFrame.add(jpBottom, BorderLayout.SOUTH);
		
		String[] uris = {
			"http://iihm.imag.fr/blanch/M1/TLI/tps/3-notification/index.html",
			"http://docs.oracle.com/javase/6/docs/api/javax/swing/JProgressBar.html",
			"http://docs.oracle.com/javase/6/docs/api/java/awt/Container.html",
			"http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html"
		};
		
		for(String uri : uris) {
			launchAndDisplayDL(uri, jpDL);
		}
		
		//zone de saisie
		JTextField textInput = new JTextField();
		textInput.setPreferredSize(new Dimension(120, 20));
		jpBottom.add(textInput, BorderLayout.CENTER);
		//bouton confirmation
		JButton btAdd = new JButton("add");
		jpBottom.add(btAdd, BorderLayout.EAST);
		
		btAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				launchAndDisplayDL(textInput.getText(), jpDL);
				myFrame.pack();
			}
	    });
		
		myFrame.setVisible(true);
		myFrame.pack();
	}	
	
	public static void launchAndDisplayDL(String url, JPanel jp) {
		Downloader d = new Downloader(url);
		Thread t = new Thread(new Runnable() { 
			public void run() { 
				try {
					d.download();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t.start();
		
	    //texte descriptif :
	    JTextField dlText = new JTextField(url);
	    dlText.setHorizontalAlignment(JTextField.LEFT);
	    dlText.setEditable(false);
	    
	    jp.add(dlText);
	    
	    //progress bar + boutons :
	    JPanel displayPart = new JPanel();
	    displayPart.setLayout(new FlowLayout());
		//-- progress bar
		JProgressBar pB = new JProgressBar(0, 100);
		pB.setValue(0);
		pB.setStringPainted(true);
		//- bouton run/pause
		JButton btRun = new JButton("|>");
		btRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(btRun.getText().equals("|>")) {
					btRun.setText("||");
					d.pause();
				} else {
					btRun.setText("|>");
					d.play();
				}
			}
	    });
		
		//- bouton delete
		JButton btDelete = new JButton("X");
		btDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				t.interrupt();
				jp.remove(dlText);
				jp.remove(displayPart);
				jp.revalidate(); 
				jp.repaint();
			}
	    });
		
		displayPart.add(pB);
		displayPart.add(btRun);
		displayPart.add(btDelete);

	    jp.add(displayPart);
		
		d.addPropertyChangeListener(new PropertyChangeListener() {		
			public void propertyChange(PropertyChangeEvent evt) {
				pB.setValue(d.getProgress());
			}
		});
	}
	
}
