package com.circlepix.android.types;

public enum BackgroundMusicType {

	none ("None"),
	song1 ("Song 1"),
	song2 ("Song 2"),
	song3 ("Song 3");

	private String title;
	
	BackgroundMusicType(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return title;
	}
	
	public String getTitle() {
		return title;
	}

	public static BackgroundMusicType getValueByName(String item) {
		
		for (BackgroundMusicType t : BackgroundMusicType.values()) {
			if (t.title.equalsIgnoreCase(item))
				return t;
		}
		
		return null;
	}

	public static BackgroundMusicType getByOrdinal(int ord) {
		return BackgroundMusicType.values()[ord];
	}

}
