package codeu.model.store.basic;
import codeu.model.data.Setting;

import java.util.ArrayList;
import java.util.List;
import codeu.model.store.persistence.PersistentStorageAgent;

public class SettingStore {
    /**
     * Singleton instance of UserStore.
     */
    private static SettingStore instance;

    /**
     * Returns the singleton instance of UserStore that should be shared between all servlet classes.
     * Do not call this function from a test; use getTestInstance() instead.
     */
    public static SettingStore getInstance() {
        if (instance == null) {
            instance = new SettingStore(PersistentStorageAgent.getInstance());
        }
        return instance;
    }

    /**
     * The PersistentStorageAgent responsible for loading Setting from and saving Users to Datastore.
     */

    private PersistentStorageAgent persistentStorageAgent;

    //** The in list memory of the setting. */
    private List<Setting > settings;
    /**
     * This class is a singleton, so its constructor is private. Call getInstance() instead.
     */
    private SettingStore(PersistentStorageAgent persistentStorageAgent) {
        this.persistentStorageAgent = persistentStorageAgent;
        settings = new ArrayList<>();
    }
    //
    public void updateSetting(Setting setting) {
        persistentStorageAgent.writeThrough(setting);
    }
    /**
     * Add a new setting to the current set of settings known to the application.
     */
    public void addSetting(Setting setting) {
        settings.add(setting);
        persistentStorageAgent.writeThrough(setting);
    }

    public List<Setting> getallSetting(){
        return  new ArrayList<>(settings);
    }

    public void setUsers(List<Setting> setting_newType){
        this.settings = setting_newType;
    }

    public Setting getSettingbyType(Setting.SettingType type){
        for(Setting types: settings){
            if(types.getType().equals(type)){
                return types;
            }
        }
        return null;
    }
    public void setSetting(List<Setting> setting) {
        this.settings = setting;
    }

}
