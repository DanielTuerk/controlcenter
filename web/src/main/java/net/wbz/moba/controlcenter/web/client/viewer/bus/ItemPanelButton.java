package net.wbz.moba.controlcenter.web.client.viewer.bus;

import org.gwtbootstrap3.client.ui.Button;

import com.google.gwt.user.client.Timer;

public class ItemPanelButton extends Button {
	boolean isWorking = false;

	/**
	 * schedules a timer to set color for changed value of buttons and delay for
	 * 2 seconds and sets original color back
	 */
	public void setColor() {
		if (isWorking) {
			return;
		}
		isWorking = true;
		final String originalColor = this.getElement().getStyle()
				.getBackgroundColor();
		this.getElement().getStyle()
				.setBackgroundColor("LightSteelBlue ");
		Timer timer = new Timer() {
			@Override
			public void run() {
				ItemPanelButton.this.getElement().getStyle()
						.setBackgroundColor(originalColor);
				isWorking = false;
			}

		};
		timer.schedule(2000);

	}

}
