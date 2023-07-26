package esaph.filing.Board.Model;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import java.io.Serializable;

public enum BoardPolicy implements Serializable
{
    POLICY_PRIVATE("pprivate"),
    POLICY_PUBLIC("ppublic");

    private final String displayName;

    BoardPolicy(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
