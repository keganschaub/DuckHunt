package view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import model.DuckHuntModel;

/**
 * This was modified from the WhackAMole section to involve more intricate
 * animation.
 * 
 * RunDuckHunt simply creates a JFrame with the ClickPanelView, which performs
 * most of the GUI magic, and organizes most of the GUI components. This class
 * contains a timer that consults the model to formulaically change the position
 * of the duck as opposed to the random position of WhackAMole.
 * 
 * 
 * @author Dylan Clavell
 * @author Rick Mercer
 * 
 */
public class RunDuckHunt extends JFrame {

	private static final long serialVersionUID = 1L;
	private ClickPanelView drawingPanel;
	private Timer timer;
	public final static int delayInMillis = 100;
	private DuckHuntModel model;
	private Container cp;

	public static void main(String[] args) {
		RunDuckHunt window = new RunDuckHunt();
		window.setVisible(true);
	}

	public RunDuckHunt() {
		this.setTitle("Duck Hunt");
		this.setSize(510, 452);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		int largestPixel = Math
				.min(this.getSize().height, this.getSize().width) - 100;
		drawingPanel = new ClickPanelView();
		drawingPanel.setSize(this.getSize().width, this.getSize().height);
		drawingPanel.setLocation(0, 0);
		this.add(drawingPanel);

		model = new DuckHuntModel(largestPixel);

		// TODO 01: Talk about Java's Observer/Observable
		// TODO 02: Connect the observer and the model
		model.addObserver(drawingPanel);

		// TODO 04: Talk about java.swing.Timer
		timer = new Timer(delayInMillis, new MoveListener());
		timer.start();
	}

	// TODO 05: Add MoveListener in section, updating the model on timer tick
	// TODO 19: ... then update the animations in the timer.
	private class MoveListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			drawingPanel.updateAnimations();
			model.changeDuckLocation();
		}

	}
}
