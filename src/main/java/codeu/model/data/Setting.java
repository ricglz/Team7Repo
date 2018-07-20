package codeu.model.data;
public class Setting {
    SettingType type;
    String value;


    public Setting(SettingType type, String value) {
        this.type = type;
        this.value = value;
    }

    public SettingType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public  void setValue(String values){
     this.value=values;
    }


    public enum SettingType {


        FONT_SIZE("font_size"), COLORS("color");


        private String storageKey;

        SettingType(String storageKey) {
            this.storageKey = storageKey;
        }

        public String getTypeString() {
            return this.storageKey;
        }

        public static SettingType getSettingType(String typeString){
            for(SettingType type: SettingType.values()){
            if(type.getTypeString().equals(typeString)){
                return type;
            }
            }
            return null;
        }

    }
    }




