package utility;

/**
 * The item can be anything, e.g., word, pair, set, topic.
 */
public class ItemWithValue implements Comparable<ItemWithValue> {
	private Object iterm = null;
	private double value = 0.0;

	public ItemWithValue(Object iterm2, double value2) {
		iterm = iterm2;
		value = value2;
	}

	public Object getIterm() {
		return iterm;
	}

	public double getValue() {
		return value;
	}

	@Override
	public int compareTo(ItemWithValue wwp) {
		return Double.compare(wwp.value, this.value);
	}
}
