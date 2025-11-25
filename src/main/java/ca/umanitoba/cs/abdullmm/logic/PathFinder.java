package ca.umanitoba.cs.abdullmm.logic;

import ca.umanitoba.cs.abdullmm.exceptions.InvalidUserException;
import ca.umanitoba.cs.abdullmm.exceptions.PathNotFoundException;
import ca.umanitoba.cs.abdullmm.model.Activity;
import ca.umanitoba.cs.abdullmm.model.GridPoint;
import ca.umanitoba.cs.abdullmm.model.LinkedStack;
import ca.umanitoba.cs.abdullmm.model.Route;
import ca.umanitoba.cs.abdullmm.model.Stack;
import ca.umanitoba.cs.abdullmm.model.UserProfile;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Pathfinding manager that finds routes between two points using a stack-based depth-first search.
 * Routes are built from existing activities either from a single user or from multiple users in their feed.
 *
 * Uses a Stack ADT (LinkedStack implementation) for depth-first search exploration.
 *
 * Preconditions for public methods:
 * - All parameters must not be null unless stated
 * - Start and end GridPoints must be valid
 *
 * Postconditions for public methods:
 * - Returns a new Route representing the path found
 * - Does not modify existing routes or activities
 */
public class PathFinder {
    private UserManager userManager;
    private ActivityManager activityManager;

    /**
     * Constructs a PathFinder with required managers.
     *
     * Precondition: both managers are not null
     * Postcondition: initialized with provided managers
     *
     * @param userManager the UserManager for user access
     * @param activityManager the ActivityManager for activity access
     */
    public PathFinder(UserManager userManager, ActivityManager activityManager) {
        Preconditions.checkNotNull(userManager, "UserManager cannot be null");
        Preconditions.checkNotNull(activityManager, "ActivityManager cannot be null");
        this.userManager = userManager;
        this.activityManager = activityManager;
    }

    /**
     * Finds a path between two points using only the specified user's previous routes.
     *
     * Precondition:
     * - userId is not null and user exists
     * - start and end are not null
     *
     * Postcondition: returns a Route containing the path from start to end
     *
     * @param userId the user ID to search within
     * @param start the starting GridPoint
     * @param end the ending GridPoint
     * @return a Route representing the path found
     * @throws InvalidUserException if the user does not exist
     * @throws PathNotFoundException if no path exists between the points
     */
    public Route findPathUserOnly(String userId, GridPoint start, GridPoint end)
            throws InvalidUserException, PathNotFoundException {
        Preconditions.checkNotNull(userId, "User ID cannot be null");
        Preconditions.checkNotNull(start, "Start point cannot be null");
        Preconditions.checkNotNull(end, "End point cannot be null");

        UserProfile profile = userManager.getUser(userId);
        ArrayList<Activity> activities = profile.getActivities();

        Set<GridPoint> availablePoints = extractPointsFromActivities(activities);
        Route path = findPathDFS(start, end, availablePoints);

        return path;
    }

    /**
     * Finds a path between two points using the user's feed (own routes + followed users' routes).
     *
     * Precondition:
     * - userId is not null and user exists
     * - start and end are not null
     *
     * Postcondition: returns a Route containing the path from start to end
     *
     * @param userId the user ID to build feed from
     * @param start the starting GridPoint
     * @param end the ending GridPoint
     * @return a Route representing the path found
     * @throws InvalidUserException if the user does not exist
     * @throws PathNotFoundException if no path exists between the points
     */
    public Route findPathFromFeed(String userId, GridPoint start, GridPoint end)
            throws InvalidUserException, PathNotFoundException {
        Preconditions.checkNotNull(userId, "User ID cannot be null");
        Preconditions.checkNotNull(start, "Start point cannot be null");
        Preconditions.checkNotNull(end, "End point cannot be null");

        // Get all activities visible in feed
        ArrayList<Activity> allActivities = new ArrayList<>();
        UserProfile currentProfile = userManager.getUser(userId);

        // Add own activities
        allActivities.addAll(currentProfile.getActivities());

        // Add followed users' activities
        for (String followedUserId : currentProfile.getFollowedUserIds()) {
            UserProfile followedProfile = userManager.getUser(followedUserId);
            allActivities.addAll(followedProfile.getActivities());
        }

        Set<GridPoint> availablePoints = extractPointsFromActivities(allActivities);
        Route path = findPathDFS(start, end, availablePoints);

        return path;
    }

