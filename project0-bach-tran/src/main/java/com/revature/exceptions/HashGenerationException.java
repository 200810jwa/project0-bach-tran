package com.revature.exceptions;

@SuppressWarnings("serial")
public class HashGenerationException extends RuntimeException {
	
	public HashGenerationException() {
		super();
	}

	public HashGenerationException(String message) {
		super(message);
	}
	
}
