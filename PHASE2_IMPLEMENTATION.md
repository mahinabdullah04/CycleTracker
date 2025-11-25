# Phase 2 Implementation Summary

## Overview
Successfully implemented Phase 2 of the CycleTracker project with multi-user support, social features, pathfinding algorithm, and proper layered architecture following SOLID principles and design by contract.

## Files Created (24 total Java files)

### Exception Layer (5 files)
1. **InvalidUserException.java** - User-related operation failures
2. **DuplicateUserException.java** - Duplicate user creation attempts
3. **InvalidActivityException.java** - Activity creation/access failures
4. **InvalidRouteException.java** - Route operation failures
5. **PathNotFoundException.java** - Pathfinding algorithm failures

### Model Layer (9 files - Enhanced from Phase 1)
1. **Stack.java** (NEW) - Generic stack interface with push/pop/peek/isEmpty
2. **LinkedStack.java** (NEW) - Linked-list implementation of Stack with proper invariants
3. **UserProfile.java** (NEW) - User's gear, activities, and followed users
4. **ActivityFeed.java** (NEW) - Aggregates own and followed users' activities
5. **Activity.java** (UPDATED) - Added preconditions, postconditions, invariants
6. **Route.java** (UPDATED) - Changed getDistance() return type from int to double
7. **Bike.java** (ENHANCED) - Added Guava precondition checks
8. **Helmet.java** (ENHANCED) - Added Guava precondition checks, fixed formatting
9. **Shoe.java** (ENHANCED) - Added Guava precondition checks, fixed type syntax error
10. **Dimension.java** (UNCHANGED)
11. **GridPoint.java** (UNCHANGED)
12. **Obstacle.java** (UNCHANGED)
13. **Map.java** (UNCHANGED)
14. **Gear.java** (UNCHANGED)

### Logic Layer (3 files - NEW)
1. **UserManager.java** - Creates, retrieves, and manages user profiles
   - Methods: createUser(), getUser(), userExists(), getAllUserIds(), getAllProfiles(), getUserCount()
   - Validates: No duplicate users, valid user IDs

2. **ActivityManager.java** - Manages activity creation and feeds
   - Methods: createActivity(), getUserActivities(), duplicateRouteActivity(), getActivityFeed()
   - Validates: User exists, activity parameters valid, index bounds

3. **PathFinder.java** - Implements DFS-based pathfinding using Stack ADT
   - Methods: findPathUserOnly(), findPathFromFeed(), findPathDFS(), isAdjacent(), reconstructPath()
   - Algorithm: Depth-first search with Stack, finds path between grid points using available routes

### UI Layer (1 file - NEW)
1. **MainRepl.java** - Main REPL interface
   - Authentication: Create profile, sign in
   - Features: View feed, add activity, manage gear, follow users, find routes, view map, add obstacles
   - Validation: Input format, menu selection, numeric ranges
   - Hardcoded world map: 50x50 grid initialized at startup

### Existing Files (PRESERVED)
1. **Main.java** - Original Phase 1 REPL (not modified per requirements)

---

## Key Implementation Details

### Design by Contract
- **Preconditions**: Validated in each layer appropriately
  - UI Layer: Input format validation (non-empty strings, numeric ranges)
  - Logic Layer: Business rule validation (duplicate users, user existence)
  - Model Layer: Class invariant validation using Guava Preconditions
- **Postconditions**: State changes verified after operations
- **Invariants**: Class-level constraints maintained throughout object lifecycle

### Layered Architecture
```
UI Layer (MainRepl) 
    ↓ (delegates to)
Logic Layer (UserManager, ActivityManager, PathFinder)
    ↓ (uses)
Model Layer (UserProfile, Activity, Route, etc.)
    ↓ (throws)
Exceptions Layer (custom exceptions)
```

### Single Responsibility Principle
- **MainRepl**: UI interaction and I/O only
- **UserManager**: User profile lifecycle management
- **ActivityManager**: Activity creation and feed management
- **PathFinder**: Pathfinding algorithm implementation
- **UserProfile**: User data storage and state management
- **Stack/LinkedStack**: Generic stack data structure

### Input Validation Strategy
- **UI Layer** (MainRepl):
  - String emptiness validation
  - Menu choice range validation
  - Numeric input range validation
  - Date format parsing with fallback to today

- **Logic Layer** (Managers):
  - User existence validation
  - Duplicate user detection
  - Activity index bounds checking
  - User ID validation

- **Model Layer** (Classes):
  - Null checks on all parameters
  - Invariant assertions using Guava Preconditions
  - Empty collection validation

