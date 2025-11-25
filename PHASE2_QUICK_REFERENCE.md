# Phase 2 Quick Reference Guide

## Project Entry Points

### Phase 2 (NEW)
```bash
mvn exec:java -Dexec.mainClass="ca.umanitoba.cs.abdullmm.ui.MainRepl"
```

### Phase 1 (Preserved)
```bash
mvn exec:java -Dexec.mainClass="ca.umanitoba.cs.abdullmm.Main"
```

---

## Main Menu Options

### Before Sign-In
1. Create Profile - Create new user account
2. Sign In - Sign into existing account

### After Sign-In
1. View Activity Feed - See own and followed users' activities
2. Add Activity - Create new activity with route and gear
3. Manage Gear - Add/remove bikes, helmets, shoes
4. Follow Users - Follow/unfollow other users
5. Find Route - Pathfinding between two points
6. View Map - Display 50x50 grid with obstacles
7. Add Obstacle - Add obstacle to map
8. View My Profile - See profile summary
9. Sign Out - Sign out and return to authentication menu

---

## Key Classes and Responsibilities

### UI Layer (ca.umanitoba.cs.abdullmm.ui)
- **MainRepl**: Main REPL interface, handles all user I/O

### Logic Layer (ca.umanitoba.cs.abdullmm.logic)
- **UserManager**: Create/retrieve users
- **ActivityManager**: Create/retrieve activities, manage feed
- **PathFinder**: Find routes using DFS with Stack

### Model Layer (ca.umanitoba.cs.abdullmm.model)
- **UserProfile**: Stores user data, gear, activities, followed users
- **ActivityFeed**: Aggregates own and followed activities
- **Route**: Collection of GridPoints forming a path
- **Activity**: Represents completed activity with route, gear, stats
- **Stack** (interface) + **LinkedStack** (implementation)
- **Gear** (interface) + **Bike**, **Helmet**, **Shoe**
- **Map**, **Obstacle**, **Dimension**, **GridPoint**

### Exceptions Layer (ca.umanitoba.cs.abdullmm.exceptions)
- **InvalidUserException** - User not found or invalid
- **DuplicateUserException** - User already exists
- **InvalidActivityException** - Activity creation failed
- **InvalidRouteException** - Route error
- **PathNotFoundException** - No path found between points

---

## Data Structures

### LinkedStack<E>
Generic LIFO stack implementation:
- `push(E element)` - Add to top
- `pop()` - Remove and return top
- `peek()` - Return top without removing
- `isEmpty()` - Check if empty

Used in PathFinder for DFS algorithm.

### UserProfile Data
```
UserProfile
├── userId: String
├── gearInventory: ArrayList<Gear>
├── activities: ArrayList<Activity>
└── followedUserIds: ArrayList<String>
```

### ActivityFeed Composition
```
ActivityFeed
├── currentUserId: String
└── userProfiles: Map<String, UserProfile>
    ├── Own activities
    └── Followed users' activities
```

---

## Algorithms

### Pathfinding (DFS with Stack)
```
1. Initialize stack with start point
2. Mark start as visited
3. While stack not empty:
   a. Pop current point
   b. If current == end: reconstruct and return path
   c. For each adjacent unvisited point:
      - Mark visited
      - Record parent
      - Push to stack
4. If stack empty: throw PathNotFoundException
```

### Adjacency Definition
Two points are adjacent if they differ by exactly 1 in X OR Y (not both):
- (x, y) ↔ (x+1, y)
- (x, y) ↔ (x-1, y)
- (x, y) ↔ (x, y+1)
- (x, y) ↔ (x, y-1)

### Available Routes Sources
- **User-only**: User's own activities only
- **Feed-based**: User's activities + followed users' activities

---

## Validation Strategy

### UI Layer (MainRepl)
- String emptiness validation
- Menu choice range validation (1-9)
- Numeric input range validation
- Date format parsing with fallback
- No errors thrown, re-prompt on invalid input

### Logic Layer (Managers)
- User existence validation
- Duplicate user detection
- Activity index bounds checking
- User ID validation
- **Throws custom exceptions**

### Model Layer (Classes)
- Null checks on all parameters
- Invariant assertions using Guava Preconditions
- Empty collection validation
- State consistency validation
- **Throws IllegalStateException/IllegalArgumentException from Preconditions**

---

## Error Messages

### Clear and Actionable
All errors follow format:
```
"Error: [Problem description]. [How to fix]"
```

Examples:
- "Error: User 'john' already exists. Choose a different username."
- "Error: User 'jane' does not exist. Check username or create profile."
- "Error: No path exists between the two points. Try different coordinates or check available routes."
- "Error: Username cannot be empty. Please enter a username."

---

## Testing Flow

