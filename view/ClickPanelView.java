package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.DuckHuntModel;

/**
 * 
 * This was modified from the WhackAMole section by Dylan to involve more
 * intricate animation, as well as a game that isn't terrible.
 * 
 * ClickPanelView is a JPanel that responds to mouse clicks that represent
 * shooting a bird. When a bird is shot, it falls and the score goes up. When a
 * player misses three times in a row, a dog appears and laughs at you.
 * 
 * 
 * @author Dylan Clavell
 * @author Rick Mercer
 * 
 */
public class ClickPanelView extends JPanel implements Observer {

	private BufferedImage sheet, background;
	private JLabel scoreLabel;
	private Point upperLeft;
	private Cursor cursor;
	private DuckHuntModel model;
	private int misses;

	public ClickPanelView() {

		loadImages();

		scoreLabel = new JLabel("Score: 0");
		scoreLabel.setFont(new Font(Font.SERIF, Font.BOLD, 20));
		scoreLabel.setSize(80, 30);
		scoreLabel.setLocation(this.getSize().width / 2 - 80, 3);

		this.add(scoreLabel);

		// TODO 21: We use MouseListeners...
		this.addMouseListener(new ClickListener());
	}

	// TODO 06: Talk about paintComponent
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gr = (Graphics2D) g;

		if (model == null) {
			return;
		}

		// TODO 07: Now images!
		// TODO 08: Talk about drawImage, its arguments, and animating
		// (briefly).
		gr.drawImage(background, 0, 0, null);

		// What do I need to draw? (check state)
		if (starting) {

			// Draw the starting dog
			gr.drawImage(getDogSprite(), dogXValue, DOG_Y_VALUE, null);

		} else if (model.isFalling()) {

			// Draw the falling duck
			gr.drawImage(getDuckSprite(), duckFallX, duckFallY, null);

		} else if (misses < 3) {

			// Draw the normal Duck
			gr.drawImage(getDuckSprite(), upperLeft.x, upperLeft.y, null);

		} else {

			// Draw the laughing dog (missed > 3 times)
			gr.drawImage(getDogSprite(), this.getSize().width / 2
					- DOG_LAUGHING_OFFSET, DOG_SPOT + dogJiggle, null);
		}