### Pathfinding Algorithm
Uses Stack ADT for depth-first search:
1. Initialize stack with start point
2. Mark start as visited, add parent mapping
3. While stack not empty:
   - Pop current point
   - If current == end, reconstruct and return path
   - For each adjacent unvisited point in available set:
     - Mark as visited
     - Record parent
     - Push to stack
4. If stack empties without finding end, throw PathNotFoundException
5. Adjacent definition: points differ by exactly 1 in X or Y coordinate (4-connected grid)

### Error Handling Flow
```
User Input (MainRepl)
    ↓
Format Validation (MainRepl throws no exception on format errors, re-prompts)
    ↓
Logic Layer Call (ActivityManager/UserManager)
    ↓
Business Rule Validation (throws custom exceptions)
    ↓
Model Layer (throws IllegalStateException/IllegalArgumentException from Preconditions)
    ↓
Catch in Logic Layer (convert to custom exception)
    ↓
Catch in MainRepl (display error message, return to menu)
```

---

## Feature Implementation Details

### 1. User Authentication
- Create new profile: Username must be unique and non-empty
- Sign in: Username must exist
- Sign out: Available from main menu
- Error handling: Duplicate user exception, invalid user exception

### 2. Activity Management
- Add activity: Select or create route, choose gear, set duration, date (optional)
- Duplicate previous route: Reuse route from user's activity history
- View activities: Display in feed with stats
- Store in UserProfile.activities collection

### 3. Activity Feed
- Own activities: From current user's UserProfile
- Followed activities: From all users in currentUser.followedUserIds
- Aggregated in ActivityFeed class
- Methods to filter own vs. followed activities

### 4. Follow System
- Follow users: Add to UserProfile.followedUserIds
- Unfollow users: Remove from UserProfile.followedUserIds
- View followed users: List all followed user IDs
- Prevent self-follow: Validation in UI

### 5. Gear Management
- Add gear: Create Bike, Helmet, or Shoe with validation
- Remove gear: Delete from UserProfile.gearInventory
- List gear: Display all owned gear with descriptions
- Associate with activities: Select gear when creating activity

### 6. Route Finding (Pathfinding)
- Scope 1: "My routes only" - uses only current user's activities
- Scope 2: "My feed" - uses current user + all followed users' activities
- Input: Start (X,Y) and End (X,Y) coordinates
- Output: Route object with points representing path
- Uses Stack ADT for DFS traversal
- Error: Throw PathNotFoundException if no path exists

### 7. Map Management
- Hardcoded world map: 50x50 grid initialized at startup
- Add obstacles: Name, position (X,Y), size (width, height)
- View map: ASCII display with obstacles marked as '*'
- Obstacle validation: Position and size constraints

---

## Class Invariants Documented

### UserProfile
- userId: not null, not empty
- gearInventory: not null, may be empty
- activities: not null, may be empty
- followedUserIds: not null, may be empty

### LinkedStack<E>
- size >= 0
- (size == 0) implies (top == null)
- (size > 0) implies (top != null)
- LIFO ordering maintained

### ActivityFeed
- currentUserId: not null, not empty
- userProfiles: not null

### Route
- points: not null
- name: not null, not empty
- Activity class validates non-empty points

### Activity
- route: not null with at least 1 point
- gearUsed: not null
- date: not null
- distance > 0
- durationMinutes > 0
- name: not null, not empty

### Bike, Helmet, Shoe
- name: not null, not empty
- All type-specific fields: not null, not empty

---

## Compliance with Phase 2 Requirements

### ✅ Multi-User Support
- UserManager creates and manages user profiles
- MainRepl authentication menu for profile creation and sign-in
- Each user has independent gear, activities, and following list

### ✅ Profile Management
- UserProfile class stores personal data
- Gear management through UI
- Activity history in UserProfile.activities

### ✅ Activity Feed
- ActivityFeed aggregates own and followed users' activities
- MainRepl displays feed with activity details
- Follow/unfollow functionality implemented

### ✅ Add Activities
- MainRepl activity creation flow
- Route selection (new or duplicated)
- Gear selection from personal inventory
- Date and duration input
- Full validation before creation

### ✅ Route Duplication
- Select from previous activities
- Copy route to new activity
- User can only duplicate their own routes

### ✅ Route Finding with Pathfinding
- Two search scopes implemented
- Stack-based DFS algorithm in PathFinder
- Adjacent point calculation (4-connected grid)
- Error handling for no path found

### ✅ Hardcoded World Map
- 50x50 grid created at startup
- Obstacle management (add/remove)
- Map visualization with ASCII display

### ✅ Layered Architecture
- UI: MainRepl in ui package
- Logic: Managers in logic package
- Model: Domain classes in model package
- Exceptions: Custom exceptions in exceptions package

