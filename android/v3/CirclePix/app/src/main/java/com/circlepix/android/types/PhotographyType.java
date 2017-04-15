package com.circlepix.android.types;

public enum PhotographyType {

	professional ("Professional"),
	agent ("Agent");

	private String title;

	PhotographyType(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return title;
	}

	public String getTitle() {
		return title;
	}

	public static PhotographyType getValueByName(String item) {

		for (PhotographyType t : PhotographyType.values()) {
			if (t.title.equalsIgnoreCase(item))
				return t;
		}

		return null;
	}

	public static PhotographyType getByOrdinal(int ord) {
		return PhotographyType.values()[ord];
	}

}
