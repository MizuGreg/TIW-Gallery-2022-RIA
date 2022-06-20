package it.polimi.tiw.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckerUtility {

	/**
	 * Checks whether this string is not null, empty or blank
	 * @param value the string to check
	 * @return true if it's well formatted
	 */
	public static boolean checkAvailability(String value) {
		return (value != null && !value.isBlank() && !value.isEmpty());
	}

	public static boolean checkValidImage(String imageString) {
		ArrayList<String> validFormats = new ArrayList<String>() {
			{
				add("jpg");
				add("png");
				add("webp");
			}
		};
				
		return validFormats.contains(getImageExtension(imageString));
	}

	public static String getImageExtension(String imageString){
		List<String> splitString = Arrays.asList(imageString.split("\\."));
		if(splitString.size() == 0) return "";
		String extension = splitString.get(splitString.size()-1); //Last token is the extension
		return extension;
	}

	public static boolean checkValidCss(String cssString) {
		return getImageExtension(cssString).equals("css");
	}

}
