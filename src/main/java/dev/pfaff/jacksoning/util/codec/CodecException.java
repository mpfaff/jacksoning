package dev.pfaff.jacksoning.util.codec;

import io.netty.util.internal.EmptyArrays;

import java.io.PrintStream;
import java.io.PrintWriter;

public class CodecException extends Exception {
	public static final CodecException NO_CONTEXT = new CodecException();

	private CodecException() {}

	public CodecException(String message) {
		super(message);
	}

	public CodecException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodecException(Throwable cause) {
		super(cause);
	}

	// TODO: stack trace when a debug flag is enabled

	@Override
	public final void printStackTrace() {
	}

	@Override
	public final void printStackTrace(PrintStream s) {
	}

	@Override
	public final void printStackTrace(PrintWriter s) {
	}

	@Override
	public final CodecException fillInStackTrace() {
		return this;
	}

	@Override
	public final StackTraceElement[] getStackTrace() {
		return EmptyArrays.EMPTY_STACK_TRACE;
	}

	@Override
	public final void setStackTrace(StackTraceElement[] stackTrace) {
	}
}
