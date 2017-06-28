package ogiba.styleablesharedialog.ShareDialog.Core;

/**
 * Created by ogiba on 28.06.2017.
 */

public enum ShareType {
    TEXT("text/*"),
    IMAGE("image/*"),
    JPEG("image/jpeg"),
    PNG("image/png");


    private String value;

    ShareType(String value){
        this.value = value;
    }

    public boolean equalsValue(String value){
        return this.value.equals(value);
    }

    @Override
    public String toString() {
        return value;
    }

    public static ShareType fromString(String text) {
        for (ShareType type : ShareType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }
}
