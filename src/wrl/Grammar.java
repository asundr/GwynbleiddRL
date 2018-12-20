package rltut;

/**
 * This class contains behavior for applying grammar to text.
 * @author Arun Sundaram
 *
 */
public class Grammar {
	
	private static final String vowels = "aeiou";
	
	/** Takes a sentence beginning with a verb and makes it third-person. */
	public static String makeThirddPerson(String text) {
		String[] words = text.split(" ");
		words[0] = words[0] + 's';
		
		StringBuilder builder = new StringBuilder();
		for (String word : words)
			builder.append(" " + word);
		return builder.toString().trim();
	}
	
	 /** Returns the article 'a' or 'an' for a given noun */
	public static String article(String noun) {
		return vowels.indexOf(noun.charAt(0)) == -1 ? "a" : "an";
	}

}
