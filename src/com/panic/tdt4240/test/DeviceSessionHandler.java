package com.panic.tdt4240.test;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.Session;
import org.example.model.Device;

@ApplicationScoped
public class DeviceSessionHandler {
    private final Set<Session> sessions = new HashSet<>();
    private final Set<Device> devices = new HashSet<>();
}