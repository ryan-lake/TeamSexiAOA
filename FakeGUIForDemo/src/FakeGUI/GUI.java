package FakeGUI;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
//QWERTYUI

public class GUI {

	JPanel data = new JPanel();
	JPanel settings = new JPanel();
	

	//data UI objects
	Label degreeLabel = new Label();	
	JTextField degreeBox = new JTextField();
	Label powerLabel = new Label();	
	JTextField powerBox = new JTextField();

	Label dwellLabel = new Label();	
	JTextField dwellBox = new JTextField();
	Label cenFreqLabel = new Label();	
	JTextField cenFreqBox = new JTextField();
	
	Button updateButton = new Button("Update");
	
	Dimension boxDim = new Dimension(25,25);
	static GridLayout dialogLayout = new GridLayout(5,0);
	
	QDFModel model;

	public GUI (){
		try{
			model= new QDFModel();
		}catch(Exception e){
			e.printStackTrace();	
		}
		createGUI();

	}
	public JPanel getDataPanel(){
		return data;
	}
	public JPanel getSettingsPanel(){
		return settings;
	}
	private void createGUI(){
		//data
		degreeLabel.setText("Degree: ");
		degreeBox.setPreferredSize(boxDim);
		degreeBox.setText("");
		degreeBox.setEditable(false);
		
		powerLabel.setText("Power level (scaled): ");	
		powerBox.setPreferredSize(boxDim);
		powerBox.setText("");
		powerBox.setEditable(false);
		
		data.add(degreeLabel);
		data.add(degreeBox);	
		data.add(powerLabel);
		data.add(powerBox);	
		
		//settings
		dwellLabel.setText("Dwell Time: ");
		dwellBox.setPreferredSize(boxDim);
		dwellBox.setText("500");
		dwellBox.setEditable(true);
		
		cenFreqLabel.setText("Center Frequency : ");	
		cenFreqBox.setPreferredSize(boxDim);
		cenFreqBox.setText("18525000");
		cenFreqBox.setEditable(true);
		
		settings.add(dwellLabel);
		settings.add(dwellBox);	
		settings.add(cenFreqLabel);
		settings.add(cenFreqBox);
		
		updateButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.query(getFreq(), getDwell());			
			}		
		});
	
			
	}

	public Button getUpdateButton(){
		return updateButton;
	}
	
	public void killTimer(){
		model.killTimer();
	}

	public int getDwell(){
		int dwell;
		try {
			dwell=Integer.parseInt(this.dwellBox.getText());
		}catch (Exception e){
			dwell = 0;
			dwellBox.setText("0");
		}
		return dwell;
	}
	
	public int getFreq(){
		int freq;
		try {
			freq=Integer.parseInt(cenFreqBox.getText());
		}catch (Exception e){
			freq = 0;
			cenFreqBox.setText("0");
		}
		return freq;
	}
	
	public static void main(String[] args){
		final GUI gui = new GUI();
		//JOptionPane JOP= new JOptionPane(); 
		
		JDialog dialog=new JDialog();
		dialog.setTitle("Qualcomm PC based GUI");
		dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		dialog.setLayout(dialogLayout);
		dialog.setResizable(false);
		
		//dialog
		dialog.add(new Label("Data"));
		dialog.add(gui.getDataPanel());
		dialog.add(new Label("Settings"));
		dialog.add(gui.getSettingsPanel() );
		dialog.add(gui.getUpdateButton());
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		dialog.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent we) {
		        gui.killTimer();
		    }
		});
		
		dialog.setSize(400, 400);

		dialog.pack();
		
		dialog.setVisible(true);
	}//main
}//GUI
