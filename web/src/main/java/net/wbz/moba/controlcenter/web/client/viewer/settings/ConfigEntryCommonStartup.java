package net.wbz.moba.controlcenter.web.client.viewer.settings;

import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.constants.FormType;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;

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
//        <b:Form type="HORIZONTAL">
//        <b:Fieldset>
//        <b:ControlGroup>
//        <b:ControlLabel for="input01">TextBox</b:ControlLabel>
//        <b:Controls>
//        <!-- If you add id attribute to element,You should use b:id attribute. -->
//        <b:TextBox alternateSize="XLARGE" b:id="input01"></b:TextBox>
//        <b:HelpBlock>In addition to freeform text, any HTML5 text-based input appears like so.</b:HelpBlock>
//        </b:Controls>
//        </b:ControlGroup>
//        </b:Fieldset>
//        </b:Form>

        Form form = new Form();
        form.setType(FormType.HORIZONTAL);
//        Fieldset fieldset = new Fieldset();
//        ControlLabel controlLabel = new ControlLabel("show Welcome Page");
//        controlLabel.setTitle();

//        fieldset.add(controlLabel);
        CheckBox cbxShowWelcomePage = new CheckBox("show Welcome Page");
//        fieldset.add(cbxShowWelcomePage);

        form.add(cbxShowWelcomePage);
        return form;
    }

    @Override
    protected void save() {

    }
}
