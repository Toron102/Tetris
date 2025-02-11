package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;

import mino.Block;
import mino.Mino;
import mino.Mino_Bar;
import mino.Mino_L1;
import mino.Mino_L2;
import mino.Mino_Square;
import mino.Mino_T;
import mino.Mino_Z1;
import mino.Mino_Z2;

public class PlayManager {
	
	//Main play area
	final int WIDTH = 360;
	final int HEIGHT = 600;
	public static int left_x;
	public static int right_x;
	public static int top_y;
	public static int bottom_y;
	
	//Mino
	Mino currentMino;
	final int MINO_START_X;
	final int MINO_START_Y;
	Mino nextMino;
	final int NEXT_MINO_X;
	final int NEXT_MINO_Y;
	public static ArrayList<Block> staticBlocks = new ArrayList<>();
	
	//Others
	public static int dropInterval = 60;
	boolean gameOver;
	public static boolean canRestart;
	
	//Effects
	boolean effectCounterOn;
	int effectCounter;
	ArrayList<Integer> effectY = new ArrayList<>();
	
	//Score
	static int level = 1;
	static int lines;
	static int score;
	
	public PlayManager() {
		
		//Main play area frame
		left_x = (GamePanel.WIDTH/2) - (WIDTH/2); //460
		right_x = left_x + WIDTH;
		top_y = 50;
		bottom_y = top_y + HEIGHT;
		
		MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
		MINO_START_Y = top_y + Block.SIZE;
		
		NEXT_MINO_X = right_x + 175;
		NEXT_MINO_Y = top_y + 500;
		
		//Set starting mino
		currentMino = pickMino();
		currentMino.setXY(MINO_START_X, MINO_START_Y);
		
		//Set next mino
		nextMino = pickMino();
		nextMino.setXY(NEXT_MINO_X, NEXT_MINO_Y);
	}
	
	private Mino pickMino() {
		
		Mino mino = null;
		int i  = new Random().nextInt(7);
		
		switch(i) {
		case 0: mino = new Mino_L1(); break;
		case 1: mino = new Mino_L2(); break;
		case 2: mino = new Mino_T(); break;
		case 3: mino = new Mino_Square(); break;
		case 4: mino = new Mino_Bar(); break;
		case 5: mino = new Mino_Z1(); break;
		case 6: mino = new Mino_Z2(); break;
		}
		return mino;
	}
	
	public void update() {
		
		//Check if current mino is active
		if(currentMino.active == false) {
			
			//If the mino is not active, put it into the staticBlocks array
			for(int i = 0; i < currentMino.b.length; i++) {
				staticBlocks.add(currentMino.b[i]);
			}
			
			//Check if game is over
			if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
				//This means currentMino's x and y immediately collided
				//so it's x and y are the same with the nextMino's
				gameOver = true;
				GamePanel.music.stop();
				GamePanel.se.play(2, false);
			}
			
			currentMino.deactivating = false;
			
			//Replace current mino with next mino
			currentMino = nextMino;
			currentMino.setXY(MINO_START_X, MINO_START_Y);
			nextMino = pickMino();
			nextMino.setXY(NEXT_MINO_X, NEXT_MINO_Y);
			
			//when mino becomes inactive, check if line(s) can be deleted
			checkDelete();
		}
		else {
			currentMino.update();
		}
	}
	
	private void checkDelete() {
		
		int x = left_x;
		int y = top_y;
		int blockCount = 0;
		int lineCount = 0;
		
		while(x < right_x && y < bottom_y) {
			
			for(int i = 0; i < staticBlocks.size(); i++) {
				if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
					//Increase blockCount if there is a static block
					blockCount++;
				}
			}
			
			x += Block.SIZE;
			
			if(x == right_x) {
				
				//If there are 12 blocks in the row, we can delete them
				if(blockCount == 12) {
					
					effectCounterOn = true;
					effectY.add(y);
					
					for(int i = staticBlocks.size()-1; i > -1; i--) {
						//Remove all blocks in the current line
						if(staticBlocks.get(i).y == y) {
							staticBlocks.remove(i);
						}
					}
					
					lineCount++;
					lines++;
					
					//Drop speed
					//If score hits certain level, increase drop speed
					//1 is the fastest
					if(lines % 10 == 0 && dropInterval > 1) {
						
						level++;
						if(dropInterval > 10) {
							dropInterval -= 10;
						}
					}
					
					//A line has been deleted, so we slide all blocks that were above this line
					for(int i = 0; i < staticBlocks.size(); i++) {
						//If a block is above current y, move it down by one Block.SIZE
						if(staticBlocks.get(i).y < y) {
							staticBlocks.get(i).y += Block.SIZE;
						}
					}
				}
				
				blockCount = 0;
				x = left_x;
				y += Block.SIZE;
			}
		}
		
		//Add score
		if(lineCount > 0) {
			GamePanel.se.play(1, false);
			int singleLineScore = 10 * level;
			score += singleLineScore * lineCount;
		}
	}
	
	public void draw(Graphics2D g2) {
		
		//Draw play area frame
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(4f));
		g2.drawRect(left_x-4, top_y-4, WIDTH+8, HEIGHT+8);
		
		//Draw next mino frame
		int x = right_x + 100;
		int y = bottom_y - 200;
		g2.drawRect(x, y, 200, 200);
		g2.setFont(new Font("Arial", Font.PLAIN, 30));
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.drawString("NEXT", x+60, y+60);
		
		//Draw score frame
		g2.drawRect(x, top_y, 250, 300);
		x += 40;
		y = top_y + 90;
		g2.drawString("LEVEL: " + level, x, y); y += 70;
		g2.drawString("LINES: " + lines, x, y); y += 70;
		g2.drawString("SCORE: " + score, x, y);
		
		//Draw current mino
		if(currentMino != null) {
			currentMino.draw(g2);
		}
		
		//Draw next mino
		if(nextMino != null) {
			nextMino.draw(g2);
		}
		
		//Draw effect
		if(effectCounterOn) {
			effectCounter++;
			
			g2.setColor(Color.red);
			for(int i = 0; i < effectY.size(); i++) {
				g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
			}
			
			if(effectCounter == 10) {
				effectCounterOn = false;
				effectCounter = 0;
				effectY.clear();
			}
		}
		
		//Draw staticBlocks array
		for(int i = 0; i < staticBlocks.size(); i++) {
			staticBlocks.get(i).draw(g2);
		}
		
		//Draw pause or game over text
		g2.setColor(Color.yellow);
		g2.setFont(g2.getFont().deriveFont(50f));
		
		if(gameOver) {
			x = left_x + 25;
			y = top_y + 320;
			g2.drawString("GAME OVER", x, y);
			canRestart = true;
		}
		
		else if(KeyHandler.pausePressed) {
			x = left_x + 70;
			y = top_y + 320;
			g2.drawString("PAUSED", x, y);
		}
		
		//Draw game title
		x = 35;
		y = top_y + 320;
		g2.setColor(Color.white);
		g2.setFont(new Font("Times New Roman", Font.ITALIC, 60));
		g2.drawString("Simple Tetris", x+20, y);
		
	}
	
}
