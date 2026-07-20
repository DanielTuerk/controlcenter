package io.github.danieltuerk.controlcenter.shared.scenario;

/**
 * @author Daniel Tuerk
 */
public record RouteListItem(long id,
                            String name,
                            boolean oneway,
                            String start,
                            String end,
                            String trackStatus) {

}