		// TODO 13: Try shaking the frame!
		// TODO 14: updateAnimations outside of paintComponent
	}

	@Override
	public void update(Observable o, Object arg) {
		model = (DuckHuntModel) o;
		upperLeft = model.getLocation();
		repaint();
	}

	public void updateAnimations() {
		// What do I need to draw? (check state)
		if (starting) {

			// TODO 15: Move the starting dog
			moveStartingDog();

		} else if (model.isFalling()) {

			// TODO 16: Move the falling duck
			moveFallingDuck();

		} else if (misses < 3) {

			// TODO 17: Do we need to move the model's duck?

		} else {

			// TODO 18: Move the jiggling dog (missed > 3 times)
			moveJigglingDog();

		}
	}

	// TODO 20: User interactions!
	private class ClickListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			// Nothing
		}

		// TODO 22: This is called when we press down on the mouse (left)
		public void mousePressed(MouseEvent e) {

			if (!model.isFalling()) {
				if (isHit(e)) {
					// The user hit the duck, and it's now falling.
					model.userHit();
					misses = 0;
					model.setFalling();
					duckFallX = upperLeft.x;
					duckFallY = upperLeft.y;
				} else {
					// The user missed, and the dog is counting down...
					model.userMissed();
					misses++;
				}
				scoreLabel.setText("Score: " + model.getScore());
			}
			// TODO: If you put this in mouseClicked, it's way too hard... Why?

		}

		public void mouseEntered(MouseEvent arg0) {
			// Nothing
		}

		public void mouseExited(MouseEvent arg0) {
			// Just to see how it works...
			System.out.println("(" + arg0.getX() + ", " + arg0.getY() + ")");
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// Nothing
		}

		private boolean isHit(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			return x > upperLeft.x && x < (upperLeft.x + DUCK_WIDTH)
					&& y > upperLeft.y && y < (upperLeft.y + DUCK_HEIGHT);
		}
	}

	/*
	 * *************************************** *
	 * Complicated things beyond this point... *
	 * *************************************** *
	 */

	/*
	 * Magic Numbers
	 */
	public static final int FIRST_DUCK_X = 72, FIRST_DUCK_Y = 56,
			SPRITE_DISTANCE = 34;
	public static final int DUCK_WIDTH = 32, DUCK_HEIGHT = 32;
	public static final int DOG_LAUGHING_HEIGHT = 50, DOG_LAUGHING_WIDTH = 35;
	public static final int DOG_LAUGHING_X = 350, DOG_LAUGHING_Y = 0,
			DOG_Y_VALUE = 300;
	public static final int DOG_SPOT = 270, DOG_LAUGHING_OFFSET = 15,
			DOG_WALKING_OFFSET = 3;
	public static final int JIGGLE_MAX = 1, JIGGLE_MIN = -1, JIGGLE_TIME = 15;
	public static final int DOG_WALKING_WIDTH = 60, DOG_WALKING_HEIGHT = 52;
	public static final int DOG_FRAME_ORDER[] = { 2, 3, 1, 0 };

	/*
	 * Animation Counters
	 */

	private int duckSpriteNum;
	private int spriteNum = 0;
	private int dogJiggle = 0, jiggleCount = 0, dogFrameCounter = 0,
			dogXValue = 0, dogTickCounter = 0;
	private boolean jiggleUp = true, starting = true;
	private int duckFallX, duckFallY;

	/*
	 * Private Helpers
	 */

	// TODO 09: Talk about how to load images
	private void loadImages() {
		try {
			background = ImageIO.read(new File("images" + File.separator
					+ "DuckHuntBackground.png"));
		} catch (IOException e) {
			System.out.println("Could not find 'DuckHuntBackground.PNG'");
		}
		try {
			sheet = ImageIO.read(new File("images" + File.separator
					+ "DuckHuntOriginalSheet.gif"));

		} catch (IOException e) {
			System.out.println("Could not find 'DuckHuntOriginalSheet.gif'");
		}
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image img = ImageIO.read(new File("images" + File.separator
					+ "CrosshairCursor.png"));
			cursor = toolkit.createCustomCursor(img, new Point(16, 16),
					"Crosshair");
			this.setCursor(cursor);
		} catch (IOException e) {
			System.out.println("Could not find 'CrosshairCursor.png'");
		}
	}

	// TODO 10: Talk about getSubimage and swapping sprites (spritesheets)
	public BufferedImage getDuckSprite() {
		if (model.isFalling()) {
			return sheet.getSubimage(FIRST_DUCK_X, FIRST_DUCK_Y, DUCK_WIDTH,
					DUCK_HEIGHT);
		}
		// Change the sprite number
		spriteNum = (spriteNum + 1) % 2;
		return sheet.getSubimage(FIRST_DUCK_X + spriteNum * SPRITE_DISTANCE,
				FIRST_DUCK_Y, DUCK_WIDTH, DUCK_HEIGHT);
	}

	public BufferedImage getDogSprite() {
		// You don't have to code it this way, this is just how I did it quickly
		if (starting) {
			if (dogXValue > this.getSize().width / 2) {
				if (dogTickCounter > 10) {
					return sheet.getSubimage(DOG_WALKING_OFFSET
							+ DOG_WALKING_WIDTH * 5, 0, DOG_WALKING_WIDTH - 10,
							DOG_WALKING_HEIGHT);
				}
				return sheet.getSubimage(DOG_WALKING_OFFSET + DOG_WALKING_WIDTH
						* 4, 0, DOG_WALKING_WIDTH, DOG_WALKING_HEIGHT);
			}
			return sheet.getSubimage(DOG_WALKING_OFFSET + DOG_WALKING_WIDTH
					* DOG_FRAME_ORDER[dogFrameCounter], 0, DOG_WALKING_WIDTH,
					DOG_WALKING_HEIGHT);
		}
		return sheet.getSubimage(DOG_LAUGHING_X, DOG_LAUGHING_Y,
				DOG_LAUGHING_WIDTH, DOG_LAUGHING_HEIGHT);
	}

	// TODO 11: Talk about translating sprites systematically
	private void moveFallingDuck() {
		// Duck falls straight down
		duckFallY += DuckHuntModel.MOVEMENT_PIXELS;
		if (duckFallY > this.getSize().height) {
			model.setFlying();
			model.resetDuck();
		}
	}

	private void moveJigglingDog() {
		if (jiggleUp) {
			dogJiggle++;
			if (dogJiggle > JIGGLE_MAX) {
				jiggleUp = false;
			}
		} else {
			dogJiggle--;
			if (dogJiggle < JIGGLE_MIN) {
				jiggleUp = true;
			}
		}
		if (jiggleCount++ > JIGGLE_TIME) {
			// Stop jiggling
			jiggleCount = 0;
			misses = 0;
		}
	}

	// TODO 12: Talk about doing both at once
	private void moveStartingDog() {
		if (dogXValue <= this.getSize().width / 2) {
			// If we haven't hit the halfway mark, keep walking...
			dogXValue += DuckHuntModel.MOVEMENT_PIXELS;
			dogFrameCounter = (dogFrameCounter + 1) % DOG_FRAME_ORDER.length;
		} else {
			// Run away, dog!
			if (dogTickCounter > 20) {
				starting = false;
			}
			dogTickCounter++;
		}
	}
}