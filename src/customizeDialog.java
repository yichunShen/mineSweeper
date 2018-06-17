import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
/** 
 * File: customizeDialog.java
 * <p>Mr. Anandarajan
 * <br/>ICS4U1
 * <br/>May 7, 2018
 * 
 * <p>Minesweeper Assignment
 * <br/>Description: The class which describes object customizeDialog used in mineSweeper.
 * It creates a dialog allow the user to enter information about customizing the game.
 * 
 * @author Benny Shen
 * @author Martin Xu
 */
public class customizeDialog implements ActionListener{
	private static JDialog theCustDialog;  										//create JDialog window
	private JButton customizeOkButton = new JButton ("OK");						//create OK button
	private JLabel lengthLabel = new JLabel("Length:");							//create length label
	private JLabel widthLabel = new JLabel("Width:");							//create width label
	private JLabel mineNumLabel = new JLabel("Number of Mines:");				//create number of mines label
	private JTextField lengthText = new JTextField();							//create text field for length
	private JTextField widthText = new JTextField();							//create text field for width
	private JTextField mineNumText = new JTextField();							//create text field for number of mines
    private boolean isClosed=false;												//use a boolean to record whether the user has close the dialog
    customizeDialog() { 
        JFrame dialogFrame= new JFrame();//create a JFrame for the dialog
        theCustDialog = new JDialog(dialogFrame, "Customize", true);//initialize the dialog 			
        theCustDialog.setLayout( new GridLayout(4,2) );  //set the layout to GridLayout
        //add all labels and text fields
        theCustDialog.add(lengthLabel);   
        theCustDialog.add(lengthText);   
        theCustDialog.add(widthLabel);   
        theCustDialog.add(widthText);   
        theCustDialog.add(mineNumLabel);   
        theCustDialog.add(mineNumText);   
        customizeOkButton.addActionListener (this);  //add ActionListener
        theCustDialog.add(new JLabel (""));  //add blank space to place the OK Button under text fields
        theCustDialog.add(customizeOkButton);//add ok Button
        theCustDialog.setSize(300,150);//set size
        theCustDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                isClosed=true;//record the close when window is closed
            }//end method
        });//end WindowListener
        setCustomizeVisible();//call helper method to set the dialog visible
    }//end constructor
    /**
     * The helper procedure type method set the dialog visible
     */
    private void setCustomizeVisible(){
		theCustDialog.setVisible(true);//set visibility of dialog true
    }//end method
    /**
     * The helper procedure type method set the dialog invisible
     */
    private void setCustomizeInvisible(){
		theCustDialog.setVisible(false);//set visibility of dialog false
    }//end method
    /**
     * The getter method returns the length user entered
     * @return int value for length
     */
    public int getLength(){
    	return Integer.parseInt(lengthText.getText());//return the text in length text field
    }//end method
    /**
     * The getter method returns the width user entered
     * @return int value for width
     */
    public int getWidth(){
    	return Integer.parseInt(widthText.getText());//return the text in width text field
    }//end method
    /**
     * The getter method returns the number of mines user entered
     * @return int value for the number of mines
     */
    public int getMineNum(){
    	return Integer.parseInt(mineNumText.getText());//return the text in number of mines text field
    }//end method
    /**
     * The return type method check if the window is closed
     * @return boolean which indicates whether the dialog window is closed
     */
    public boolean checkInput() {
    	return isClosed;//return the boolean variable
    }//end method
    public void actionPerformed(ActionEvent event) {
		try{
			if(Integer.parseInt(lengthText.getText())<=0||Integer.parseInt(widthText.getText())<=0||Integer.parseInt(mineNumText.getText())<=0){
				JOptionPane.showMessageDialog(null, "Length/width/number of bombs must be positive.","Wrong Input",JOptionPane.ERROR_MESSAGE);//prompt the user when entering invalid numbers
			}else if((Integer.parseInt(lengthText.getText())*Integer.parseInt(widthText.getText())-9)<Integer.parseInt(mineNumText.getText())){
				JOptionPane.showMessageDialog(null, "Number of bombs over limit.","Wrong Input",JOptionPane.ERROR_MESSAGE);//prompt the user when the number of bombs is bigger than limit of a map
			}else{
				if(Integer.parseInt(lengthText.getText())>31||Integer.parseInt(widthText.getText())>31){
					JOptionPane.showMessageDialog(null, "The map is too big. You might not be able to reach all units.","Watch Out!",JOptionPane.WARNING_MESSAGE);//warn the user when map is too big
				}//end if
				setCustomizeInvisible();//set the window invisible
			}//end if
		}catch(Exception wrongInput){
			JOptionPane.showMessageDialog(null, "Length/width/number of bombs are entered incorrectly.","Wrong Input",JOptionPane.ERROR_MESSAGE);//prompt the user to enter whole numbers
		}//end try catch
	}//end method
}//end class




