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
    private List<Setting > setting;
    /**
     * This class is a singleton, so its constructor is private. Call getInstance() instead.
     */
    private SettingStore(PersistentStorageAgent persistentStorageAgent) {
        this.persistentStorageAgent = persistentStorageAgent;
        setting = new ArrayList<>();
    }
    //
    public void updateColor(Setting setting) {
        persistentStorageAgent.writeThrough(setting);
    }

    //Returning individual color from name from setting store, set by kirielle
    public String getColor(){
        return setting.get(0).getColor();
        //for instance
        //return "red";
    }



    /**
     * Sets the List of Users stored by this UserStore. This should only be called once, when the data
     * is loaded from Datastore.
     */
    public void setSetting(List<Setting> setting) {
        this.setting = setting;
        // getUser(DEFAULT_BOT_USERNAME)
    }

}
