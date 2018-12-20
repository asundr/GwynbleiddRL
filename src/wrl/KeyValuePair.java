package wrl;

/**  
 * A 2-tuple useful for encapsulating key-value pairs
 * @author Arun Sundaram
 *
 */
public class KeyValuePair<K, V> {
	
	public K key;
	public V value;
	
	public KeyValuePair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if ( !(other instanceof KeyValuePair<?,?>) )
			return false;
		@SuppressWarnings("unchecked")
		KeyValuePair<K, V> o = (KeyValuePair<K, V>) other;
		return this.key.equals(o.key) && o.value.getClass().equals(this.value.getClass());
	}
}
