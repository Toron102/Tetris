package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import mino.Mino;

public class GamePanel extends JPanel implements Runnable{

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	final int FPS = 60;
	Thread gameThread;
	PlayManager pm;
	public static Sound music = new Sound();
	public static Sound se = new Sound();
	
	public GamePanel() {
		
		//Panel settings
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setBackground(Color.black);
		this.setLayout(null);
		this.addKeyListener(new KeyHandler(this));
		this.setFocusable(true);
		
		pm = new PlayManager();
	}
	
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
		
		music.play(0, true);
		music.loop();
	}

	@Override
	public void run() {

		//Game loop
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while(gameThread != null) {
			
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;
			
			if(delta >= 1) {
				update();
				repaint();
				delta--;
			}
		}
		
	}
	
	private void update() {
		if(KeyHandler.pausePressed == false && pm.gameOver == false) {
			pm.update();
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		pm.draw(g2);
	}
	
	public void restart() {
		PlayManager.level = 1;
		PlayManager.score = 0;
		PlayManager.lines = 0;
		PlayManager.staticBlocks.clear();
		PlayManager.dropInterval = 60;
		KeyHandler.pausePressed = false;
		pm.gameOver = false;
		pm.currentMino = null;
		pm.nextMino = null;
		pm.currentMino = pm.pickMino();
		pm.currentMino.setXY(pm.MINO_START_X, pm.MINO_START_Y);
		pm.nextMino = pm.pickMino();
		pm.nextMino.setXY(pm.NEXT_MINO_X, pm.NEXT_MINO_Y);
		music.stop();
		music.play(0, true);
	}
}
