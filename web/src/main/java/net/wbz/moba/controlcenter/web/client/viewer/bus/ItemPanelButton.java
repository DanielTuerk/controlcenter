package net.wbz.moba.controlcenter.web.client.viewer.bus;

import org.gwtbootstrap3.client.ui.Button;

import com.google.gwt.user.client.Timer;
/**
 * Button  which represents a single bit for a bus address.
 * 
 * Toggle the state of the bit and show different background color on change.
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 *
 */
public class ItemPanelButton extends Button {
	 private boolean running = false;

	/**
	 * Schedules a timer to set color on changing state of button and delay for
	 * 2 seconds to switch back to original color.
	 */
	public synchronized void setColor() {
		if (running) {
			return;
		}
		running = true;
		final String originalColor = this.getElement().getStyle()
				.getBackgroundColor();
		this.getElement().getStyle()
				.setBackgroundColor("LightSteelBlue ");
		Timer timer = new Timer() {
			@Override
			public void run() {
				ItemPanelButton.this.getElement().getStyle()
						.setBackgroundColor(originalColor);
				running = false;
			}

		};
		timer.schedule(2000);

	}

}
