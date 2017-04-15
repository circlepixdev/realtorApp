package com.circlepix.android.beans;

public enum NarrationType {

	none ("None"),
	male ("Male"),
	female ("Female");

	private String title;
	
	NarrationType(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return title;
	}
	
	public String getTitle() {
		return title;
	}

	public static NarrationType getValueByName(String item) {
		
		for (NarrationType t : NarrationType.values()) {
			if (t.title.equalsIgnoreCase(item))
				return t;
		}
		
		return null;
	}

	public static NarrationType getByOrdinal(int ord) {
		return NarrationType.values()[ord];
	}

}
