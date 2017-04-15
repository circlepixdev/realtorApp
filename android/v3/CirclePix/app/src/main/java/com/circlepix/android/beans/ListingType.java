package com.circlepix.android.beans;

public enum ListingType {

	forSale ("For Sale"),
	forLease ("For Lease"),
	forRent ("For Rent");

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
