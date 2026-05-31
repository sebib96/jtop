package org.jtop.service;

import org.jtop.model.SystemSnapshot;

public interface DataSource {

    SystemSnapshot getLatestSnapshot();

    void startPolling();

}
