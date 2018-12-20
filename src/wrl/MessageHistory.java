package wrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class manages a chronological message history.
 * @author Arun Sundaram
 *
 */
public class MessageHistory {
	
	private String[] messageHistory;
	private int index;
	private int messageCount;
	private int maxMessages;
	private int newMessages = 1;
	private boolean resetNewMessages = false;
	
	
	public MessageHistory(int maxMessages) {
		this.maxMessages = maxMessages;
		this.messageHistory = new String[maxMessages];
		this.index = 0;
		this.messageCount = 0;
	}
	
	/** {@code maxMessages} defaults to 1024 */
	public MessageHistory() {
		this(1024);
	}
	
	/** Returns the total number of messages in history. */
	public int size() {
		return messageCount;
	}
	
	/** Returns the maximum number of messages in the history. */
	public int maxHistory() {
		return maxMessages;
	}

	/** Returns the number of messages added since both {@linkplain #add(String)} and {@linkplain #get(int, int)} were called. */
	public int newMessages() {
		return newMessages;
	}
	
	/** Adds the message to the history. If {@linkplain #get(int, int)} was called before this, it resets {@code newMessages} to zero. */
	public void add(String message) {
		if (resetNewMessages) {
			newMessages = 0;
			resetNewMessages = false;
		}
		messageHistory[index] = message;
		index = (index + 1) %  maxMessages;
		messageCount = Math.min(maxMessages, messageCount + 1);
		newMessages++;
	}
	
	/** 
	 * Returns a list of messages in order of creation.
	 * @param last - the most recent message to retrieve (0 would be most recent)
	 * @param count - the maximum number of messages to retrieve ending with {@code last} 
	 * @return
	 */
	public List<String> get(int last, int count) {
		resetNewMessages = true;
		List<String> out = new ArrayList<String>();
		if (count >= messageCount) {
			for (String s : messageHistory) {
				if (s == null)
					break;
				else
					out.add(s);
			}
			return out;
		}
		
		int earlyIndex = (index - last - count + maxMessages);
		earlyIndex %= maxMessages;
		int i = index-last;
		while (i != earlyIndex) {
			i = (i-1+maxMessages) % maxMessages;
			if (messageHistory[i] == null)
				break;
			out.add(messageHistory[i]);
		}
		
		Collections.reverse(out);
		return out;
	}

}
