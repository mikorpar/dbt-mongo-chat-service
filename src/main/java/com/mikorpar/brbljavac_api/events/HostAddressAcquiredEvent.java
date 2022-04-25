package com.mikorpar.brbljavac_api.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class HostAddressAcquiredEvent extends ApplicationEvent {

    @Getter
    private final String address;

    public HostAddressAcquiredEvent(Object source, String address) {
        super(source);
        this.address = address;
    }
}
