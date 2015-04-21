import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;
import java.awt.Image;

import javax.swing.JLabel;

import java.util.*;


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
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Start Spill");
		btnNewButton.setForeground(new Color(255, 0, 51));
		btnNewButton.setBackground(Color.WHITE);
		btnNewButton.setFont(new Font("Vladimir Script", Font.PLAIN, 18));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Hello");
				
				
			}
		});
		
		JButton btnKnapp = new JButton("Test System");
		btnKnapp.setBounds(268, 209, 89, 23);
		frame.getContentPane().add(btnKnapp);
		
		
		btnNewButton.setBounds(39, 209, 89, 23);
		frame.getContentPane().add(btnNewButton);
		
		JLabel label = new JLabel("");
		Image bilder = new ImageIcon(this.getClass().getResource("/Coffee-Cup-icon.png")).getImage();
		label.setIcon(new ImageIcon(bilder));
		label.setBounds(0, 0, 434, 261);
		frame.getContentPane().add(label);
		

	}
}
