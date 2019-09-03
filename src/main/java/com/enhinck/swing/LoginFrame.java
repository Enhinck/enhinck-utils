package com.enhinck.swing;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

/**
 *
 * @author HEB
 *
 */
public class LoginFrame extends JFrame {
	private static final long serialVersionUID = 4380988512910634728L;
	private JPanel mainpanel;       
	private JLabel jlblsno;			
	private JTextField jtxtsno;		
	private JTextField jpwdIP;	
	private JLabel jlbltips;		
	private JButton jbtnCancel;		
	private JButton jbtnLogin;		
	private JLabel jlblpwd;			
	private JLabel jlblpwd2;
	private JTextField jpwdtxt2;	
	private Image backgroundImage;	
	public LoginFrame() {
		// backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("login_select.jpg")).getImage();
		initGUI();
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(330, 280);
		setLocation(550,200);
	}
	private void initGUI() {
		try {
			{
			//	this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("loginico.png")).getImage());
				getContentPane().setLayout(null);
				this.setTitle("Pojo\u7ED3\u5408\u6570\u636E\u5E93\u8F6C\u6362\u6570\u636E\u5B57\u5178");
				{
					mainpanel = new JPanel(){
						/**
						 * 
						 */
						private static final long serialVersionUID = 8250596394522699662L;

						@Override
						protected void paintComponent(Graphics g) {

							super.paintComponent(g);
							 if (backgroundImage != null) {
						            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
						        }
						}
					};
					getContentPane().add(mainpanel, "Center");
					mainpanel.setLayout(null);
					mainpanel.setBounds(0, 0, 330, 253);
					{
						jlblsno = new JLabel();
						mainpanel.add(jlblsno);
						jlblsno.setText("pojo Path:");
						jlblsno.setBounds(41, 66, 60, 17);
					}
					{
						jtxtsno = new JTextField();
						mainpanel.add(jtxtsno);
						jtxtsno.setText("D:\\java\\");
						jtxtsno.setBounds(100, 63, 138, 24);
					}
					
					{
						jlblpwd = new JLabel();
						mainpanel.add(jlblpwd);
						jlblpwd.setText("DB IP:");
						jlblpwd.setBounds(41, 110, 46, 17);
					}
				
					{
						jpwdIP = new JTextField();
						jpwdIP.setText("jdbc:mysql://192.168.9.52:4460/ioc");
						mainpanel.add(jpwdIP);
						jpwdIP.setBounds(100, 107, 180, 24);
					}
					
					{
						jlblpwd2 = new JLabel();
						mainpanel.add(jlblpwd2);
						jlblpwd2.setText("root PWD:");
						jlblpwd2.setBounds(41, 150, 60, 17);
					}
				
					{
						jpwdtxt2 = new JTextField();
						jpwdtxt2.setText("Greentown@123");
						mainpanel.add(jpwdtxt2);
						jpwdtxt2.setBounds(100, 150, 136, 24);
					}
					
					
					{
						jbtnLogin = new JButton();
						mainpanel.add(jbtnLogin);
						jbtnLogin.setText("TEST DB");
						jbtnLogin.setBounds(60, 190, 100, 24);
					}
					{
						jbtnCancel = new JButton();
						mainpanel.add(jbtnCancel);
						jbtnCancel.setText("CREATE");
						jbtnCancel.setBounds(154, 190,100, 24);
					}
					{
						jlbltips = new JLabel();
						mainpanel.add(jlbltips);
						jlbltips.setText("\u6570\u636E\u5B57\u5178\u751F\u6210\u5668V1.0");
						jlbltips.setBounds(100, 21, 150, 17);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		addmyaction();
	}
	private void addmyaction() {
		jbtnLogin.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent evt) {
                login(evt);
            }
        });
        jbtnCancel.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent evt) {
                cancel(evt);
            }
        });
		
	}
	private void login(ActionEvent evt) {
	
		
		String url = jpwdIP.getText().trim();
		
		String pwd = new String(jpwdtxt2.getText()).trim();
		
		try{
			final Database jdbcNb = new Database("com.mysql.jdbc.Driver",
					url, "root", pwd);
		
			Connection con = jdbcNb.getConnection();
			JDBCUtil.releaseConnection(con, null, null);
			JOptionPane.showMessageDialog(this, "\u8FDE\u63A5\u6210\u529F");
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "\u8FDE\u63A5\u5931\u8D25"+e.getMessage());
		}
		
	}
	private void cancel(ActionEvent evt) {
		
		String url = jpwdIP.getText().trim();
		
		String tableSchema = StringUtils.substringAfterLast(url, "/");
		System.out.println(tableSchema);
		String pwd = new String(jpwdtxt2.getText()).trim();
		String path = jtxtsno.getText().trim();
		try{
			final Database jdbcNb = new Database("com.mysql.jdbc.Driver",
					url, "root", pwd);
			//ReadPojo2DataDictionary.create(jdbcNb, path, tableSchema);
			
			//ReadDB2DataDictionary.create(jdbcNb, path, tableSchema);
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "\u521B\u5EFA\u5931\u8D25"+e.getMessage());
		}
		
	}
	public static void main(String[] args) {
		new LoginFrame();
	}
}