package codeu.model.data;

import java.time.Instant;
import java.util.UUID;
import java.util.Comparator;
import java.util.List;


/**
 * Class representing an actvitiy.
 * 
 * Attributes:
 * - ActivityType (ActivityType)
 *  - UserRegistered
 *  - ConversationCreated
 *  - MessageSent
 * - id (UUID)
 * - creationTime (Instant)
 */
public class Activity implements Comparable<Activity> {
    public enum ActivityType {
        UserRegistered {
            @Override
            public String getDisplayStringFormat() {
                // First %1$s = time
                // Second %2$s = username
                return "<strong>%1$s</strong>: %2$s joined!";
            }
        },

        MessageSent {
            @Override
            public String getDisplayStringFormat() {
                // First %1$s = time
                // Second %2$s = username
                // Third %3$s = conversation title
                // Fourth %4$s = message content
                return "<strong>%1$s</strong>: %2$s sent a message in <a href=\"/chat/%3$s\">%3$s</a>: \"%4$s\"";
            }
        },

        ConversationCreated {
            @Override
            public String getDisplayStringFormat() {
                // First %1$s = time
                // Second %2$s = username
                // Third %3$s = conversation title
                return "<strong>%1$s</strong>: %2$s created a new conversation: <a href=\"/chat/%3$s\">%3$s</a>";
            }
        };

        public abstract String getDisplayStringFormat();
    }
    

    private final ActivityType activityType;
    private final UUID id;
    private final Instant creationTime;
    private final List<String> displayStringParameters;

    public Activity(ActivityType activityType, UUID id, Instant creationTime, List<String> displayStringParameters) {
        this.activityType = activityType;
        this.id = id;
        this.creationTime = creationTime;
        this.displayStringParameters = displayStringParameters;
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

    public List<String> getDisplayStringParameters() {
        return displayStringParameters;
    }

    public String getDisplayString() {
        return String.format(activityType.getDisplayStringFormat(), displayStringParameters.toArray());
    }

    /** 
     * Compare two activites, based on creationTime time.
     */
    @Override
    public int compareTo(Activity otherActivity) {
        return getCreationTime().compareTo(otherActivity.getCreationTime());
    }
}