### ✅ Error Handling
- Custom exception types for different errors
- Input validation at each layer
- User-friendly error messages
- No stack traces exposed to users (Preconditions errors caught)

### ✅ Design by Contract
- Preconditions documented in method javadoc
- Postconditions documented in method javadoc
- Invariants documented in class javadoc
- Guava Preconditions used throughout

### ✅ Single Responsibility Principle
- Each class has one reason to change
- UI handles I/O only
- Logic handles business rules
- Model handles state
- Exceptions represent different error types

---

## Testing Recommendations

### Manual Testing Flow
1. **Create Profiles**: Create 2-3 test users
2. **Add Gear**: Each user adds bikes/helmets/shoes
3. **Create Routes**: Create routes with multiple points
4. **Add Activities**: Create activities for each user with different gear/routes
5. **Follow Users**: Have one user follow another
6. **View Feed**: Verify feed shows own and followed activities
7. **Route Finding**: 
   - Find route in user-only scope (should find routes)
   - Find route in feed scope (should find more routes)
   - Find route between non-existent points (should error appropriately)
8. **Error Scenarios**:
   - Try to create duplicate user
   - Try to sign in with non-existent user
   - Try to add activity without gear
   - Try to find route with invalid coordinates

### Validation Points
- All error messages are clear and actionable
- No stack traces shown to users
- Invalid input doesn't crash application
- State changes properly propagate (follow -> feed updates)
- Pathfinding algorithm terminates (no infinite loops)

---

## Project Structure

```
CycleTracker/
├── pom.xml (with Guava dependency)
├── README.md (comprehensive documentation with Mermaid diagrams)
├── src/
│   └── main/
│       ├── java/
│       │   └── ca/umanitoba/cs/abdullmm/
│       │       ├── Main.java (Phase 1 REPL, preserved)
│       │       ├── ui/
│       │       │   └── MainRepl.java (Phase 2 REPL)
│       │       ├── logic/
│       │       │   ├── UserManager.java
│       │       │   ├── ActivityManager.java
│       │       │   └── PathFinder.java
│       │       ├── model/
│       │       │   ├── Stack.java (interface)
│       │       │   ├── LinkedStack.java (implementation)
│       │       │   ├── UserProfile.java
│       │       │   ├── ActivityFeed.java
│       │       │   ├── Activity.java
│       │       │   ├── Route.java
│       │       │   ├── Bike.java
│       │       │   ├── Helmet.java
│       │       │   ├── Shoe.java
│       │       │   ├── Gear.java (interface)
│       │       │   ├── Dimension.java
│       │       │   ├── GridPoint.java
│       │       │   ├── Obstacle.java
│       │       │   └── Map.java
│       │       └── exceptions/
│       │           ├── InvalidUserException.java
│       │           ├── DuplicateUserException.java
│       │           ├── InvalidActivityException.java
│       │           ├── InvalidRouteException.java
│       │           └── PathNotFoundException.java
│       └── resources/
└── target/
```

---

## Compilation and Execution

### Compile
```bash
mvn clean compile
```

### Run Phase 2 Application
```bash
mvn exec:java -Dexec.mainClass="ca.umanitoba.cs.abdullmm.ui.MainRepl"
```

### Or Package and Run
```bash
mvn clean package
java -cp target/classes ca.umanitoba.cs.abdullmm.ui.MainRepl
```

### Phase 1 REPL (Still Available)
```bash
java -cp target/classes ca.umanitoba.cs.abdullmm.Main
```

---

## Summary of Improvements

### From Phase 1 to Phase 2
1. **Multi-user system**: Single user → UserProfile per user with UserManager
2. **Social features**: Isolated activities → ActivityFeed with following system
3. **Intelligent routing**: Manual route creation → Pathfinding with Stack ADT
4. **Better architecture**: Monolithic Main.java → Layered UI/Logic/Model/Exceptions
5. **Robust error handling**: Basic try-catch → Custom exceptions with validation strategy
6. **Enhanced validation**: Minimal checks → Design by contract with Guava Preconditions
7. **Code organization**: Single file UI → Dedicated MainRepl class
8. **Maintainability**: Tight coupling → Clear separation of concerns

---

## Notes

- The original Main.java is preserved as per requirements (grading staff will not interact with it)
- MainRepl is the new entry point for Phase 2
- All features follow the "happy path" flow with error handling
- Hardcoded map is initialized at startup (no map selection needed)
- All public APIs include comprehensive javadoc with contracts
- Project uses Java 17+ features (records, sealed classes compatibility)
- Guava library (v33.5.0) used for preconditions
- No external UI frameworks used (console-based REPL)

