package br.unisinos.siead.ds3.ticket.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtils {
	public static Logger loggerForThisClass() {
		StackTraceElement myCaller = Thread.currentThread().getStackTrace()[2];
		return LogManager.getLogger(myCaller.getClassName());
	}
}
