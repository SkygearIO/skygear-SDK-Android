package io.skygear.skygear;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * The Skygear Access Control.
 */
public class AccessControl {
    static AccessControl defaultAccessControl = null;

    /**
     * The Public Access Entry Queue.
     */
    final Queue<Entry> publicEntryQueue;

    /**
     * The User-based Access Entry Map.
     */
    final Map<String, Queue<Entry>> userEntryMap;

    /**
     * Gets the default access control access control.
     *
     * @return the access control
     */
    static AccessControl defaultAccessControl() {
        AccessControl defaultAccessControl = AccessControl.defaultAccessControl;
        if (defaultAccessControl == null) {
            defaultAccessControl = new AccessControl(new Entry[]{
                    new Entry(Level.READ_ONLY)
            });
        }

        return defaultAccessControl;
    }

    /**
     * Instantiates a new Skygear Access Control.
     */
    public AccessControl() {
        super();

        this.publicEntryQueue = new EntryQueue(Entry.Type.PUBLIC);
        this.userEntryMap = new HashMap<>();

        // TODO: add data structure role-based ACEs
    }

    /**
     * Instantiates a new Skygear Access Control.
     *
     * @param entries the entry array
     */
    public AccessControl(Entry[] entries) {
        this();

        for (Entry perEntry : entries) {
            this.addEntry(perEntry);
        }
    }

    /**
     * Adds an access entry to the access control
     *
     * <p>
     * This method return the access control itself, in order to chain up different methods
     * </p>
     *
     * @param entry the entry
     * @return the access control itself
     */
    public AccessControl addEntry(Entry entry) {
        Entry.Type type = entry.getType();

        if (type == Entry.Type.PUBLIC) {
            this.publicEntryQueue.add(entry);
        } else if (type == Entry.Type.USER_BASED) {
            String userId = entry.getUserId();
            Queue<Entry> entryQueue = this.userEntryMap.get(userId);
            if (entryQueue == null) {
                entryQueue = new EntryQueue(Entry.Type.USER_BASED);
            }

            entryQueue.add(entry);
            this.userEntryMap.put(userId, entryQueue);
        }

        // TODO: support role-based ACEs

        return this;
    }

    /**
     * Removes all entries of a specific type.
     *
     * <p>
     * This method return the access control itself, in order to chain up different methods
     * </p>
     *
     * @param type the type
     * @return the access control itself
     */
    public AccessControl clearEntries(Entry.Type type) {
        if (type == Entry.Type.PUBLIC) {
            this.publicEntryQueue.clear();
        } else if (type == Entry.Type.USER_BASED) {
            this.userEntryMap.clear();
        }

        // TODO: support role-based ACEs

        return this;
    }

    /**
     * Removes all user-based entries for a specific user id.
     *
     * <p>
     * This method return the access control itself, in order to chain up different methods
     * </p>
     *
     * @param userId the user id
     * @return the access control
     */
    public AccessControl clearEntries(String userId) {
        this.userEntryMap.remove(userId);
        return this;
    }

    /**
     * Removes all entries.
     *
     * <p>
     * This method return the access control itself, in order to chain up different methods
     * </p>
     *
     * @return the access control itself
     */
    public AccessControl clearEntries() {
        // TODO: support role-based ACEs

        return this.clearEntries(Entry.Type.PUBLIC)
                .clearEntries(Entry.Type.USER_BASED);
    }

    /**
     * Removes an entry.
     *
     * <p>
     * This method return the access control itself, in order to chain up different methods
     * </p>
     *
     * @param entry the entry
     * @return the access control itself
     */
    public AccessControl removeEntry(Entry entry) {
        Entry.Type type = entry.getType();

        if (type == Entry.Type.PUBLIC) {
            this.publicEntryQueue.remove(entry);
        } else if (type == Entry.Type.USER_BASED) {
            String userId = entry.getUserId();
            Queue<Entry> entryQueue = this.userEntryMap.get(userId);
            if (entryQueue != null) {
                entryQueue.remove(entry);
                this.userEntryMap.put(userId, entryQueue);
            }
        }

        // TODO: support role-based ACEs

        return this;
    }

    /**
     * Gets the public access control entry.
     *
     * @return the public access control entry
     */
    public Entry getPublicAccess() {
        if (this.publicEntryQueue.size() == 0) {
            return new Entry(Level.NO_ACCESS);
        }

        return this.publicEntryQueue.peek();
    }

