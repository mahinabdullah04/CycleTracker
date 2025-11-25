package ca.umanitoba.cs.abdullmm.logic;

import ca.umanitoba.cs.abdullmm.exceptions.InvalidActivityException;
import ca.umanitoba.cs.abdullmm.exceptions.InvalidUserException;
import ca.umanitoba.cs.abdullmm.model.Activity;
import ca.umanitoba.cs.abdullmm.model.ActivityFeed;
import ca.umanitoba.cs.abdullmm.model.Gear;
import ca.umanitoba.cs.abdullmm.model.Route;
import ca.umanitoba.cs.abdullmm.model.UserProfile;
import com.google.common.base.Preconditions;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Manager for activities. Responsible for creating activities, managing activity history,
 * and providing feed operations. Validates business rules related to activities.
 *
 * Preconditions for public methods:
 * - Parameters must not be null unless explicitly stated
 * - userManager must be provided and non-null
 *
 * Postconditions for public methods:
 * - Activity state changes are reflected in user profiles immediately
 */
public class ActivityManager {
    private UserManager userManager;

    /**
     * Constructs an ActivityManager with a given UserManager.
     *
     * Precondition: userManager is not null
     * Postcondition: initialized with provided UserManager
     *
     * @param userManager the UserManager for user profile access
     */
    public ActivityManager(UserManager userManager) {
        Preconditions.checkNotNull(userManager, "UserManager cannot be null");
        this.userManager = userManager;
    }

    /**
     * Creates a new activity and records it for a user.
     *
     * Precondition:
     * - userId is not null and empty, and user exists
     * - route is not null and contains at least one point
     * - gear is not null
     * - date is not null
     * - distance > 0
     * - durationMinutes > 0
     * - name is not null and not empty
     *
     * Postcondition: activity is created and added to user's activity history
     *
     * @param userId the ID of the user creating the activity
     * @param route the route taken in this activity
     * @param gear the gear used in this activity
     * @param date the date of the activity
     * @param distance the distance covered
     * @param durationMinutes the duration in minutes
     * @param name the activity name
     * @return the created Activity
     * @throws InvalidUserException if the user does not exist
     * @throws InvalidActivityException if activity parameters are invalid
     */
    public Activity createActivity(String userId, Route route, Gear gear, LocalDate date,
                                   double distance, int durationMinutes, String name)
            throws InvalidUserException, InvalidActivityException {
        Preconditions.checkNotNull(userId, "User ID cannot be null");
        Preconditions.checkNotNull(route, "Route cannot be null");
        Preconditions.checkNotNull(gear, "Gear cannot be null");
        Preconditions.checkNotNull(date, "Date cannot be null");
        Preconditions.checkNotNull(name, "Name cannot be null");

        UserProfile userProfile = userManager.getUser(userId);

        try {
            Activity activity = new Activity(route, gear, date, distance, durationMinutes, name);
            userProfile.addActivity(activity);
            return activity;
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new InvalidActivityException("Failed to create activity: " + e.getMessage(), e);
        }
    }

    /**
     * Gets a user's activity history.
     *
     * Precondition: userId is not null, not empty, and user exists
     * Postcondition: returns a copy of the user's activities
     *
     * @param userId the user ID
     * @return list of the user's activities
     * @throws InvalidUserException if the user does not exist
     */
    public ArrayList<Activity> getUserActivities(String userId) throws InvalidUserException {
        Preconditions.checkNotNull(userId, "User ID cannot be null");
        UserProfile userProfile = userManager.getUser(userId);
        return new ArrayList<>(userProfile.getActivities());
    }

    /**
     * Duplicates a previous route into a new activity for the same user.
     * Used when a user wants to reuse a route they've taken before.
     *
     * Precondition:
     * - userId is not null and user exists
     * - sourceActivityIndex >= 0 and < number of user's activities
     * - All other activity parameters are valid (see createActivity)
     *
     * Postcondition: a new activity is created with the duplicated route
     *
     * @param userId the user ID
     * @param sourceActivityIndex the index of the activity whose route to duplicate
     * @param gear the gear to use for the new activity
     * @param date the date for the new activity
     * @param durationMinutes the duration for the new activity
     * @param name the name for the new activity
     * @return the created Activity with the duplicated route
     * @throws InvalidUserException if the user does not exist
     * @throws InvalidActivityException if activity parameters are invalid or index is out of bounds
     */
    public Activity duplicateRouteActivity(String userId, int sourceActivityIndex, Gear gear,
                                           LocalDate date, int durationMinutes, String name)
            throws InvalidUserException, InvalidActivityException {
        Preconditions.checkNotNull(userId, "User ID cannot be null");
        UserProfile userProfile = userManager.getUser(userId);

        ArrayList<Activity> activities = userProfile.getActivities();
        if (sourceActivityIndex < 0 || sourceActivityIndex >= activities.size()) {
            throw new InvalidActivityException("Invalid activity index: " + sourceActivityIndex);
        }

        Activity sourceActivity = activities.get(sourceActivityIndex);
        Route originalRoute = sourceActivity.getRoute();
        double distance = sourceActivity.getDistance();

        return createActivity(userId, originalRoute, gear, date, distance, durationMinutes, name);
    }

    /**
     * Gets an activity feed for a user (own activities + followed users' activities).
     *
     * Precondition: userId is not null and user exists
     * Postcondition: returns a new ActivityFeed object
     *
     * @param userId the user ID
     * @return the ActivityFeed for this user
     * @throws InvalidUserException if the user does not exist
     */
    public ActivityFeed getActivityFeed(String userId) throws InvalidUserException {
        Preconditions.checkNotNull(userId, "User ID cannot be null");
        userManager.getUser(userId); // Validate user exists
        return new ActivityFeed(userId, userManager.getAllProfiles());
    }
}

