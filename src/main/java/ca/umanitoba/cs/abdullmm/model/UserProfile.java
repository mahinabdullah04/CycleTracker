package ca.umanitoba.cs.abdullmm.model;

import com.google.common.base.Preconditions;
import java.util.ArrayList;

/**
 * Represents a user profile with personal gear and activity history.
 *
 * Class Invariants:
 * - gearInventory is never null (may be empty)
 * - activities is never null (may be empty)
 * - followedUsers is never null (may be empty)
 * - userId is not null and not empty
 */
public class UserProfile {
    private String userId;
    private ArrayList<Gear> gearInventory;
    private ArrayList<Activity> activities;
    private ArrayList<String> followedUserIds;

    /**
     * Constructs a UserProfile for a given user.
     *
     * Precondition: userId is not null and not empty
     * Postcondition: profile is initialized with empty gear, activities, and followed users
     *
     * @param userId the unique identifier for this user
     */
    public UserProfile(String userId) {
        this.userId = userId;
        this.gearInventory = new ArrayList<>();
        this.activities = new ArrayList<>();
        this.followedUserIds = new ArrayList<>();
        checkInvariant();
    }

    public String getUserId() {
        return userId;
    }

    public ArrayList<Gear> getGearInventory() {
        return gearInventory;
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public ArrayList<String> getFollowedUserIds() {
        return followedUserIds;
    }

    /**
     * Adds gear to the user's inventory.
     *
     * Precondition: gear is not null
     * Postcondition: gear has been added to gearInventory
     *
     * @param gear the gear to add
     */
    public void addGear(Gear gear) {
        Preconditions.checkNotNull(gear, "Gear cannot be null");
        gearInventory.add(gear);
        checkInvariant();
    }

    /**
     * Removes gear from the user's inventory.
     *
     * Precondition: gear is in the inventory
     * Postcondition: gear has been removed from gearInventory
     *
     * @param gear the gear to remove
     */
    public void removeGear(Gear gear) {
        Preconditions.checkNotNull(gear, "Gear cannot be null");
        gearInventory.remove(gear);
        checkInvariant();
    }

    /**
     * Adds an activity to the user's history.
     *
     * Precondition: activity is not null
     * Postcondition: activity has been added to activities
     *
     * @param activity the activity to record
     */
    public void addActivity(Activity activity) {
        Preconditions.checkNotNull(activity, "Activity cannot be null");
        activities.add(activity);
        checkInvariant();
    }

    /**
     * Checks if this user is following another user.
     *
     * Precondition: targetUserId is not null and not empty
     * Postcondition: no state change
     *
     * @param targetUserId the user ID to check
     * @return true if following, false otherwise
     */
    public boolean isFollowing(String targetUserId) {
        Preconditions.checkNotNull(targetUserId, "Target user ID cannot be null");
        Preconditions.checkState(!targetUserId.isEmpty(), "Target user ID cannot be empty");
        return followedUserIds.contains(targetUserId);
    }

    /**
     * Follows another user.
     *
     * Precondition: targetUserId is not null, not empty, and not already being followed
     * Postcondition: targetUserId is in followedUserIds
     *
     * @param targetUserId the user ID to follow
     */
    public void followUser(String targetUserId) {
        Preconditions.checkNotNull(targetUserId, "Target user ID cannot be null");
        Preconditions.checkState(!targetUserId.isEmpty(), "Target user ID cannot be empty");
        Preconditions.checkState(!followedUserIds.contains(targetUserId),
                "Already following this user");
        followedUserIds.add(targetUserId);
        checkInvariant();
    }

    /**
     * Unfollows a user.
     *
     * Precondition: targetUserId is currently being followed
     * Postcondition: targetUserId is not in followedUserIds
     *
     * @param targetUserId the user ID to unfollow
     */
    public void unfollowUser(String targetUserId) {
        Preconditions.checkNotNull(targetUserId, "Target user ID cannot be null");
        Preconditions.checkState(followedUserIds.contains(targetUserId),
                "Not currently following this user");
        followedUserIds.remove(targetUserId);
        checkInvariant();
    }

    /**
     * Verifies the class invariants.
     */
    private void checkInvariant() {
        Preconditions.checkState(gearInventory != null, "Gear inventory cannot be null");
        Preconditions.checkState(activities != null, "Activities list cannot be null");
        Preconditions.checkState(followedUserIds != null, "Followed users list cannot be null");
        Preconditions.checkNotNull(userId, "User ID cannot be null");
        Preconditions.checkState(!userId.isEmpty(), "User ID cannot be empty");
    }
}

