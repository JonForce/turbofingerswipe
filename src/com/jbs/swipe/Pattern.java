package com.jbs.swipe;

import java.util.Random;

public final class Pattern<Type> {
	
	private Type[] options;
	private Object[] sequence;
	private int sequenceIndex = 0;
	
	/**
	 * Create a repeating, randomized pattern.
	 * @param length The length of the Pattern.
	 * @param options The potential values in the Pattern.
	 */
	public Pattern(int length, Type... options) {
		this.options = options;
		this.sequence = new Object[length];
		for (int i = 0; i != sequence.length; i ++)
			sequence[i] = options[i % options.length];
	}
	
	/**
	 * @return The next Object in the Pattern.
	 */
	public Type next() {
		return (Type) sequence[sequenceIndex++ % sequence.length];
	}
	
	/**
	 * Randomize the sequence of Objects in the Pattern.
	 * @param random The random number generator to use.
	 */
	public void scramble(Random random) {
		for (int i = 0; i != sequence.length; i ++)
			sequence[i] = options[random.nextInt(options.length)];
	}
}