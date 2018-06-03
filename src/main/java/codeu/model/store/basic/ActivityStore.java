package codeu.model.store.basic;

import codeu.model.data.User;
import codeu.model.data.Message;
import codeu.model.data.Conversation;
import codeu.model.data.Activity;
import codeu.model.data.ActivityType;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.time.Instant;

/**
 * Store class that uses in-memory data structures to hold values and automatically loads from and
 * saves to PersistentStorageAgent. It's a singleton so all servlet classes can access the same
 * instance.
 */
public class ActivityStore {

    /** Singleton instance of ActivityStore. */
    private static ActivityStore instance;

    /**
     * Returns the singleton instance of ActivityStore that should be shared between all servlet classes.
     * Do not call this function from a test; use getTestInstance() instead.
     */
    public static ActivityStore getInstance() {
        if (instance == null) {
            instance = new ActivityStore(PersistentStorageAgent.getInstance());
        }
        return instance;
    }

    /**
     * Instance getter function used for testing. Supply a mock for PersistentStorageAgent.
     *
     * @param persistentStorageAgent a mock used for testing
     */
    public static ActivityStore getTestInstance(PersistentStorageAgent persistentStorageAgent) {
        return new ActivityStore(persistentStorageAgent);
    }

    /**
     * The PersistentStorageAgent responsible for loading Users from and saving Users to Datastore.
     */
    private PersistentStorageAgent persistentStorageAgent;

    /** The in-memory list of Activities. */
    private List<Activity> activities;

    /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
    private ActivityStore(PersistentStorageAgent persistentStorageAgent) {
        this.persistentStorageAgent = persistentStorageAgent;
        activities = new ArrayList<>();
    }

     /**
     * Access all Activity objects.
     */
    public List<Activity> getAllActivities() {
        return activities;
    }

    /**
     * Access all Activity objects, sorted from oldest to most recent.
     * Sorts all of the activities based on creation attribute.
     */
    public List<Activity> getAllSortedActivities() {
        List<Activity> activitiesCopy = new ArrayList<>(activities);
        activitiesCopy.sort(Collections.reverseOrder());
        return activitiesCopy;
    }

    /**
     * Access all Activity objects that were created on or after a specified date.
     * 
     * @param instant time that the Activity objects must be created on or after
     */
    public List<Activity> getActivitiesMadeOnOrAfter(Instant instant) {
        int index = -1; // Index to take a sublist up to, exclusive
        List<Activity> sortedActivities = getAllActivities();
        for (int i=0; i < sortedActivities.size(); i++) {
            if (sortedActivities.get(i).getCreationTime().compareTo(instant) < 0) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            return sortedActivities.subList(0,index);
        } else {
            return sortedActivities;
        }
    }

    /**
     * Access a specified number of Activity objects from a specified index
     * 
     * @param startIndex index to begin subarray
     * @param batchSize number of Activity objects to access
     */
    public List<Activity> getActivitiesBatch(int startIndex, int batchSize) {
        if (startIndex + batchSize > activities.size()) {
            return getAllActivities().subList(startIndex, activities.size());
        } else {
            return getAllActivities().subList(startIndex, startIndex + batchSize);
        }
    }

    /** Add a new activity to the current set of activities known to the application. */
    public void addActivity(Activity activity) {
        activities.add(activity);
        persistentStorageAgent.writeThrough(activity);
    }

    public void addActivity(User user) {
        Activity activity = new Activity(ActivityType.UserRegistered, user.getId(), user.getCreationTime());
        activities.add(activity);
        persistentStorageAgent.writeThrough(activity);
    }

    public void addActivity(Message message) {
        Activity activity = new Activity(ActivityType.MessageSent, message.getId(), message.getCreationTime());
        activities.add(activity);
        persistentStorageAgent.writeThrough(activity);
    }

    public void addActivity(Conversation conversation) {
        Activity activity = new Activity(ActivityType.ConversationCreated, conversation.getId(), conversation.getCreationTime());                
        activities.add(activity);
        persistentStorageAgent.writeThrough(activity);
    }

    /** Sets the List of Activities stored by this ActivityStore. */
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    /** Get number of activities */
    public int getActivitiesCount() {
        return activities.size();
    }

}