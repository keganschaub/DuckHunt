package model;

import java.awt.Point;
import java.util.Observable;
import java.util.Random;

/**
 * This was modified from the WhackAMole section to involve more intricate
 * animation.
 * 
 * DuckHuntModel is, intuitively, the model for Duck Hunt. It organizes the
 * formulaic movement of the duck, and the state of the duck.
 * 
 * 
 * @author Dylan Clavell
 * @author Rick Mercer
 */
public class DuckHuntModel extends Observable {

	public static final int X_START = 220, Y_START = 300;
	public static final int MOVEMENT_PIXELS = 8;
	public static final int PLANE_HEIGHT = 414, PLANE_WIDTH = 472;
	public static final int SPRITE_X = 0, SPRITE_Y = 0;
	public static final int ANGLE_RANGE = 178;
	private int x, y;
	private double xV, yV;
	private Random generator;

	private int largestPixel;
	private int score;
	private boolean falling;

	public DuckHuntModel(int lastPixel) {
		falling = false;
		score = 0;
		this.largestPixel = lastPixel;
		generator = new Random();
		resetDuck();
	}

	public void changeDuckLocation() {
		// Move the duck
		if (x + MOVEMENT_PIXELS * xV > PLANE_WIDTH
				|| x + MOVEMENT_PIXELS * xV < 0)
			xV = -xV;
		x += MOVEMENT_PIXELS * xV;

		if (y + MOVEMENT_PIXELS * yV > PLANE_HEIGHT
				|| y + MOVEMENT_PIXELS * yV < 0)
			yV = -yV;
		y += MOVEMENT_PIXELS * yV;

		// TODO 03: setChanged and notifyObservers
		super.setChanged();
		super.notifyObservers();
	}

	public Point getLocation() {
		return new Point(x, y);
	}

	// The message sent by the controller when the user missed the mole
	public void userMissed() {
		score--;
	}

	// The message sent by the controller when the user clicks on the mole
	public void userHit() {
		score++;
		resetDuck();
	}

	public void resetDuck() {
		// Velocities don't aim down
		double angle = -(generator.nextInt(ANGLE_RANGE) + 1) * (Math.PI / 180);
		yV = Math.sin(angle);
		xV = Math.cos(angle);
		// Duck starts in random position
		x = generator.nextInt(PLANE_WIDTH);
		y = Y_START;
	}

	// Return the current score
	public int getScore() {
		return score;
	}

	// Changes the duck's status to "falling"
	public void setFalling() {
		falling = true;
	}

	// Changes the duck's status to "flying"
	public void setFlying() {
		falling = false;
	}

	// Return if the duck has been shot and is falling
	public boolean isFalling() {
		return falling;
	}
}