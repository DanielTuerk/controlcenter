package net.wbz.moba.controlcenter.web.resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a class as REST resource to get registered under the {@link javax.ws.rs.Path}.
 *
 * @author Daniel Tuerk
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestResource {

}
