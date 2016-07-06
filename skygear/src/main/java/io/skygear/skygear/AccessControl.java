package io.skygear.skygear;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * The Skygear Access Control.
 */
public class AccessControl {
    private static final int ENTRY_QUEUE_DEFAULT_SIZE = 5;

    /**
     * The Public Access Entry Queue.
     */
    final Queue<Entry> publicEntryQueue;

    /**
     * Gets the default access control access control.
     *
     * @return the access control
     */
    static AccessControl defaultAccessControl() {

        // TODO: get default ACL from persistent store

        return new AccessControl(new Entry[]{
                new Entry(Level.READ_ONLY)
        });
    }

    /**
     * Instantiates a new Skygear Access Control.
     */
    public AccessControl() {
        super();

        this.publicEntryQueue = new PriorityQueue<>(
                ENTRY_QUEUE_DEFAULT_SIZE,
                Collections.<Entry>reverseOrder()
        );

        // TODO: add data structure role-based ACEs
        // TODO: add data structure user-based ACEs
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
        }

        // TODO: support role-based ACEs
        // TODO: support user-based ACEs

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
        }

        // TODO: support role-based ACEs
        // TODO: support user-based ACEs

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
        // TODO: support user-based ACEs

        return this.clearEntries(Entry.Type.PUBLIC);
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
        }

        // TODO: support role-based ACEs
        // TODO: support user-based ACEs

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
     * The Skygear Access Control Entry.
     */
    static class Entry implements Comparable<Entry> {
        private boolean isPublic = false;
        private Level level = null;

        // TODO: support role-based ACEs
        // TODO: support user-based ACEs

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
         * Checks whether it is a public access control entry.
         *
         * @return the boolean indicating whether it is a public access control entry.
         */
        public boolean isPublic() {
            return isPublic;
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
            }

            // TODO: support role-based ACEs
            // TODO: support user-based ACEs

            throw new IllegalStateException("Unknown Access Control Entry Type");
        }

        @Override
        public int compareTo(Entry another) {
            if (another == null) {
                return 1;
            }

            if (this.getType() != another.getType()) {
                throw new InvalidParameterException(
                        "Access Control Entry with different types cannot be compared"
                );
            }

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
            PUBLIC

            // TODO: support role-based ACEs
            // TODO: support user-based ACEs
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
