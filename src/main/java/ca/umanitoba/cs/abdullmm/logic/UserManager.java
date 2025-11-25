package ca.umanitoba.cs.abdullmm.logic;

import ca.umanitoba.cs.abdullmm.exceptions.DuplicateUserException;
import ca.umanitoba.cs.abdullmm.exceptions.InvalidUserException;
import ca.umanitoba.cs.abdullmm.model.UserProfile;
import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for user profiles. Responsible for creating, retrieving, and managing user accounts.
 * Validates business rules related to user management.
 *
 * Preconditions for public methods:
 * - userIds and userProfile parameters must not be null or empty
 *
 * Postconditions for public methods:
 * - User state changes are reflected immediately in the internal map
 */
public class UserManager {
    private Map<String, UserProfile> userProfiles;

    /**
     * Constructs a UserManager with an empty set of users.
     *
     * Postcondition: initialized with empty user map
     */
    public UserManager() {
        this.userProfiles = new HashMap<>();
    }

    /**
     * Creates a new user profile.
     *
     * Precondition: userId is not null, not empty, and not already in use
     * Postcondition: a new UserProfile exists for this userId
     *
     * @param userId the unique identifier for the new user
     * @return the newly created UserProfile
     * @throws DuplicateUserException if userId already exists
     */
    public UserProfile createUser(String userId) throws DuplicateUserException {
        Preconditions.checkNotNull(userId, "User ID cannot be null");
        Preconditions.checkState(!userId.isEmpty(), "User ID cannot be empty");
        Preconditions.checkState(!userId.trim().isEmpty(), "User ID cannot contain only whitespace");

        if (userProfiles.containsKey(userId)) {
            throw new DuplicateUserException("User '" + userId + "' already exists");
        }

        UserProfile profile = new UserProfile(userId);
        userProfiles.put(userId, profile);
        return profile;
    }

    /**
     * Retrieves a user profile by ID.
     *
     * Precondition: userId is not null and not empty
     * Postcondition: no state change; returns null if user does not exist
     *
     * @param userId the user ID to retrieve
     * @return the UserProfile or null if not found
     * @throws InvalidUserException if userId is invalid
     */
    public UserProfile getUser(String userId) throws InvalidUserException {
        Preconditions.checkNotNull(userId, "User ID cannot be null");
        Preconditions.checkState(!userId.isEmpty(), "User ID cannot be empty");

        UserProfile profile = userProfiles.get(userId);
        if (profile == null) {
            throw new InvalidUserException("User '" + userId + "' does not exist");
        }
        return profile;
    }

    /**
     * Checks if a user exists.
     *
     * Precondition: userId is not null and not empty
     * Postcondition: no state change
     *
     * @param userId the user ID to check
     * @return true if the user exists, false otherwise
     */
    public boolean userExists(String userId) {
        Preconditions.checkNotNull(userId, "User ID cannot be null");
        return !userId.isEmpty() && userProfiles.containsKey(userId);
    }

    /**
     * Gets all user IDs in the system.
     *
     * Postcondition: returns a copy of all user IDs
     *
     * @return a set of all user IDs
     */
    public java.util.Set<String> getAllUserIds() {
        return new java.util.HashSet<>(userProfiles.keySet());
    }

    /**
     * Gets all user profiles.
     *
     * Postcondition: returns the internal map (direct access for feed construction)
     *
     * @return map of all user profiles
     */
    protected Map<String, UserProfile> getAllProfiles() {
        return userProfiles;
    }

    /**
     * Gets the number of users in the system.
     *
     * Postcondition: no state change
     *
     * @return the number of user profiles
     */
    public int getUserCount() {
        return userProfiles.size();
    }
}

