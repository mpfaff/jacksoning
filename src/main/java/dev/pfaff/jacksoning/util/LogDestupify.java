package dev.pfaff.jacksoning.util;

import com.mojang.datafixers.util.Function3;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Some utilities to destupify the logging API.
 */
public final class LogDestupify {
	/**
	 * An extra gate to workaround awful {@link Logger} implementations returning {@code true} from
	 * {@link Logger#isDebugEnabled()}.
	 */
	private static final boolean DEBUG_ENABLED = false;
	/**
	 * @see #DEBUG_ENABLED
	 */
	private static final boolean TRACE_ENABLED = false;
	private final Logger logger;

	public LogDestupify(Logger logger) {
		this.logger = logger;
	}

	public boolean isEnabled(Level level) {
		return switch (level) {
			case ERROR -> logger.isErrorEnabled();
			case WARN -> logger.isWarnEnabled();
			case INFO -> logger.isInfoEnabled();
			case DEBUG -> DEBUG_ENABLED && logger.isDebugEnabled();
			case TRACE -> TRACE_ENABLED && logger.isTraceEnabled();
		};
	}

	private void logAlways(Level level, String message) {
		switch (level) {
			case ERROR -> logger.error(message);
			case WARN -> logger.warn(message);
			case INFO -> logger.info(message);
			case DEBUG -> logger.debug(message);
			case TRACE -> logger.trace(message);
		}
	}

	private void logAlways(Level level, String message, Throwable e) {
		switch (level) {
			case ERROR -> logger.error(message, e);
			case WARN -> logger.warn(message, e);
			case INFO -> logger.info(message, e);
			case DEBUG -> logger.debug(message, e);
			case TRACE -> logger.trace(message, e);
		}
	}

	/**
	 * Prefer {@link #log(Level, Supplier)} and friends when the message is not static.
	 */
	public void log(Level level, String message) {
		if (isEnabled(level)) {
			logAlways(level, message);
		}
	}

	/**
	 * Prefer {@link #log(Level, Function, Object)} or {@link #log(Level, BiFunction, Object, Object)}
	 * where possible. In the event that the JVM cannot optimize away or delay the lambda allocation until inside the if
	 * condition, those methods will not incur a performance penalty because (if used correctly) they will be
	 * capture-less closures, which can (in theory) be memoized by the JVM.
	 */
	public void log(Level level, Supplier<String> message) {
		if (isEnabled(level)) {
			logAlways(level, message.get());
		}
	}

	public <A> void log(Level level, Function<A, String> message, A a) {
		if (isEnabled(level)) {
			logAlways(level, message.apply(a));
		}
	}

	public <A, B> void log(Level level, BiFunction<A, B, String> message, A a, B b) {
		if (isEnabled(level)) {
			logAlways(level, message.apply(a, b));
		}
	}

	public <A, B, C> void log(Level level, Function3<A, B, C, String> message, A a, B b, C c) {
		if (isEnabled(level)) {
			logAlways(level, message.apply(a, b, c));
		}
	}


	/**
	 * Prefer {@link #log(Level, Supplier)} when the message is not static.
	 */
	public void log(Level level, String message, Throwable e) {
		if (isEnabled(level)) {
			logAlways(level, message, e);
		}
	}

	public void log(Level level, Supplier<String> message, Throwable e) {
		if (isEnabled(level)) {
			logAlways(level, message.get(), e);
		}
	}
}