### Recommended Test Scenario
1. Create user "alice"
2. Create user "bob"
3. Add gear to alice (bike, helmet)
4. Add gear to bob (shoes)
5. Create route "Downtown" with points (5,5), (6,5), (7,5)
6. Create activity for alice using "Downtown"
7. Sign out alice, sign in bob
8. Create route "Park" with points (3,3), (3,4), (3,5)
9. Create activity for bob using "Park"
10. Sign out bob, sign in alice
11. Follow bob
12. View feed (should see alice's and bob's activities)
13. Find route from (5,5) to (6,5) - should find in user-only scope
14. Find route from (3,3) to (6,5) - should find in feed scope
15. Add obstacle "Building" at (10,10)
16. View map (should show obstacle as '*')

---

## Files Summary

### Created for Phase 2 (19 files)
```
NEW:
├── ui/
│   └── MainRepl.java (741 lines)
├── logic/
│   ├── UserManager.java (122 lines)
│   ├── ActivityManager.java (154 lines)
│   └── PathFinder.java (243 lines)
├── model/
│   ├── Stack.java (interface, 40 lines)
│   ├── LinkedStack.java (implementation, 110 lines)
│   ├── UserProfile.java (144 lines)
│   └── ActivityFeed.java (95 lines)
└── exceptions/
    ├── InvalidUserException.java
    ├── DuplicateUserException.java
    ├── InvalidActivityException.java
    ├── InvalidRouteException.java
    └── PathNotFoundException.java

ENHANCED (4 files):
├── model/Shoe.java (added Guava checks, fixed syntax)
├── model/Helmet.java (added Guava checks)
├── model/Route.java (changed getDistance() to double)
└── README.md (comprehensive Phase 2 documentation)

PRESERVED (5 files):
├── Main.java (original REPL)
├── model/Activity.java
├── model/Bike.java
├── model/Dimension.java
├── model/Gear.java
├── model/GridPoint.java
├── model/Map.java
├── model/Obstacle.java
└── pom.xml

TOTAL: 24 Java files + 3 documentation files
```

---

## Build and Package

```bash
# Clean and compile
mvn clean compile

# Run Phase 2 REPL
mvn exec:java -Dexec.mainClass="ca.umanitoba.cs.abdullmm.ui.MainRepl"

# Or package and run
mvn clean package
java -cp target/classes ca.umanitoba.cs.abdullmm.ui.MainRepl

# Check for errors
mvn clean compile 2>&1 | grep -i error
```

---

## Common Issues and Solutions

### Issue: "Cannot resolve symbol" for exceptions
**Solution**: Ensure all exception files are in exceptions/ package and imports are correct.

### Issue: MainRepl.run() shows compilation errors
**Solution**: Check that all custom exceptions are imported at top of MainRepl.java.

### Issue: PathFinder doesn't find obvious routes
**Solution**: Verify points are adjacent (differ by 1 in X or Y only, not diagonally).

### Issue: No map displayed
**Solution**: Map is initialized on startup. If null errors occur, check Dimension validation.

### Issue: Activities not showing in feed after following
**Solution**: Make sure to follow BEFORE signing out. Follow relationships are user-specific.

---

## Design Patterns Used

### MVC-like Layering
- Model: Domain classes (Activity, Route, UserProfile, etc.)
- View: MainRepl (console-based REPL)
- Controller: Managers (UserManager, ActivityManager, PathFinder)

### Strategy Pattern
- Stack interface with LinkedStack implementation
- Gear interface with Bike, Helmet, Shoe implementations

### Exception Handling Strategy
- Custom exceptions for business errors
- Guava Preconditions for contract violations
- Layer-appropriate validation

### Design by Contract
- Preconditions at method entry
- Postconditions at method exit
- Invariants at object lifecycle

---

## Performance Considerations

### Pathfinding Algorithm
- Time: O(V + E) where V = points, E = adjacencies
- Space: O(V) for visited set and stack
- Terminates in finite time due to visited set tracking

### Collections
- ArrayList for dynamic collections (good for small datasets)
- HashMap for user profile map (O(1) lookup)

### No Performance Issues Expected
- Data sets are small (test data)
- No nested loops or quadratic algorithms
- Stack ADT efficient for DFS

---

## Security Notes

### Not Implemented (Out of Scope)
- Password hashing/authentication
- Permission checking
- Input sanitization
- SQL injection prevention (no database)
- XSS prevention (no web interface)

### Data Integrity
- Guava Preconditions ensure data consistency
- No external file I/O or network
- All data in-memory (lost on shutdown)

---

## Future Enhancements

1. **Persistence**: Save/load profiles from files or database
2. **Advanced Pathfinding**: Dijkstra's algorithm, A* search
3. **Visualization**: GUI map display with routes
4. **Statistics**: Activity analytics, distance/time tracking
5. **Achievements**: Badges for milestones
6. **Export**: Save activities to CSV/JSON
7. **Notifications**: Follow request system
8. **Search**: Find users, activities, routes

