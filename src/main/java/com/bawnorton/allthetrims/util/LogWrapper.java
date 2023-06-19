package com.bawnorton.allthetrims.util;

import org.slf4j.Logger;

public class LogWrapper {
    private final Logger logger;
    private final String prefix;

    public LogWrapper(Logger logger, String prefix) {
        this.logger = logger;
        this.prefix = prefix;
    }

    public static LogWrapper of(Logger logger, String prefix) {
        return new LogWrapper(logger, prefix);
    }

    public void info(String message) {
        logger.info(prefix + " " + message);
    }

    public void warn(String message) {
        logger.warn(prefix + " " + message);
    }

    public void error(String message) {
        logger.error(prefix + " " + message);
    }

    public void error(String message, Throwable throwable) {
        logger.error(prefix + " " + message, throwable);
    }
}
