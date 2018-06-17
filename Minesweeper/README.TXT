Author:	  Benny Shen, Martin Xu
Teacher:  Mr. Anandarajan
Course:	  ICS4U1
File:	  README.TXT
Date:	  May 7, 2018

Minesweeper Assignment

----------------
Game Information
----------------

-Instructions

Player win the game by:
flag all units which contains mine or reveal all units which are not mines

Player lose the game by:
Reveal a unit which contains mine

Scores are ranked by comparing the time used to win. Shorter times are ranked higher.

Note that all adjacent units of the first click will not contain mine.

-Install and Play

1. Move all java files in "classes" folder to src folder of a project
2. Move all other files to the project folder directly.
3. Compile and run mineSweeper.java in Java SE Runtime Environment 8.

-------
Classes
-------

-mineSweeper
Location: 	Minesweeper\classes\mineSweeper.java
Type: 		public
Purpose:	main program which allows the user to play the game, Minesweeper

All variables and helper methods(exclude methods of interfaces implemented) are private, not visible and accessible outside of the class. Explanations of the components are listed in scope of the project plan and should be read with changes made in progress log. The functions, type of each method has been explained in javadoc and comments in the class.

Difficulty levels of the game is stored as an int value difficultyLevel in the class.
	0 - Beginner
	1 - Intermediate
	2 - Expert
	3 - Customize
It is used as index to get names, side lengths and number of mines from difficultyNames, difficultySide, difficultyMineNum, respectively while intializing the games of presetted difficulty levels.

A unit is set and read for whether been revealed by its background color.
Background:
lightgray		- not revealed
white			- revealed

The states of a unit in a game is compressed by the return type method compress to a single line of String value. The process and its inverse are used during saving and adding units. The compressed information is composed of a click code, an icon code and information it contains, separated by intervals of one space character.
Click Code:
	0 - the unit is not revealed
	1 - the unit is revealed
Icon Code:
	0 - the unit does not has condition
	1 - the unit is flagged
	2 - the unit is questioned


-customzieDialog
Location:	Minesweeper\classes\customizeDialog.java
Type:		public
Purpose:	an object used in mineSweeper which creates dialog for user to enter setting for customization of the game

All variables and helper methods(exclude methods of interfaces implemented) are private, not visible and accessible outside of the class. It majorly serves the purpose of reading length, width and the number of mines user enters and repeated prompt the user to reenter until all answers are valid positive integers or the user closes the window.

The program perform no action whenever the user close the dialog window.


-pair
Location:	Minesweeper\classes\pair.java
Type:		public
Purpose:	an object used in mineSweeper which stores the coordinates of unit in map

The object pair holds two private integer values a and b which corresponds to x-coordinate(index of row) and y-coordinate(index of column) when used to store the coordinates of a unit in map.

Each variable has getter and setter method. A compareTo method is created to be used in priority queue in the procedure type method, revealUnit() in mineSweeper.


-----------
Other Files
-----------

Face Button Images

-aClick
Location:	Minesweeper\aClick.png
Size:		30x30 pixels
Purpose:	visual material used to indicate a click on one of the unit in the map.

The image of a round yellow face with mouse shaped "o", displayed on the Face button temporarily when a unit is clicked.

-InGame
Location:	Minesweeper\InGame.png
Size:		30x30 pixels
Purpose:	visual material used to indicate the game is on-going

The image of a round yellow face with mouse curved downward, displayed on the Face button while the game is on-going.

-loseFace
Location:	Minesweeper\loseFace.png
Size:		30x30 pixels
Purpose:	visual material used to indicate the game is lost

The image of a round yellow face with mouse curved upward, eyes shaped "x", displayed on the Face button while the game is lost.

-winFace
Location:	Minesweeper\winFace.png
Size:		30x30 pixels
Purpose:	visual material used to indicate the game is won

The image of a round yellow face with mouse curved downward, a sun glasses, displayed on the Face button while the game is won.


Unit Condition Images

-aFlag
Location:	Minesweeper\aFlag.png
Size:		15x15 pixels
Purpose:	visual material used to indicate a unit is flagged

The image of a red flag with black border, displayed on a unit when it has been flagged.

-aMine
Location:	Minesweeper\aMine.png
Size:		15x15 pixels
Purpose:	visual material used when a mine has been revealed

The image of round black metal mine with eight thorn, displayed when a mine is revealed or displayed together with all other mines when the game is lost.

-aQuestion
Location:	Minesweeper\aQuestion.png
Size:		15x15 pixels
Purpose:	visual material used when a unit is questioned

The image of a question mark in Times New Roman, displayed on a unit when it has been questioned.


Saves
*All saves of the game are text files in format of:
"date and time the game was saved
 number of millisecond time used
 number of mines left
 intial number of mines
 length of the map
 width of the map
 integer representation of difficulty level
 name of the icon image of the Face Button
 compressed information of status of all units each per line"

-previousGame
Location:	Minesweeper\previousGame

The temporary save of the previous game. It records every ending status of the game whenever the application window has been closed. The user is always questioned if he/she needs to load previous game or start a new game when the program is run.

-save+"n"
Location:	Minesweeper\"save"+"n"

n is positive integer numbers from 1 to 20 inclusive which indicates the index of the save and a part of the name of the save file. The saves are manually accessible in save menu and can be loaded in load menu.

Rankings
*All ranking files are five-line text files. Each line contains one long value which indicates the number of milliseconds in increasing order.

-rankings+"n"
Location:	Minesweeper\"rankings"+"n"

n is positive integer numbers from 1 to 3 inclusive which indicates the integer representation of the preset difficutly levels: beginner, intermediate and expert. The long values are converted in to format of hours:minutes:seconds and displayed in Top Scores menu. The ranking information will be automatically updated after each win.