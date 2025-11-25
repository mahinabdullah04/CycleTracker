package ca.umanitoba.cs.abdullmm.model;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an activity feed that aggregates activities from a user and their followed users.
 *
 * Class Invariants:
 * - userProfiles is never null (may be empty)
 * - currentUserId is not null and not empty
 */
public class ActivityFeed {
    private String currentUserId;
    private Map<String, UserProfile> userProfiles;

    /**
     * Constructs an ActivityFeed for a specific user.
     *
     * Precondition: currentUserId is not null and not empty; userProfiles is not null
     * Postcondition: feed is initialized to aggregate activities
     *
     * @param currentUserId the ID of the user viewing the feed
     * @param userProfiles a map of all user profiles in the system
     */
    public ActivityFeed(String currentUserId, Map<String, UserProfile> userProfiles) {
        Preconditions.checkNotNull(currentUserId, "Current user ID cannot be null");
        Preconditions.checkState(!currentUserId.isEmpty(), "Current user ID cannot be empty");
        Preconditions.checkNotNull(userProfiles, "User profiles map cannot be null");

        this.currentUserId = currentUserId;
        this.userProfiles = userProfiles;
        checkInvariant();
    }

    /**
     * Gets all activities visible in the feed: own activities and followed users' activities.
     *
     * Postcondition: returns a new ArrayList combining own and followed activities
     *
     * @return list of all activities visible in the feed
     */
    public ArrayList<Activity> getAllActivities() {
        ArrayList<Activity> feedActivities = new ArrayList<>();

        // Add current user's activities
        UserProfile currentProfile = userProfiles.get(currentUserId);
        if (currentProfile != null) {
            feedActivities.addAll(currentProfile.getActivities());
        }

        // Add followed users' activities
        if (currentProfile != null) {
            for (String followedUserId : currentProfile.getFollowedUserIds()) {
                UserProfile followedProfile = userProfiles.get(followedUserId);
                if (followedProfile != null) {
                    feedActivities.addAll(followedProfile.getActivities());
                }
            }
        }

        return feedActivities;
    }

    /**
     * Gets the current user's own activities only.
     *
     * Postcondition: returns a new ArrayList with only current user's activities
     *
     * @return list of current user's activities
     */
    public ArrayList<Activity> getOwnActivities() {
        UserProfile currentProfile = userProfiles.get(currentUserId);
        if (currentProfile == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(currentProfile.getActivities());
    }

    /**
     * Gets activities from all followed users.
     *
     * Postcondition: returns a new ArrayList with followed users' activities only
     *
     * @return list of followed users' activities
     */
    public ArrayList<Activity> getFollowedActivities() {
        ArrayList<Activity> followedActivities = new ArrayList<>();

        UserProfile currentProfile = userProfiles.get(currentUserId);
        if (currentProfile == null) {
            return followedActivities;
        }

        for (String followedUserId : currentProfile.getFollowedUserIds()) {
            UserProfile followedProfile = userProfiles.get(followedUserId);
            if (followedProfile != null) {
                followedActivities.addAll(followedProfile.getActivities());
            }
        }

        return followedActivities;
    }

    /**
     * Verifies the class invariants.
     */
    private void checkInvariant() {
        Preconditions.checkNotNull(userProfiles, "User profiles map cannot be null");
        Preconditions.checkNotNull(currentUserId, "Current user ID cannot be null");
        Preconditions.checkState(!currentUserId.isEmpty(), "Current user ID cannot be empty");
    }
}

