package net.gageot.listmaker;

/**
 * TODO.
 */
public interface Accumulator<A, V> {
	/**
	 * TODO.
	 * 
	 * @return TODO.
	 */
	A apply(A accumulator, V value);

	Accumulator<Integer, Integer> SUM = new Accumulator<Integer, Integer>() {
		@Override
		public Integer apply(Integer sum, Integer value) {
			return sum + value;
		}
	};
}
