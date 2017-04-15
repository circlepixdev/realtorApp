package com.circlepix.android.types;

/**
 * Created by Keuahn on 6/13/2016.
 */


public enum ApiResultType {

    SUCCESS ("Success"),
    FAILED ("Failed"),
    DOWN ("Down"),
    UNKNOWN ("Unknown");

    private String title;

    ApiResultType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public static ApiResultType getValueByName(String item) {

        for (ApiResultType t : ApiResultType.values()) {
            if (t.title.equalsIgnoreCase(item))
                return t;
        }

        return null;
    }

    public static ApiResultType getByOrdinal(int ord) {
        return ApiResultType.values()[ord];
    }


}