    /**
     * Gets access for a user.
     *
     * @param user the user
     * @return the access
     */
    public Entry getAccess(User user) {
        return this.getAccess(user.getId());
    }

    /**
     * Gets access for a user id.
     *
     * @param userId the user id
     * @return the access
     */
    public Entry getAccess(String userId) {
        Queue<Entry> entryQueue = this.userEntryMap.get(userId);
        if (entryQueue == null || entryQueue.size() == 0) {
            return new Entry(userId, Level.NO_ACCESS);
        }

        return entryQueue.peek();
    }

    /**
     * The Skygear Access Control Entry Queue.
     *
     * <p>
     * EntryQueue is a {@link PriorityQueue} for {@link Entry}, which will provide the
     * highest access level entry at {@link #peek()}.
     * </p>
     */
    static class EntryQueue extends PriorityQueue<Entry> {
        private static final int ENTRY_QUEUE_DEFAULT_SIZE = 5;
        private final Entry.Type type;

        public EntryQueue(Entry.Type type) {
            this(type, ENTRY_QUEUE_DEFAULT_SIZE);
        }

        public EntryQueue(Entry.Type type, int initialCapacity) {
            super(initialCapacity, Collections.<Entry>reverseOrder());

            this.type = type;
        }

        @Override
        public boolean offer(Entry o) {
            if (o.getType() != this.type) {
                throw new InvalidParameterException("Entry type conflict.");
            }
            return super.offer(o);
        }
    }

    /**
     * The Skygear Access Control Entry.
     */
    static class Entry implements Comparable<Entry> {
        private boolean isPublic = false;
        private Level level = null;
        private String userId = null;

        // TODO: support role-based ACEs

        /**
         * Instantiates a new Public Access Control Entry.
         *
         * @param level    the level
         */
        public Entry(Level level) {
            super();

            this.isPublic = true;
            this.level = level;
        }


        /**
         * Instantiates a new User-based Access Control Entry.
         *
         * @param userId the user id
         * @param level  the level
         */
        public Entry(String userId, Level level) {
            super();

            this.userId = userId;
            this.level = level;
        }

        /**
         * Instantiates a new User-based Access Control Entry.
         *
         * @param user  the user
         * @param level the level
         */
        public Entry(User user, Level level) {
            this(user.getId(), level);
        }

        /**
         * Checks whether it is a public access control entry.
         *
         * @return the boolean indicating whether it is a public access control entry.
         */
        public boolean isPublic() {
            return isPublic;
        }

        /**
         * Gets user id.
         *
         * @return the user id
         */
        public String getUserId() {
            return userId;
        }

        /**
         * Gets the access level.
         *
         * @return the access level
         */
        public Level getLevel() {
            return level;
        }

        /**
         * Gets the access control entry type.
         *
         * @return the type
         */
        Type getType() {
            if (this.isPublic()) {
                return Type.PUBLIC;
            } else if (this.getUserId() != null) {
                return Type.USER_BASED;
            }

            // TODO: support role-based ACEs

            throw new IllegalStateException("Unknown Access Control Entry Type");
        }

        @Override
        public int compareTo(Entry another) {
            if (another == null) {
                return 1;
            }

            Type entryType = this.getType();

            if (entryType != another.getType()) {
                throw new InvalidParameterException(
                        "Access Control Entry with different types cannot be compared"
                );
            }

            if (entryType == Type.USER_BASED && !this.getUserId().equals(another.getUserId())) {
                throw new InvalidParameterException(
                        "User-based Access Control Entry with different user IDs cannot be compared"
                );
            }

            // TODO: support role-based ACE

            return this.getLevel().compareTo(another.getLevel());
        }

        @Override
        public boolean equals(Object another) {
            if (another == null || !(another instanceof Entry)) {
                return false;
            }

            try {
                return this.compareTo((Entry) another) == 0;
            } catch (InvalidParameterException e) {
                return false;
            }
        }

        /**
         * The Skygear Access Control Entry Type.
         */
        enum Type {
            /**
             * the Public type.
             */
            PUBLIC,
            USER_BASED

            // TODO: support role-based ACEs
        }
    }

    /**
     * The Skygear Access Level.
     */
    enum Level {
        /**
         * No access level.
         */
        NO_ACCESS,
        /**
         * the Read-only level.
         */
        READ_ONLY,
        /**
         * the Read-write level.
         */
        READ_WRITE
    }
}
