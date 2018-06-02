package codeu.model.data;

import java.time.Instant;
import java.util.UUID;

/**
 * Class representing an actvitiy
 * 
 * Attributes:
 * - activityType (ActivityType)
 *  - UserRegistered
 *  - UserLoggedIn
 *  - ConversationCreated
 *  - MessageSent
 * - id (UUID)
 * - creation (Instant)
 */
public class Activity {
    private enum ActivityType {
        UserRegistered, UserLoggedIn, ConversationCreated, MessageSent
    }

    private final ActivityType activityType;
    private final UUID id;
    private final Instant creation;

    public Activity(ActivityType activityType, UUID id, Instant creation) {
        this.activityType = activityType;
        this.id = id;
        this.creation = creation;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreation() {
        return creation;
    }
}