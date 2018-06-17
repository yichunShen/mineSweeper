import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.Border;
/** 
 * File: mineSweeper.java
 * <p>Mr. Anandarajan
 * <br/>ICS4U1
 * <br/>May 7, 2018
 * 
 * <p>Minesweeper Assignment
 * <br/>Description: The major class which includes all Graphic User Interface components,
 * implementation for the initializations and functions of the game.
 * 
 * @author Benny Shen
 * @author Martin Xu
 */
public class mineSweeper extends JFrame implements ActionListener {
	private JPanel scoreboard = new JPanel();										//create JPanel for the scoreboard section
	private JPanel game = new JPanel();												//create JPanel for the game section
	private GridBagConstraints constr = new GridBagConstraints();					//create GridBagConstriants for GridBagLayout templates
	private JMenuBar menuBar = new JMenuBar();										//create JMenuBar for menu section
	private JMenu difficulty, topScores, save, load;								//declare JMenus for functions of menu
	private JRadioButtonMenuItem[] selectionItem;									//declare an array of JRadioButtonMenuItem for selection of difficulty level 
	final String[] difficultyName = { "Beginner", "Intermediate", "Expert" };		//create an array of Strings for name of each difficulty level
	final int[] difficultySide = { 8, 16, 24 };										//create an int array for the side length of map of each difficulty level
	final int[] difficultyMineNum = { 10, 40, 99 };									//create an int array for the number of mines of each difficulty level
	private File[] shortestTime = new File[3];										//create an array of File instance for records of rankings of each difficulty level
	private JMenuItem[][] rankInfo = new JMenuItem[3][5];							//create a 2D array of JMenuItems for the ranking information of each difficulty level
	final int maxSaveNum = 20;														//create constant for the maximum number of saves
	private JMenuItem[] saveLabels;													//declare array of JMenuItems for labels of saves in save menu
	private JMenuItem[] loadLabels;													//declare array of JMenuItems for labels of saves in load menu
	private File[] saves = new File[maxSaveNum];									//create array of file instances for the saves
	private File beforeGame = new File("previousGame");								//create a file instance for the record of the previous game
	private BufferedReader fileReader;												//declare BufferedReader for reading files
	private JLabel timeLabel;														//declare JLabel for the timer
	private Timer timer;															//declare the timer
	private long lastRecordTime;													//declare a long variable to record the number of milliseconds last recorded by the program
	private JButton theFace;														//declare a JButton for the Face button in scoreboard section
	private JLabel mineNumLabel;													//declare a JLabel for the number of mines left in scoreboard section
	private boolean firstClick;														//declare a boolean to record whether the next click will be the first click
	private int difficultyLevel;													//declare an int variable to record the difficulty level
	private int width;																//declare an int variable to record width of the map (horizontal dimension)
	private int length;																//declare an int variable to record length of the mao (vertical dimension)
	private long timeUsed;															//declare a long variable to record the number of millisecond since the first click
	private int initialMineNum;														//declare an int variable for recording the initial number of mines
	private int mineLeft;															//declare an int variable for the number of mines left assuming every flag is placed correctly on a mine.
	private JButton[][] map;														//declare a 2D array of JButtons for the map in the game section
	final int[] adjacentX = { 1, -1, 0, 0, 1, 1, -1, -1 };							//create array of constant of changes of x-coordinates of adjacent units
	final int[] adjacentY = { 0, 0, 1, -1, 1, -1, 1, -1 };							//create array of constant of changes of y-coordinates of adjacent units
	final Color[] textColors = { Color.blue, new Color(0, 120, 0), Color.red, new Color(192, 0, 255), Color.magenta, Color.cyan, Color.black, Color.gray };//create an array of Colors for each number text
	final Border unClicked = BorderFactory.createRaisedBevelBorder();				//create constant Border for setting of any button not clicked
	final Border clicked = BorderFactory.createLoweredBevelBorder();				//create constant Border for setting of any button clicked
	private MouseListener unitClick = new MouseListener() {							//create MouseListener for the units in map
		public void mousePressed(MouseEvent mouseEvent) {//whenever a unit is clicked
			int modifiers = mouseEvent.getModifiers();	
			if (!timer.isRunning()) {//record time and start timer if it's not started
				lastRecordTime = System.currentTimeMillis();
				timer.start();
			}//end if
			pair sourceUnit = getUnit(mouseEvent.getSource());				//record the location of source unit in a pair
			if ((modifiers & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK && map[sourceUnit.getA()][sourceUnit.getB()].getIcon() == null) {//when a unit without icon is left clicked
				theFace.setIcon(new ImageIcon("aClick.png"));//set the face button to clicked face image
				map[sourceUnit.getA()][sourceUnit.getB()].setBorder(clicked);//set border to clicked
			}//end if
			if ((modifiers & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK && map[sourceUnit.getA()][sourceUnit.getB()].getBackground().equals(Color.lightGray)) {//when a unit unrevealed is right clicked
				if (map[sourceUnit.getA()][sourceUnit.getB()].getIcon() == null) {//when the unit does not has condition
					if (mineLeft != 0) {//if there is still flag left
						map[sourceUnit.getA()][sourceUnit.getB()].setIcon(new ImageIcon("aFlag.png"));//flag the unit
						mineLeft--;//decrease number of flags left
						mineNumLabel.setText("<html>Mines Left: <br>" + mineLeft + "</html>");//display the new number
					}//end if
				} else if (map[sourceUnit.getA()][sourceUnit.getB()].getIcon().toString().equals("aFlag.png")) {//when the unit is flagged
					map[sourceUnit.getA()][sourceUnit.getB()].setIcon(new ImageIcon("aQuestion.png"));//question the unit
				} else {//when the unit is questioned
					map[sourceUnit.getA()][sourceUnit.getB()].setIcon(null);//clear the conditions
					mineLeft++;//record that the mine is not marked
					mineNumLabel.setText("<html>Mines Left: <br>" + mineLeft + "</html>");//display the new number
				}//end if
			}//end if
		}//end method
		public void mouseReleased(MouseEvent mouseEvent) {
			pair sourceUnit = getUnit(mouseEvent.getSource());//record the location of source unit in a pair
			if (firstClick) {//if this is the first click
				generateMine(sourceUnit.getA() + 1, sourceUnit.getB() + 1);//generate mines to avoid first click on a mine
				firstClick = false;//record first click
				if (checkWin()) {
					try {
						gameEnd(true);
					} catch (IOException e) {
						e.printStackTrace();
					}//perform game end if checked the game has been won
				}//end if
			}//end if
			if (SwingUtilities.isLeftMouseButton(mouseEvent)) {//if the left click is released
				theFace.setIcon(new ImageIcon("InGame.png"));//change the face button back to smile
				if (map[sourceUnit.getA()][sourceUnit.getB()].getIcon() == null) {//if the unit has no condition
					if (map[sourceUnit.getA()][sourceUnit.getB()].getBackground().equals(Color.lightGray)) {//if the unit is unrevealed
						try {
							revealUnit(sourceUnit.getA() + 1, sourceUnit.getB() + 1);//perform reveal action on this unit
						} catch (IOException e) {
							e.printStackTrace();
						}//end try catch
					}//end if
				}//end if
			}//end if
			if (checkWin()) {
				try {
					gameEnd(true);
				} catch (IOException e) {
					e.printStackTrace();
				}//perform game end if checked the game has been won
			}//end if
		}//end method

		public void mouseClicked(MouseEvent mouseEvent) {
			pair sourceUnit = getUnit(mouseEvent.getSource());//record the location of the source unit in a pair
			if (mouseEvent.getClickCount() == 2//when a unit is double clicked
					&& map[sourceUnit.getA()][sourceUnit.getB()].getBackground().equals(Color.white)//and it is revealed (which also means it is not a mine in game)
					&& !unitInfo[sourceUnit.getA()][sourceUnit.getB()].equals("0")) {//and it contains information of a number, not a blank
				if (countFlag(sourceUnit.getA(), sourceUnit.getB(), Integer.parseInt(unitInfo[sourceUnit.getA()][sourceUnit.getB()]))) {//if the number of flags in adjacent unit matches its information
					for (int i = 0; i < adjacentX.length; i++) {//check each of the adjacent
						int nx = sourceUnit.getA() + adjacentX[i];//calculate new x-coordinate
						int ny = sourceUnit.getB() + adjacentY[i];//calculate new y-coordinate
						if (nx >= 0 && ny >= 0 && nx < length && ny < width) {//when the coordinates are within the map
							if (map[nx][ny].getIcon() == null) {//when the new unit does not has icon
								try {
									revealUnit(nx + 1, ny + 1);//perform reveal action on the new unit
								} catch (IOException e) {
									e.printStackTrace();
								}//end try catch
							}//end if
						}//end if
					}//end for
				}//end if
				if (checkWin()) {
					try {
						gameEnd(true);
					} catch (IOException e) {
						e.printStackTrace();
					}//perform game end if checked the game has been won
				}//end if
			}//end if
		}//end method

		public void mouseEntered(MouseEvent mouseEvent) {
		}//end method

		public void mouseExited(MouseEvent mouseEvent) {
		}//end method
	};//end MouseListener
	private int[][] mineLocations;													//declare 2D array of int for the location of mines by "1"
	private int[][] bit;															//declare 2D array of int for Binary Index Tree while generate the informations
	private String[][] unitInfo;													//declare 2D array of String for information contained by the units
	/**
	 * The constructor of the mineSweeper
	 * @throws IOException
	 */
	public mineSweeper() throws IOException {
		initializeMenu();//initialize the Menu section by calling intializeMenu() procedure type method
		initializeScoreboard();//initialize the scoreboard section by calling intializingScoreboard() procedure type method
		initializeGame(0, difficultySide[0], difficultySide[0], difficultyMineNum[0], true);//initialize the game by difficulty beginner
		setTitle("Minesweeper");//set the title of the window to game title
		addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if(theFace.getIcon().toString().equals("InGame.png")){//if the game is not over yet
					try {
						saveThis(beforeGame,-1);//save the game now for next start
					} catch (IOException e1) {
						e1.printStackTrace();
					}//end try catch
				}//end if
				System.exit(0);//terminate the program
			}//end method
		});//end WindowStateListener
		setJMenuBar(menuBar);//add the menu section
		menuBar.setVisible(true);//set the menu visible
		setLayout(new GridBagLayout());//set the layout of window to GridBagLayout
		//add the scoreboard section
		constr.fill = GridBagConstraints.VERTICAL;
		constr.gridx = 0;
		constr.gridy = 0;
		add(scoreboard, constr);
		//add the game section
		constr.weightx = 1;													
		constr.gridy = 1;
		add(game, constr);
		setResizable(false);//avoid the user to resize the window
		setVisible(true);//set the window visible
		fileReader  = new BufferedReader(new FileReader("previousGame"));
		if(fileReader.readLine()!=null){//if there is a previous game
			Object[] options = {"Continue previous level",
	        "Starts new game"};//create an array of objects for choices of loading previous game
			int choice = JOptionPane.showOptionDialog(null,
					"Do you want to continue previous game or start a new one?",
					"Hi.",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[1]);//display the dialog and allow user to choose whether load previous game or start new game
			if(choice==0){
				loadFile(beforeGame);//load previous game when user choose to
			}//end if
		}//end if
	}//end constructor
	/**
	 * This procedure type method initializes the game section.
	 * @param theDiffLevel the difficulty level of the game
	 * @param iLength the length of the map
	 * @param iWidth the width of the map
	 * @param iMineNum the number of mines in the map
	 * @param needReset a boolean which indicates whether the game needs to be reset
	 */
	private void initializeGame(int theDiffLevel, int iLength, int iWidth, int iMineNum, boolean needReset) {
		firstClick = true;//set the next click as first click
		difficultyLevel = theDiffLevel;//set the difficulty level
		length = iLength;//set the length
		width = iWidth;//set the width
		game.setLayout(new GridLayout(length, width));//set GridLayout with length and width
		game.setBackground(Color.lightGray);//set the background of the map to light gray
		game.setBorder(clicked);//set the border of the map to lowered bevel border
		setSize(Math.max(300, width * 20 + 140), Math.max(290, length * 20 + 130));//set the size of the map by formula related to the length and width
		timer.stop();//stop the timer if it is running
		if (needReset) {//when the game is restarted/started instead of loaded
			initialMineNum = iMineNum;//set the initial mine number
			timeLabel.setText("<html>Time Used: <br>00:00:00" + "</html>");//clear display of the timer
			timeUsed = 0;//set time used to 0
			unitInfo = new String[length][width];//initialize the 2D array of information of units
			mineLeft = initialMineNum;//set all mines marked as not flagged
			mineNumLabel.setText("<html>Mines Left: <br>" + Integer.toString(mineLeft) + "</html>");//display cleared number of mines
			map = new JButton[length][width];//initialize the map of units
			for (int i = 0; i < length; i++) {
				for (int j = 0; j < width; j++) {
					addUnit(i, j, 0, 0, "");//add the units to the map with status not revealed, none conditions
				}//end for
			}//end for
		}//end if
		game.revalidate();//revalidate all components of the game section
	}//end method
	/**
	 * The procedure type method adds a unit with information from the parameter.
	 * @param x x-coordinate of the unit in the map
	 * @param y y-coordinate of the unit in the map
	 * @param clickCode code which represents the status of unit whether been revealed
	 * @param iconCode code which represents the status of unit has no condition/flagged/questioned
	 * @param text information contained by the unit
	 */
	private void addUnit(int x, int y, int clickCode, int iconCode, String text) {
		map[x][y] = new JButton();//initialize 2D array of JButton for the map
		if (clickCode != 0) {//when the unit is revealed
			map[x][y].setBackground(Color.white);//set background to white
			if (!(text.equals("0")||text.equals("M"))) {//if the unit contains a number
				map[x][y].setText(text);//set the text to a number
				map[x][y].setForeground(textColors[Integer.parseInt(text) - 1]);//set the text to corresponding color
			}//end if
			map[x][y].setBorder(clicked);//set the border to revealed border
		} else {//when the unit is not revealed
			map[x][y].setBackground(Color.lightGray);//set the background to light gray
			map[x][y].setBorder(unClicked);//set border to not revealed border
		}//end if
		if (iconCode == 1) {//if the iconCode is 1
			map[x][y].setIcon(new ImageIcon("aFlag.png"));//set the unit to be flagged by changing its icon
		} else if (iconCode == 2) {//if the iconCode is 2
			map[x][y].setIcon(new ImageIcon("aQuestion.png"));//set the unit to be questioned by changing its icon
		}//end if
		map[x][y].setPreferredSize(new Dimension(20, 20));//set the appropriate size of the unit
		map[x][y].addMouseListener(unitClick);//add the MouseListener
		game.add(map[x][y]);//add the unit to the map
	}//end method
	/**
	 * The procedure type method initializes the scoreboard section.
	 * @throws IOException
	 */
	private void initializeScoreboard() throws IOException {
		scoreboard.setLayout(new GridBagLayout());//set layout to GridBagLayout
		scoreboard.setBorder(clicked);//set border to lowered bevel border
		scoreboard.setBackground(Color.lightGray);//set the background to light gray
		timeLabel = new JLabel();//initialize JLabel for display of timer
		timer = new Timer(1000, new ActionListener() {//initialize the timer
			public void actionPerformed(ActionEvent e) {
				long thisTime = System.currentTimeMillis();//record the current time
				timeUsed += thisTime - lastRecordTime;//add the new time interval to sum of time used
				timeLabel.setText("<html>Time Used: <br>" + formatTime(timeUsed) + "</html>");//display the calculated time used
				lastRecordTime = thisTime;//record this action of recording
			}//end method
		});
		//add the JLabel of timer display
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.gridx = 0;
		constr.gridy = 0;
		scoreboard.add(timeLabel, constr);
		theFace = new JButton("", new ImageIcon("InGame.png"));//create the JButton for the Face button
		theFace.setBackground(Color.lightGray);//set background to not clicked color: light gray
		theFace.setBorder(unClicked);//set border of the the Face button to not clicked status
		theFace.addActionListener(new ActionListener() {//add ActionListener for the Face button
			public void actionPerformed(ActionEvent e) {//when the Face button is clicked
				theFace.setBorder(clicked);//set the border of the Face button to be clicked
				theFace.setIcon(new ImageIcon("InGame.png"));//set the Face back to InGame status
				//reset the game with exact same setting of current game
				game.removeAll();
				initializeGame(difficultyLevel, length, width, initialMineNum, true);
				theFace.setBorder(unClicked);//set the border to unclicked
			}//end method
		});//end ActionListener
		//add the Face button to the scoreboard section
		constr.gridx = 1;
		scoreboard.add(theFace, constr);
		mineNumLabel = new JLabel("<html>Mines Left: <br>" + mineLeft + "</html>");//create JLabel for the display of number of mines left
		//add the display of number of mines left to the scoreboard section
		constr.gridx = 2;
		scoreboard.add(mineNumLabel, constr);
	}//end method
	/**
	 * The procedure type method initializes the menu section.
	 * @throws IOException
	 */
	private void initializeMenu() throws IOException {
		difficulty = new JMenu("Difficulty");//create the menu for difficulty selection
		difficulty.setMnemonic(KeyEvent.VK_D);//set shortcut Alt+D
		menuBar.add(difficulty);//add difficulty menu the menu bar
		selectionItem = new JRadioButtonMenuItem[difficultyName.length + 1];//initialize the array of JRadioButtonMenuItem for the choices
		ButtonGroup difficulties = new ButtonGroup();//create a ButtonGroup since only one level can be chosen
		for (int i = 0; i < difficultyName.length; i++) {//for the basic three levels
			selectionItem[i] = new JRadioButtonMenuItem(difficultyName[i]);//initialize the JRadioButtonMenuItem by the corresponding name
			if (i == 0) {
				selectionItem[i].setSelected(true);//set the game to beginner level first
			}//end if
			difficulties.add(selectionItem[i]);//add the choice to ButtonGroup
			difficulty.add(selectionItem[i]);//add the item to the menu
			selectionItem[i].addActionListener(this);//add the ActionListener
			// actionperform:reset the game to main frame to difficulty side and #bombs
		}//end for
		//add the customize option
		selectionItem[difficultyName.length] = new JRadioButtonMenuItem("Customize");
		selectionItem[difficultyName.length].addActionListener(this);
		difficulties.add(selectionItem[difficultyName.length]);
		difficulty.add(selectionItem[difficultyName.length]);
		topScores = new JMenu("Top Scores");//create top scores menu
		topScores.setMnemonic(KeyEvent.VK_T);//set shortcut Alt+T
		menuBar.add(topScores);//add the top scores menu to menu bar
		for (int i = 0; i < 3; i++) {
			shortestTime[i] = new File("rankings" + (i + 1));//create File instance
			fileReader = new BufferedReader(new FileReader(shortestTime[i]));//set BufferedReader to read the rankings record 
			topScores.add(new JMenuItem(difficultyName[i]));//add the name of difficulty level
			for (int j = 0; j < 5; j++) {
				try {
					rankInfo[i][j] = new JMenuItem((j + 1) + " " + formatTime(Long.parseLong(fileReader.readLine())));//add time if there is time recording
				} catch (Exception e) {
					rankInfo[i][j] = new JMenuItem((j + 1) + " " + "N/A");//add N/A if there isn't a score recorded
				}
				topScores.add(rankInfo[i][j]);//add the item to the menu
			}//end try catch
			topScores.addSeparator();//add separator for ending of each difficulty level
		}//end for
		save = new JMenu("Save");//create save menu
		save.setMnemonic(KeyEvent.VK_S);//set shortcut Alt+S
		load = new JMenu("Load");//create load menu
		load.setMnemonic(KeyEvent.VK_L);//set shortcut Alt+L
		//initialize JMenuItem array for labels in each menu
		saveLabels = new JMenuItem[maxSaveNum];
		loadLabels = new JMenuItem[maxSaveNum];
		for (int i = 0; i < maxSaveNum; i++) {
			saves[i] = new File("save" + (i + 1));//initialize file instance for each save
			fileReader = new BufferedReader(new FileReader(saves[i]));//set BufferedReader to read the saves
			String time = fileReader.readLine();//read the first line which is the time the save was made
			saveLabels[i] = new JMenuItem((i+1)+". "+time);//add the label to menu items in both menus
			loadLabels[i] = new JMenuItem((i+1)+". "+time);
			if (time == null){//if the save is empty
				loadLabels[i].setText((i+1)+". Empty");//add the label of empty to menu items in both menus
				saveLabels[i].setText((i+1)+". Empty");
			}//end if
			saveLabels[i].addActionListener(this);//add ActionLister to both menu items
			loadLabels[i].addActionListener(this);
			save.add(saveLabels[i]);//add the menu items to corresponding menu
			load.add(loadLabels[i]);
		}//end for
		menuBar.add(save);//add both menus to the menu bar
		menuBar.add(load);
	}//end method
	/**
	 * The return type method returns a boolean to indicate if the game has win or not.
	 * @return a boolean which indicates whether the user has won the game
	 */
	private boolean checkWin() {
		boolean win = true;//create a boolean to mark if an invalid unit is found
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < width; j++) {
					if (!unitInfo[i][j].equals("M") && map[i][j].getBackground().equals(Color.lightGray)){//when a non-mine unit is not revealed
						win = false;//record found invalid unit
						break;
					}//end if
			}//end for
		}//end for
		if(win){
			return true;//return true if all unit fits winning requirement
		}//end if
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < width; j++) {
				try{
					if (unitInfo[i][j].equals("M")&&!map[i][j].getIcon().toString().equals("aFlag.png")){//when a non-mine unit is not revealed
						return false;
					}//end if
				}catch(Exception e){
					return false;			
				}//end try catch
			}//end for
		}//end for		
		return true;
	}//end method
	/**
	 * The procedure type method performs reveal action to a unit with coordinates in the parameter as described in project plan.
	 * @param x x-coordinate of the unit which is going to be revealed
	 * @param y y-coordinate of the unit which is going to be revealed
	 * @throws IOException
	 */
	private void revealUnit(int x, int y) throws IOException {
		Queue<pair> unv = new PriorityQueue<pair>();//create a Queue for Breadth-First Search algorithm
		if (unitInfo[x - 1][y - 1].equals("M")) {//when the unit revealed is a mine
			map[x - 1][y - 1].setBackground(Color.red);//set it red
			map[x - 1][y - 1].setBorder(clicked);//set it clicked
			map[x - 1][y - 1].setIcon(new ImageIcon("aMine.png"));//reveal the image of a mine
			gameEnd(false);//perform game end action for losing the game
		} else {//when the unit contains blank or number
			unv.add(new pair(x, y));//record its location as pair to be start point of BFS
			map[x - 1][y - 1].setBackground(Color.white);//set it white
			map[x - 1][y - 1].setBorder(clicked);//set it clicked
			while (!unv.isEmpty()) {//while there is a unit not judged
				pair p = unv.poll();//get the pair not searched in Queue
				int xpos = p.getA();//get x-coordinate
				int ypos = p.getB();//get y-coordinate
				if (unitInfo[xpos - 1][ypos - 1].equals("0")) {//when it is a blank unit
					for (int i = 0; i < adjacentX.length; i++) {//loop all adjacent units
						int nx = xpos + adjacentX[i];
						int ny = ypos + adjacentY[i];
						if (nx < 1 || ny < 1 || nx >= length + 1 || ny >= width + 1 || map[nx - 1][ny - 1].getBorder().equals(clicked) || map[nx - 1][ny - 1].getIcon() != null) {
							continue;
						} else if (!unitInfo[nx - 1][ny - 1].equals("M")) {//while the adjacent unit is not a mine
							unv.add(new pair(nx, ny));//add the unit to be judged
							//reveal the unit
							map[nx - 1][ny - 1].setBackground(Color.white);
							map[nx - 1][ny - 1].setBorder(clicked);
						}//end if
					}//end for
				}else {//if the unit contains a number
					map[xpos - 1][ypos - 1].setText(unitInfo[xpos - 1][ypos - 1]);//display the number
					map[xpos - 1][ypos - 1].setBackground(Color.white);//set unit white
					map[xpos - 1][ypos - 1].setBorder(clicked);//set unit clicked
					map[xpos - 1][ypos - 1].setForeground(textColors[Integer.parseInt(unitInfo[xpos - 1][ypos - 1]) - 1]);//set text to its corresponding color
				}//end if
			}//end while
		}//end if
	}//end method
	/**
	 * The return type method counts the number of flags within a unit which contains a number
	 * and check if the number of flags matches the number 
	 * @param x x-coordinate of the unit which contains a number
	 * @param y y-coordinate of the unit which contains a number
	 * @param flagNum the number which contains by the unit
	 * @return a boolean to indicate whether the numbers matches
	 */
	private boolean countFlag(int x, int y, int flagNum) {
		int count = 0;//create accumulator
		for (int i = 0; i < adjacentX.length; i++) {//loop through all adjacent units
			int nx = x + adjacentX[i];
			int ny = y + adjacentY[i];
			try {
				if (map[nx][ny].getIcon().toString().equals("aFlag.png")) {
					count++;//record if a flag is found
				}
			} catch (Exception e) {//do nothing when a unit with no condition is found
			}//end try catch
		}//end for
		if (count == flagNum) {
			return true;//return true when numbers match
		} else {
			return false;//return false when numbers does not match
		}//end if
	}//end method
	/**
	 * The return type method returns a pair which contains the coordinates of the source unit
	 * @param source the Object source from listener
	 * @return a pair includes coordinates of the source unit in the map
	 */
	private pair getUnit(Object source) {
		for (int i = 0; i < length; i++) {//search in all rows
			for (int j = 0; j < width; j++) {//search in all coloumns
				if (source.equals(map[i][j])) {
					return new pair(i, j);//return the pair with coordinates
				}//end if
			}//end for
		}//end for
		return null;//return null if the unit is not found
	}//end method
	/**
	 * The procedure type method generates mines.
	 * @param iniX x-coordinate of the first click
	 * @param iniY y-coordinate of the first click
	 */
	private void generateMine(int iniX, int iniY) {
		int mines = initialMineNum;//get the initial number of mines to count
		Random r = new Random();//create Random to generate the coordinate of the mine
		mineLocations = new int[length + 1][width + 1];//initialize the 2D array which records the location of mines
		bit = new int[length + 1][width + 1];//initialize the 2D array of binary index tree
		while (mines > 0) {//repeat generate the mines before count finish
			int xMine = r.nextInt(length) + 1;
			int yMine = r.nextInt(width) + 1;
			if (mineLocations[xMine][yMine] != 0 || (Math.abs(xMine - iniX) <= 1 && Math.abs(yMine - iniY) <= 1))
				continue;
			else {//when the generation was successful
				mineLocations[xMine][yMine] = 1;//record generated location
				bitUpdate(xMine, yMine, 1);//update the binary index tree
				mines--;//record successful generation
			}//end if
		}//end while
		//calculate the number of mines in adjacent for each unit
		for (int i = 1; i < length + 1; i++) {
			for (int j = 1; j < width + 1; j++) {
				if (mineLocations[i][j] == 0) {
					int x1 = (i - 1 >= 1) ? (i - 1) : 1;
					int y1 = (j - 1 >= 1) ? (j - 1) : 1;
					int x2 = (i + 1 <= length) ? (i + 1) : length;
					int y2 = (j + 1 <= width) ? (j + 1) : width;
					unitInfo[i - 1][j - 1] = Integer.toString(
							bitQuery(x2, y2) - bitQuery(x2, y1 - 1) - bitQuery(x1 - 1, y2) + bitQuery(x1 - 1, y1 - 1));
				} else if (mineLocations[i][j] == 1) {
					unitInfo[i - 1][j - 1] = "M";
				}//end if
			}//end for
		}//end for
	}//end method
	/**
	 * The procedure type method updates the binary index tree
	 * @param i x-coordinate of the update target
	 * @param j y-coordinate of the update target
	 * @param v the value added to the target
	 */
	private void bitUpdate(int i, int j, int v) {
		int i2 = i;
		while (i2 < length + 1) {
			int j2 = j;
			while (j2 < width + 1) {
				bit[i2][j2] += v;
				j2 += (j2 & (-j2));
			}//end while
			i2 += (i2 & (-i2));
		}//end while
	}//end method
	/**
	 * The return type method returns the sum of values in rectangular range from 0-i, 0-j.
	 * @param i x-end point of query range
	 * @param j y-end point of query range
	 * @return the sum of all values within the range
	 */
	private int bitQuery(int i, int j) {
		int ans = 0;
		int i2 = i;
		while (i2 > 0) {
			int j2 = j;
			while (j2 > 0) {
				ans += bit[i2][j2];
				j2 -= (j2 & (-j2));
			}//end while
			i2 -= (i2 & (-i2));
		}//end while
		return ans;//return the answer
	}//end method
	/**
	 * The procedure type method adds the result to top scores when a game is won.
	 * @throws IOException
	 */
	private void addResult() throws IOException {
		fileReader = new BufferedReader(new FileReader(shortestTime[difficultyLevel]));//create BufferedReader to read rankings
		String[] originalRanks = new String[5];//create a String array to record the original top scores
		String formatedThis = formatTime(timeUsed);//create a String to record the formated time now
		for (int i = 0; i < 5; i++) {
			originalRanks[i] = fileReader.readLine();//record original top scores
		}//end for
		PrintWriter fileWriter = new PrintWriter(new FileWriter(shortestTime[difficultyLevel]));//create PrintWriter to write new ranking information
		boolean inserted = false;//use a boolean to record whether the current score has been added to the list
		for (int i = 0; i < 5; i++) {
			if (inserted) {//if the score has been added, add the original scores shifted(oen index before)
				try {
					rankInfo[difficultyLevel][i].setText((i+1) + " " + formatTime(Long.parseLong(originalRanks[i - 1])));
				} catch (Exception e) {
					rankInfo[difficultyLevel][i].setText((i+1)+" N/A");
				}
				fileWriter.println(originalRanks[i-1]);
			} else {//if the score is not added yet
				try {
					if (timeUsed <= Long.parseLong(originalRanks[i])) {//if the current time used is better than original
						rankInfo[difficultyLevel][i].setText((i + 1) + " " + formatedThis);//add the current score
						fileWriter.println(timeUsed);
						inserted = true;//record the addition
					} else {//if the current time used is worse, add the original score
						rankInfo[difficultyLevel][i].setText((i+1)+" "+formatTime(Long.parseLong(originalRanks[i])));
						fileWriter.println(originalRanks[i]);
					}//end if
				} catch (Exception e) {//whenever a null is found in top scores, replace the null with current time used
					rankInfo[difficultyLevel][i].setText((i + 1) + " " + formatedThis);
					fileWriter.println(timeUsed);
					inserted = true;//record the addition
				}//end try catch
			}//end if
		}//end for
		fileWriter.close();//close the fileWriter
	}//end method
	/**
	 * The return type method returns the formated String value of long variable for the number of milliseconds.
	 * @param secondNum long which is the number of milliseconds
	 * @return String value in hours:minutes:seconds format
	 */
	private String formatTime(long secondNum) {
		secondNum /= 1000;//convert millisecond to seconds
		String[] timeInfo = new String[3];//store the converted number in a String array
		timeInfo[0] = Long.toString(secondNum / 3600);//calculate number of hours
		timeInfo[1] = Long.toString((secondNum % 3600) / 60);//calculate the number of minutes
		timeInfo[2] = Long.toString(secondNum % 60);//calculate the number of seconds
		String formatedTime = "";//create a String to return the result
		//for converted number, add to the result by format of no less than two digits
		for (int i = 0; i < 3; i++) {
			if (timeInfo[i].length() == 1) {
				formatedTime += "0";
			}//end if
			formatedTime += (timeInfo[i] + ":");
		}//end for
		return formatedTime.substring(0, formatedTime.length() - 1);//returnt the answer
	}//end method
	/**
	 * The procedure type method performs game end actions.
	 * @param win boolean which indicates whether the game is won or lost
	 * @throws IOException
	 */
	private void gameEnd(boolean win) throws IOException {
		timer.stop();//stop the timer
		if (!win) {//if lost
			loseAnimation();//perform lose animation
			theFace.setIcon(new ImageIcon("LoseFace.png"));//set the Face button to lose faces
			JOptionPane.showMessageDialog(null, "You just lost the game.", "Good Try!", JOptionPane.WARNING_MESSAGE);//display information about lost
		} else {//if won
			theFace.setIcon(new ImageIcon("WinFace.png"));//set the Face button to win face
			JOptionPane.showMessageDialog(null, "You Win!", "Congrats.", JOptionPane.WARNING_MESSAGE);//display information about won
			if (difficultyLevel < 3) {//when the game is not in customize level
				addResult();//add the result to top scores
			}//end if
		}//end if
		game.removeAll();//remove all components of game
	}//end method
	/**
	 * The procedure type method performs animation of losing the game: reveal all units which contain mines.
	 */
	private void loseAnimation() {
		for (int i = 0; i < length; i++) {//loop through all rows
			for (int j = 0; j < width; j++) {//loop through all columns
				//reveal if the unit contains a mine exclude the one exposed
				if (unitInfo[i][j].equals("M")&&map[i][j].getBackground().equals(Color.lightGray)) {
					map[i][j].setBackground(Color.white);
					map[i][j].setIcon(new ImageIcon("aMine.png"));
					map[i][j].setBorder(clicked);
				}//end if
			}//end for
		}//end for
	}//end method
	/**
	 * The return type method returns the compressed String value of status of a unit in game.
	 * Details of compression method are explained in README.TXT
	 * @param x x-coordinate of the unit
	 * @param y y-coordinate of the unit
	 * @return String which is one line compressed information about the unit in game
	 */
	private String compress(int x, int y) {
		String compressed = "";//create a String to store the information
		//record color information
		if (map[x][y].getBackground().equals(Color.white)) {
			compressed += (1 + " ");
		} else {
			compressed += (0 + " ");
		}//end if
		//record icon information
		try {
			if (map[x][y].getIcon().toString().equals("aFlag.png")) {
				compressed += (1 + " ");
			} else {
				compressed += (2 + " ");
			}//end if
		} catch (Exception e) {
			compressed += (0 + " ");
		}//end try catch
		compressed += (unitInfo[x][y] + "\n");//record the information the unit contains
		return compressed;//return the information
	}//end method
	/**
	 * The procedure type method saves the game into a File in parameter.
	 * @param aFile file to be saved
	 * @param saveIndex the index of saves if it is not a previous game
	 * @throws IOException
	 */
	private void saveThis(File aFile, int saveIndex) throws IOException {
		if(!theFace.getIcon().toString().equals("InGame.png")){//when the game is no continuing
			JOptionPane.showMessageDialog(null,
				    "The game is already over.",
				    "Save failed.",
				    JOptionPane.WARNING_MESSAGE);//display message of failure of saving for a game ended
			return;//do not perform any other operations
		}//end if
		timer.stop();//stop timer
		String now = LocalDateTime.now().toString();//get the String format of date and time when game saved
		if(saveIndex>-1){//when the game is not a previous game
			saveLabels[saveIndex].setText((saveIndex+1)+". "+now);//set the labels of the save in the menu
			loadLabels[saveIndex].setText((saveIndex+1)+". "+now);
		}//end if
		String formatedSave ="";//create a String to store the saved information
		//first line: time now
		formatedSave+=now+"\n";
		//second line: time used
		formatedSave+=Long.toString(timeUsed)+"\n";
		//third line: number of mines left
		formatedSave+=Integer.toString(mineLeft)+"\n";
		//Fourth line: initialMineNum
		formatedSave+=Integer.toString(initialMineNum)+"\n";
		//fifth line: length
		formatedSave+=Integer.toString(length)+"\n";
		//sixth line: width
		formatedSave+=Integer.toString(width)+"\n";
		//seventh line: difficulty Level
		formatedSave+=Integer.toString(difficultyLevel)+"\n";
		//eighth line: face icon
		formatedSave+=theFace.getIcon().toString()+"\n";
		for(int i =0;i<length;i++){
			for(int j =0;j<width;j++){
				formatedSave+=compress(i,j);//compress information about all units and record
			}//end for
		}//end for
		PrintWriter saveWriter = new PrintWriter(aFile);//create PrintWriter to write the file
		saveWriter.print(formatedSave);//write the file
		saveWriter.close();//close the PrintWriter
	}//end method
	/**
	 * The procedure type method loads the game from a File
	 * @param aFile file which contains status of a on-going game
	 * @throws IOException
	 */
	private void loadFile(File aFile) throws IOException {
		fileReader = new BufferedReader(new FileReader(aFile));//set BufferedReader to read the file
		fileReader.readLine();//read the first line of title and do nothing
		timeUsed = Long.parseLong(fileReader.readLine());//read and set time used
		timeLabel.setText("<html>Time Used: <br>" + formatTime(timeUsed) + "</html>");//set display of timer
		mineLeft = Integer.parseInt(fileReader.readLine());//read and set the number of mines left
		mineNumLabel.setText("<html>Mines Left: <br>" + mineLeft + "</html>");//set display of number of mines left
		initialMineNum = Integer.parseInt(fileReader.readLine());//read and set the initial number of mines
		length = Integer.parseInt(fileReader.readLine());//read and set length of the map
		width = Integer.parseInt(fileReader.readLine());//read and set width of the map
		difficultyLevel = Integer.parseInt(fileReader.readLine());//read and set the difficulty level
		theFace.setIcon(new ImageIcon(fileReader.readLine()));//read and set the Face button icon
		game.removeAll();//reset the board
		map = new JButton[length][width];//reset map
		unitInfo = new String[length][width];//reset unitInfo
		for (int i = 0; i < length * width; i++) {
			String[] aCompress = fileReader.readLine().split(" ");
			addUnit(i / width, i % width, Integer.parseInt(aCompress[0]), Integer.parseInt(aCompress[1]), aCompress[2]);//add unit with converted information
			unitInfo[i / width][i % width] = aCompress[2];//record the information contained by the unit
		}//end for
		initializeGame(difficultyLevel, length, width, initialMineNum, false);//initialize the game section with new information without reseting it
		firstClick = false;//no need of generate mine
	}//end method
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();//get the source of action and store as an object
		for (int i = 0; i < difficultyName.length; i++) {//search if the source is a preset difficulty selection
			if (source.equals(selectionItem[i])) {
				if (i != difficultyLevel) {//when the difficulty level has been changed
					game.removeAll();//reset the game section
					initializeGame(i, difficultySide[i], difficultySide[i], difficultyMineNum[i], true);//initialize the game with new difficulty level
					difficultyLevel = i;
				}//end if
				return;//action performed
			}//end if
		}//end for
		if (source.equals(selectionItem[selectionItem.length - 1])) {//check if the source is selecting customize level
			customizeDialog forCustomize = new customizeDialog();//create customize dialog
			if (!forCustomize.checkInput()) {//when user did not give up entering data
				game.removeAll();//reset the game
				initializeGame(3, forCustomize.getLength(), forCustomize.getWidth(), forCustomize.getMineNum(), true);
				return;//action performed
			}//end if
		}//end if
		for (int i = 0; i < maxSaveNum; i++) {//search for the source in the save and load menu
			try {
				if (source.equals(saveLabels[i])) {
					saveThis(saves[i],i);//perform save function if it is a save in menu
					return;//action performed
				}//end if
				if (source.equals(loadLabels[i])) {
					if (!loadLabels[i].getText().contains("Empty")) {//when loading a save which is not empty
						loadFile(saves[i]);//perform load function
					}//end if
					return;//action performed
				}//end if
			} catch (IOException e) {
				e.printStackTrace();
			}//end try catch
		}//end for
	}//end method
	public static void main(String[] args) throws IOException {
		mineSweeper theGame = new mineSweeper();//create the JFrame of the game
	}// end method
}//end class
