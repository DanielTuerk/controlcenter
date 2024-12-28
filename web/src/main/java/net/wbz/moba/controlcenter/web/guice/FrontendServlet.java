package net.wbz.moba.controlcenter.web.guice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO could be the entry point, or is there a better way?
 * @author Daniel Tuerk
 */
public class FrontendServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        throw new IllegalStateException("unable to service request");
    }

}
