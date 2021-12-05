package esaph.filing.Utils;

import java.io.Serializable;

public interface ListObserver extends Serializable
{
    void observDataChange(boolean isEmpty);
}

