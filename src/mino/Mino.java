package mino;

import java.awt.Color;
import java.awt.Graphics2D;

import main.KeyHandler;
import main.PlayManager;

public class Mino {

	public Block b[] = new Block[4];
	public Block tempB[] = new Block[4];
	int autoDropCounter = 0;
	public int direction = 1; //There will be 4 directions
	boolean leftCollision, rightCollsision, bottomCollision;
	public boolean active = true;
	
	
	public void create(Color c) {
		b[0] = new Block(c);
		b[1] = new Block(c);
		b[2] = new Block(c);
		b[3] = new Block(c);
		tempB[0] = new Block(c);
		tempB[1] = new Block(c);
		tempB[2] = new Block(c);
		tempB[3] = new Block(c);
	}
	
	public void setXY(int x, int y) {
		
	}
	
	public void updateXY(int direction) {
		
		checkRotationCollision();
		
		if(leftCollision == false && rightCollsision == false && bottomCollision == false) {
			
			this.direction = direction;
			b[0].x = tempB[0].x;
			b[0].y = tempB[0].y;
			b[1].x = tempB[1].x;
			b[1].y = tempB[1].y;
			b[2].x = tempB[2].x;
			b[2].y = tempB[2].y;
			b[3].x = tempB[3].x;
			b[3].y = tempB[3].y;
		}
	}
	
	public void getDirection1() {}
	
	public void getDirection2() {}
	
	public void getDirection3() {}
	
	public void getDirection4() {}
	
	public void checkMovementCollision() {
		
		leftCollision = false;
		rightCollsision = false;
		bottomCollision = false;
		
		//Check frame collision
		//Left wall
		for(int i = 0; i < b.length; i++) {
			if(b[i].x == PlayManager.left_x) {
				leftCollision = true;
			}
		}
		
		//Right wall
		for(int i = 0; i < b.length; i++) {
			if(b[i].x + Block.SIZE == PlayManager.right_x) {
				rightCollsision = true;
			}
		}
		
		//Bottom wall
		for(int i = 0; i < b.length; i++) {
			if(b[i].y + Block.SIZE == PlayManager.bottom_y) {
				bottomCollision = true;
			}
		}
	}
	
	public void checkRotationCollision() {
		
		leftCollision = false;
		rightCollsision = false;
		bottomCollision = false;
		
		//Check frame collision
		//Left wall
		for(int i = 0; i < b.length; i++) {
			if(tempB[i].x < PlayManager.left_x) {
				leftCollision = true;
			}
		}
		
		//Right wall
		for(int i = 0; i < b.length; i++) {
			if(tempB[i].x + Block.SIZE > PlayManager.right_x) {
				rightCollsision = true;
			}
		}
		
		//Bottom wall
		for(int i = 0; i < b.length; i++) {
			if(tempB[i].y + Block.SIZE > PlayManager.bottom_y) {
				bottomCollision = true;
			}
		}
	}
	
	public void update() {
		
		//Player moves mino
		if(KeyHandler.upPressed) {
			
			switch(direction) {
			case 1: getDirection2(); break;
			case 2: getDirection3(); break;
			case 3: getDirection4(); break;
			case 4: getDirection1(); break;
			}
			
			KeyHandler.upPressed = false;
		}
		
		checkMovementCollision();
		
		if(KeyHandler.leftPressed) {
			//If mino's left side is not hitting leftCollision, it can do left
			if(leftCollision == false) {
				b[0].x -= Block.SIZE;
				b[1].x -= Block.SIZE;
				b[2].x -= Block.SIZE;
				b[3].x -= Block.SIZE;
			}
			KeyHandler.leftPressed = false;
		}
		
		if(KeyHandler.downPressed) {
			//If mino's bottom is not hitting bottomCollision, it can go down
			if(bottomCollision == false) {
				b[0].y += Block.SIZE;
				b[1].y += Block.SIZE;
				b[2].y += Block.SIZE;
				b[3].y += Block.SIZE;
				
				//Reset auto drop counter
				autoDropCounter = 0;
			}
			KeyHandler.downPressed = false;
		}
		
		if(KeyHandler.rightPressed) {
			//If mino's collision is not hitting rightCollision, it can go right
			if(rightCollsision == false) {
				b[0].x += Block.SIZE;
				b[1].x += Block.SIZE;
				b[2].x += Block.SIZE;
				b[3].x += Block.SIZE;
			}
			KeyHandler.rightPressed = false;
		}
		
		if(bottomCollision) {
			active = false;
		}
		else {
			autoDropCounter++; //Counter increases every frame
			if(autoDropCounter == PlayManager.dropInterval) {
				
				//Mino goes down
				b[0].y += Block.SIZE;
				b[1].y += Block.SIZE;
				b[2].y += Block.SIZE;
				b[3].y += Block.SIZE;
				autoDropCounter = 0;
			}
		}
	}
	
	public void draw(Graphics2D g2) {
		
		int margin = 2;
		
		g2.setColor(b[0].c);
		g2.fillRect(b[0].x+margin, b[0].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
		g2.fillRect(b[1].x+margin, b[1].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
		g2.fillRect(b[2].x+margin, b[2].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
		g2.fillRect(b[3].x+margin, b[3].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));
	}
}
