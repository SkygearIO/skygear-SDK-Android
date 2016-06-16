package io.skygear.skygear;

import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

/**
 * The Skygear Database.
 *
 * This class wraps the logic of public / private database concept in Skygear.
 * Also, all record CRUD should be achieved via this Class.
 *
 */
public class Database {
    private static final String PUBLIC_DATABASE_NAME = "_public";
    private static final String PRIVATE_DATABASE_NAME = "_private";
    private static final List<String> AvailableDatabaseNames
            = Arrays.asList(PUBLIC_DATABASE_NAME, PRIVATE_DATABASE_NAME);

    private String name;
    private WeakReference<Container> containerRef;

    /**
     * Instantiates a public database.
     *
     * Please be reminded that the skygear container passed in would be weakly referenced.
     *
     * @param container the skygear container
     * @return the database
     */
    public static Database publicDatabase(Container container) {
        return new Database(PUBLIC_DATABASE_NAME, container);
    }

    /**
     * Instantiates a private database.
     *
     * Please be reminded that the skygear container passed in would be weakly referenced.
     *
     * @param container the skygear container
     * @return the database
     */
    public static Database privateDatabase(Container container) {
        return new Database(PRIVATE_DATABASE_NAME, container);
    }

    /**
     * Instantiates a new Database.
     *
     * Please be reminded that the skygear container passed in would be weakly referenced.
     *
     * @param databaseName the database name
     * @param container  the container
     */
    Database(String databaseName, Container container) {
        super();

        if (!Database.AvailableDatabaseNames.contains(databaseName)) {
            throw new InvalidParameterException("Invalid database name");
        }

        this.name = databaseName;
        this.containerRef = new WeakReference<>(container);
    }

    /**
     * Gets container.
     *
     * @return the container
     */
    public Container getContainer() {
        Container container = this.containerRef.get();
        if (container == null) {
            throw new InvalidParameterException("Missing container for database");
        }

        return container;
    }

    /**
     * Gets the database name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    public void save(Record record, RecordSaveResponseHandler handler) {
        this.save(new Record[]{ record }, handler);
    }

    public void save(Record[] records, RecordSaveResponseHandler handler) {
        RecordSaveRequest request = new RecordSaveRequest(records, this);
        request.responseHandler = handler;

        this.getContainer().sendRequest(request);
    }
}
