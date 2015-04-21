package gui;

import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.JLabel;

import coffeeblocks.CoffeeMaker;


public class Frame1 {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame1 window = new Frame1();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Frame1() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		frame.setTitle("CoffeeMaker Launcher");
		
		CoffeeMaker main = new CoffeeMaker();
		
		JFileChooser opener = new JFileChooser();
		
		JButton btnNewButton = new JButton("Start Spill");
		btnNewButton.setForeground(new Color(255, 0, 51));
		btnNewButton.setFont(new Font("Vladimir Script", Font.PLAIN, 18));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				
				int returnVal = opener.showOpenDialog(frame);
				if(returnVal==JFileChooser.APPROVE_OPTION){
					System.out.println(opener.getSelectedFile().getAbsolutePath());
					main.lhcStart(opener.getSelectedFile().getAbsolutePath());
				}
				
				frame.setVisible(true);
			}
		});
		
//		JOptionPane.showMessageDialog(null, "Hello");
		
		JButton btnKnapp = new JButton("Test System");
		btnKnapp.setBounds(268, 209, 89, 23);
		btnKnapp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				
				boolean success = true;
				try{
					CoffeeMaker.testSystem();
				}catch(IllegalStateException x){
					JOptionPane.showMessageDialog(null, "Det virker ikke som dette systemet er i stand til å kjøre spillet. Du kan forsøke å kjøre det, men ingen garanti er gitt for at det vil kjøre som det skal.");
					success = false;
				}
				if(success)
					JOptionPane.showMessageDialog(null, "Spillet burde kjøre, men trenger fortsatt en god prosessor. Lykke til!");
				
				frame.setVisible(true);
			}
		});
		frame.getContentPane().add(btnKnapp);
		
		btnNewButton.setBounds(39, 209, 89, 23);
		frame.getContentPane().add(btnNewButton);
		
		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/res/cup.png"))));
		label.setBounds(0, 0, 450, 300);
		frame.getContentPane().add(label);
		

	}
}
