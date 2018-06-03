package codeu.model.data;

import java.time.Instant;
import java.util.UUID;
import java.util.Comparator;


/**
 * Class representing an actvitiy.
 * 
 * Attributes:
 * - activityType (ActivityType)
 *  - UserRegistered
 *  - ConversationCreated
 *  - MessageSent
 * - id (UUID)
 * - creationTime (Instant)
 */
public class Activity implements Comparator<Activity> {

    private final ActivityType activityType;
    private final UUID id;
    private final Instant creationTime;

    public Activity(ActivityType activityType, UUID id, Instant creationTime) {
        this.activityType = activityType;
        this.id = id;
        this.creationTime = creationTime;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    /** 
     * Compare two activites, based on creationTime time.
     */
    @Override
    public int compare(Activity activity1, Activity activity2) {
        return activity1.getCreationTime().compareTo(activity2.getCreationTime());
    }
}