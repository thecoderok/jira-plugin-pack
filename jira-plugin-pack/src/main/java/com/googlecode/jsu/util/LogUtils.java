package com.googlecode.jsu.util;

import org.apache.log4j.Logger;

/**
 * @author Alexey Abashev
 */
public final class LogUtils {
	private static final Logger GENERAL = Logger.getLogger("com.googlecode.jsu");
	
	public static Logger getGeneral() {
		return GENERAL;
	}

	private LogUtils() {
	}
}
