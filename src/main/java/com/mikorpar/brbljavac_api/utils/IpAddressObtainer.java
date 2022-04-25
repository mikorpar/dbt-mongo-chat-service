package com.mikorpar.brbljavac_api.utils;

import com.mikorpar.brbljavac_api.events.HostAddressAcquiredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.channels.SocketChannel;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class IpAddressObtainer {

    @Value("#{${server.port} + 1}")
    private int port;
    public static final String NO_ADDRESS = "";
    private final ApplicationEventPublisher publisher;

    @Bean(name = "hostAddress")
    public String getHostAddress() {
        try {
            for (NetworkInterface intf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!intf.isUp() || intf.isLoopback()) continue;
                for (InetAddress addr : Collections.list(intf.getInetAddresses())) {
                    if (addr instanceof Inet6Address || !addr.isReachable(3000)) continue;
                    try (SocketChannel socket = SocketChannel.open()) {
                        socket.socket().setSoTimeout(3000);
                        socket.socket().setReuseAddress(true);
                        socket.bind(new InetSocketAddress(addr, port));
                        socket.connect(new InetSocketAddress("google.com", 80));
                    } catch (IOException ex) {
                        continue;
                    }
                    publisher.publishEvent(new HostAddressAcquiredEvent(this, addr.getHostAddress()));
                    return addr.getHostAddress();
                }
            }
        } catch (IOException ignored) {}
        publisher.publishEvent(new HostAddressAcquiredEvent(this, NO_ADDRESS));
        return NO_ADDRESS;
    }
}
