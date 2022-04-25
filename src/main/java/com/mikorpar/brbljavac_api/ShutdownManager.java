package com.mikorpar.brbljavac_api;

import com.mikorpar.brbljavac_api.events.HostAddressAcquiredEvent;
import com.mikorpar.brbljavac_api.utils.IpAddressObtainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShutdownManager implements ApplicationListener<HostAddressAcquiredEvent> {

    @Autowired
    private ApplicationContext appContext;

    @Override
    public void onApplicationEvent(HostAddressAcquiredEvent event) {
        if (event.getAddress().equals(IpAddressObtainer.NO_ADDRESS)){
            log.error("Host IP address is not successfully acquired. Please wait 2 minutes and try again.");
            System.exit(SpringApplication.exit(appContext, () -> 1));
        }
    }
}
