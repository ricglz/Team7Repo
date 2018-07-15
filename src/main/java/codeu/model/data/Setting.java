package codeu.model.data;

public class Setting {
    private String defaultTheme;

    public Setting(String  defaultTheme){
        this.defaultTheme = defaultTheme;
    }

    public String getColor(){
        return defaultTheme;
    }
    public void setColor(String Cname){
        defaultTheme = Cname;
    }
}
