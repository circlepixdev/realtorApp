package com.circlepix.android.types;

public enum ListingType {

	residential ("Residential"),
	commercial ("Commercial"),
	land ("Land"),
	multiunit ("Multi-unit");

	private String title;
	
	ListingType(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return title;
	}
	
	public String getTitle() {
		return title;
	}

	public static ListingType getValueByName(String item) {
		
		for (ListingType t : ListingType.values()) {
			if (t.title.equalsIgnoreCase(item))
				return t;
		}
		
		return null;
	}

	public static ListingType getByOrdinal(int ord) {
		return ListingType.values()[ord];
	}

}
