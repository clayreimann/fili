/*
 * Copyright 2017 Yahoo Inc. All rights reserved.
 */
package com.yahoo.bard.webservice.logging;

import com.yahoo.bard.webservice.application.MetricRegistryFactory;

import com.codahale.metrics.MetricRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Represents a phase that is timed.
 * TimedPhase is used to associate a Timer located in the registry with the exact duration of such a phase for a
 * specific request.
 */
public class TimedPhase {
    private static final MetricRegistry REGISTRY = MetricRegistryFactory.getRegistry();
    private static final Logger LOG = LoggerFactory.getLogger(RequestLog.class);

    protected final String name;
    protected long start;
    protected long duration;

    /**
     * Constructor.
     *
     * @param name  Name of the phase
     */
    protected TimedPhase(String name) {
        this.name = name;
    }

    /**
     * Start the phase.
     */
    protected void start() {
        if (start != 0) {
            LOG.warn("Tried to start timer that is already running: {}", name);
            return;
        }
        start = System.nanoTime();
    }

    /**
     * Stop the phase.
     */
    protected void stop() {
        if (start == 0) {
            LOG.warn("Tried to stop timer that has not been started: {}", name);
            return;
        }
        duration += System.nanoTime() - start;
        REGISTRY.timer(name).update(duration, TimeUnit.NANOSECONDS);
        start = 0;
    }

    protected boolean isRunning() {
        return start != 0;
    }
}