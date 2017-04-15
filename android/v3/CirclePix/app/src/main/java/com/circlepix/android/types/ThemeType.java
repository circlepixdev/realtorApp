package com.circlepix.android.types;

public enum ThemeType {

	circlepix ("CirclePix"),
	red ("Red"),
	green ("Green"),
	blue ("Blue");

	private String title;
	
	ThemeType(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return title;
	}
	
	public String getTitle() {
		return title;
	}

	public static ThemeType getValueByName(String item) {
		
		for (ThemeType t : ThemeType.values()) {
			if (t.title.equalsIgnoreCase(item))
				return t;
		}
		
		return null;
	}

	public static ThemeType getByOrdinal(int ord) {
		return ThemeType.values()[ord];
	}

}
