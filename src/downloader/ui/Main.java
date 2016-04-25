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
		
		JFrame Jeff = new JFrame();
		Jeff.setMinimumSize(new Dimension(400, 400));
		Jeff.setLocationRelativeTo(null);
		
		//partie HAUTE : barres de téléchargement
		JPanel JP_dl = new JPanel();
		JP_dl.setLayout(new StackLayout());
		
		//partie BASSE : texfield + bouton add
		JPanel JP_bottom = new JPanel();

		JP_bottom.setLayout(new BorderLayout());
		
		Jeff.add(JP_dl, BorderLayout.NORTH);
		Jeff.add(JP_bottom, BorderLayout.SOUTH);
		
		String[] uris = {
			"http://iihm.imag.fr/blanch/M1/TLI/tps/3-notification/index.html",
			"http://docs.oracle.com/javase/6/docs/api/javax/swing/JProgressBar.html",
			"http://docs.oracle.com/javase/6/docs/api/java/awt/Container.html",
			"http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html"
		};
		
		for(String uri : uris) {
			launchDL(uri, JP_dl);
		}
		
		//zone de saisie
		JTextField textphild = new JTextField();
		textphild.setPreferredSize(new Dimension(120, 20));
		JP_bottom.add(textphild, BorderLayout.CENTER);
		//bouton confirmation
		JButton bt_add = new JButton("add");
		JP_bottom.add(bt_add, BorderLayout.EAST);
		
		bt_add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				launchDL(textphild.getText(), JP_dl);
				Jeff.pack();
			}
	    });
		
		Jeff.setVisible(true);
		Jeff.pack();
	}	
	
	public static void launchDL(String url, JPanel JP) {
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
	    
	    JP.add(dlText);
	    
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
				JP.remove(dlText);
				JP.remove(displayPart);
				JP.revalidate(); 
				JP.repaint();
			}
	    });
		
		displayPart.add(pB);
		displayPart.add(btRun);
		displayPart.add(btDelete);

	    JP.add(displayPart);
		
		d.addPropertyChangeListener(new PropertyChangeListener() {		
			public void propertyChange(PropertyChangeEvent evt) {
				pB.setValue(d.getProgress());
			}
		});
	}
	
}
