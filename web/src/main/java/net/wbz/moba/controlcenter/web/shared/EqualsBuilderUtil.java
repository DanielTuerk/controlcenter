package net.wbz.moba.controlcenter.web.shared;

import java.util.Objects;

/**
 * @author Daniel Tuerk
 */
public class EqualsBuilderUtil {
    private boolean isEquals = true;

    public EqualsBuilderUtil append(Object lhs, Object rhs) {
        if (!this.isEquals) {
            return this;
        } else {
            this.isEquals = Objects.equals(lhs, rhs);
            return this;
        }
    }
}
