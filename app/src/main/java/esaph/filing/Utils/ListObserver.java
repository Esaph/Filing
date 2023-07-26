package esaph.filing.Utils;

/*
 * Copyright (c) 2023.
 *  Julian Auguscik
 */

import java.io.Serializable;

public interface ListObserver extends Serializable
{
    void observDataChange(boolean isEmpty);
}

