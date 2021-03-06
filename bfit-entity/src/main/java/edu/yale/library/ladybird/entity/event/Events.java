package edu.yale.library.ladybird.entity.event;

/**
 * Standard ladybird events
 *
 * @author Osman Din
 */
public enum Events {

    USER_LOGIN("user.login"),
    USER_VISIT("user.visit.page"),
    USER_SEARCH("user.search");

    String name;

    private Events(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
