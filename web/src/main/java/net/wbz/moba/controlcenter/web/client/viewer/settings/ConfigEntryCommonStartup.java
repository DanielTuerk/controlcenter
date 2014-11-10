package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.constants.FormType;

/**
 * @author Daniel Tuerk
 */
public class ConfigEntryCommonStartup extends AbstractCommonConfigEntry {
    @Override
    public String getName() {
        return "Startup";
    }

    @Override
    protected Panel getContentPanel() {
        Form form = new Form();
        form.setType(FormType.HORIZONTAL);

        CheckBox cbxShowWelcomePage = new CheckBox("show Welcome Page");
        form.add(cbxShowWelcomePage);
        return form;
    }

    @Override
    protected void save() {

    }
}
