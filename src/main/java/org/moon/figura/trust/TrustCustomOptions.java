package org.moon.figura.trust;

import java.util.List;

public interface TrustCustomOptions {
    String getCustomTrustOptionsId();

    List<? extends TrustOption> getTrustOptions();
}