    /**
     * Finds a path using depth-first search with a stack.
     *
     * Uses the Stack ADT for traversal: marks visited nodes and backtracks when stuck.
     *
     * Precondition:
     * - start and end are not null
     * - availablePoints is not null (may be empty)
     *
     * Postcondition: returns a Route with the path, or throws PathNotFoundException
     *
     * @param start the starting GridPoint
     * @param end the ending GridPoint
     * @param availablePoints set of all available grid points
     * @return a Route representing the path found
     * @throws PathNotFoundException if no path exists
     */
    private Route findPathDFS(GridPoint start, GridPoint end, Set<GridPoint> availablePoints)
            throws PathNotFoundException {
        Preconditions.checkNotNull(start, "Start point cannot be null");
        Preconditions.checkNotNull(end, "End point cannot be null");
        Preconditions.checkNotNull(availablePoints, "Available points cannot be null");

        if (!availablePoints.contains(start)) {
            throw new PathNotFoundException("Starting point is not on any existing route");
        }
        if (!availablePoints.contains(end)) {
            throw new PathNotFoundException("Ending point is not on any existing route");
        }

        Stack<GridPoint> stack = new LinkedStack<>();
        Map<GridPoint, GridPoint> parent = new HashMap<>();
        Set<GridPoint> visited = new HashSet<>();

        stack.push(start);
        visited.add(start);
        parent.put(start, null);

        while (!stack.isEmpty()) {
            GridPoint current = stack.pop();

            if (current.equals(end)) {
                return reconstructPath(parent, start, end);
            }

            // Get neighbors (adjacent points in availablePoints)
            for (GridPoint neighbor : availablePoints) {
                if (!visited.contains(neighbor) && isAdjacent(current, neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    stack.push(neighbor);
                }
            }
        }

        throw new PathNotFoundException("No path exists between the two points");
    }

    /**
     * Checks if two grid points are adjacent (horizontally or vertically).
     *
     * Precondition: both points are not null
     * Postcondition: no state change
     *
     * @param p1 first point
     * @param p2 second point
     * @return true if points are adjacent, false otherwise
     */
    private boolean isAdjacent(GridPoint p1, GridPoint p2) {
        Preconditions.checkNotNull(p1, "Point 1 cannot be null");
        Preconditions.checkNotNull(p2, "Point 2 cannot be null");

        int dx = Math.abs(p1.x() - p2.x());
        int dy = Math.abs(p1.y() - p2.y());

        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    /**
     * Reconstructs the path from start to end using parent pointers.
     *
     * Precondition: parent map contains valid chain from end to start
     * Postcondition: returns a new Route with the path
     *
     * @param parent map of nodes to their parents
     * @param start the starting point
     * @param end the ending point
     * @return a Route representing the path
     */
    private Route reconstructPath(Map<GridPoint, GridPoint> parent, GridPoint start, GridPoint end) {
        Preconditions.checkNotNull(parent, "Parent map cannot be null");
        Preconditions.checkNotNull(start, "Start point cannot be null");
        Preconditions.checkNotNull(end, "End point cannot be null");

        Route path = new Route("Found Path");
        GridPoint current = end;

        while (current != null) {
            path.getPoints().add(0, current); // Add to front to maintain order
            current = parent.get(current);
        }

        return path;
    }

    /**
     * Extracts all GridPoints from a list of activities.
     *
     * Precondition: activities is not null
     * Postcondition: returns a set of all unique points from all routes
     *
     * @param activities list of activities
     * @return set of all available GridPoints
     */
    private Set<GridPoint> extractPointsFromActivities(ArrayList<Activity> activities) {
        Preconditions.checkNotNull(activities, "Activities list cannot be null");

        Set<GridPoint> points = new HashSet<>();
        for (Activity activity : activities) {
            Route route = activity.getRoute();
            if (route != null) {
                points.addAll(route.getPoints());
            }
        }
        return points;
    }
}

