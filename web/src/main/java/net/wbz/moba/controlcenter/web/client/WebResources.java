package net.wbz.moba.controlcenter.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;

/**
 * @author Daniel Tuerk
 */
public interface WebResources extends ClientBundle {

    WebResources INSTANCE = GWT.create(WebResources.class);

    /**
     * TODO clean and remove {@link NotStrict}
     *
     * @return
     */
    @NotStrict
    @Source("style/ControlCenterApp.css")
    CssResource mainCss();

    /**
     * TODO clean and remove {@link NotStrict}
     *
     * @return
     */
    @NotStrict
    @Source("style/scenario.css")
    CssResource scenarioCss();

    /**
     * TODO clean and remove {@link NotStrict}
     *
     * @return
     */
    @NotStrict
    @Source("style/track.css")
    CssResource trackCss();

}
