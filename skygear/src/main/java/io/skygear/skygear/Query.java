package io.skygear.skygear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;


/**
 * The Skygear Record Query.
 */
public class Query {
    private final String type;
    private final List<JSONArray> predicates;
    private final JSONArray sortPredicate;
    private final JSONObject transientPredicate;

    private boolean negation;

    /**
     * Instantiates a new Query.
     *
     * @param type record type
     */
    public Query(String type) {
        super();
        this.type = type;
        this.negation = false;
        this.predicates = new LinkedList<>();
        this.sortPredicate = new JSONArray();
        this.transientPredicate = new JSONObject();
    }

    /**
     * Set like predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key   the key
     * @param value the value
     * @return the query
     */
    public Query like(String key, Object value) {
        try {
            this.predicates.add(
                    QueryPredicate.basicPredicate(
                            QueryPredicate.keypathRepresentation(key),
                            "like",
                            value
                    )
            );
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, value.toString())
            );
        }
        return this;
    }

    /**
     * Set not like predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key   the key
     * @param value the value
     * @return the query
     */
    public Query notLike(String key, Object value) {
        try {
            this.predicates.add(QueryPredicate.not(
                    QueryPredicate.basicPredicate(
                            QueryPredicate.keypathRepresentation(key),
                            "like",
                            value
                    )
            ));
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, value.toString())
            );
        }
        return this;
    }

    /**
     * Set case insensitive like predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key   the key
     * @param value the value
     * @return the query
     */
    public Query caseInsensitiveLike(String key, Object value) {
        try {
            this.predicates.add(
                    QueryPredicate.basicPredicate(
                            QueryPredicate.keypathRepresentation(key),
                            "ilike",
                            value
                    )
            );
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, value.toString())
            );
        }
        return this;
    }

    /**
     * Set case insensitive not like predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key   the key
     * @param value the value
     * @return the query
     */
    public Query caseInsensitiveNotLike(String key, Object value) {
        try {
            this.predicates.add(QueryPredicate.not(
                    QueryPredicate.basicPredicate(
                            QueryPredicate.keypathRepresentation(key),
                            "ilike",
                            value
                    )
            ));
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, value.toString())
            );
        }
        return this;
    }

    /**
     * Set equal to predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key   the key
     * @param value the value
     * @return the query
     */
    public Query equalTo(String key, Object value) {
        try {
            this.predicates.add(
                    QueryPredicate.basicPredicate(
                            QueryPredicate.keypathRepresentation(key),
                            "eq",
                            value
                    )
            );
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, value.toString())
            );
        }
        return this;
    }

    /**
     * Set not equal to predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key   the key
     * @param value the value
     * @return the query
     */
    public Query notEqualTo(String key, Object value) {
        try {
            this.predicates.add(
                    QueryPredicate.basicPredicate(
                            QueryPredicate.keypathRepresentation(key),
                            "neq",
                            value
                    )
            );
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, value.toString())
            );
        }
        return this;
    }

    /**
     * Set greater than predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key   the key
     * @param value the value
     * @return the query
     */
    public Query greaterThan(String key, Object value) {
        try {
            this.predicates.add(
                    QueryPredicate.basicPredicate(
                            QueryPredicate.keypathRepresentation(key),
                            "gt",
                            value
                    )
            );
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, value.toString())
            );
        }
        return this;
    }

    /**
     * Set greater than or equal to predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key   the key
     * @param value the value
     * @return the query
     */
    public Query greaterThanOrEqualTo(String key, Object value) {
        try {
            this.predicates.add(
                    QueryPredicate.basicPredicate(
                            QueryPredicate.keypathRepresentation(key),
                            "gte",
                            value
                    )
            );
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, value.toString())
            );
        }
        return this;
    }

    /**
     * Set less than predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key   the key
     * @param value the value
     * @return the query
     */
    public Query lessThan(String key, Object value) {
        try {
            this.predicates.add(
                    QueryPredicate.basicPredicate(
                            QueryPredicate.keypathRepresentation(key),
                            "lt",
                            value
                    )
            );
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, value.toString())
            );
        }
        return this;
    }

    /**
     * Set less than or equal to predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key   the key
     * @param value the value
     * @return the query
     */
    public Query lessThanOrEqualTo(String key, Object value) {
        try {
            this.predicates.add(
                    QueryPredicate.basicPredicate(
                            QueryPredicate.keypathRepresentation(key),
                            "lte",
                            value
                    )
            );
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, value.toString())
            );
        }
        return this;
    }

    /**
     * Set contains predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key     the key
     * @param lookups the list of lookup objects
     * @return the query
     */
    public Query contains(String key, List lookups) {
        try {
            this.predicates.add(
                    QueryPredicate.basicPredicate(
                            QueryPredicate.keypathRepresentation(key),
                            "in",
                            new JSONArray(lookups)
                    )
            );
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, lookups.toString())
            );
        }
        return this;
    }

    /**
     * set not contains predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key     the key
     * @param lookups the list of lookup objects
     * @return the query
     */
    public Query notContains(String key, List lookups) {
        try {
            this.predicates.add(QueryPredicate.not(
                    QueryPredicate.basicPredicate(
                            QueryPredicate.keypathRepresentation(key),
                            "in",
                            new JSONArray(lookups)
                    )
            ));
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, lookups.toString())
            );
        }
        return this;
    }

    /**
     * Set contains value predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key    the key
     * @param needle the needle
     * @return the query
     */
    public Query containsValue(String key, Object needle) {
        try {
            this.predicates.add(
                    QueryPredicate.basicPredicate(
                            needle,
                            "in",
                            QueryPredicate.keypathRepresentation(key)
                    )
            );
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, needle.toString())
            );
        }
        return this;
    }

    /**
     * Set not contains value predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key    the key
     * @param needle the needle
     * @return the query
     */
    public Query notContainsValue(String key, Object needle) {
        try {
            this.predicates.add(QueryPredicate.not(
                    QueryPredicate.basicPredicate(
                            needle,
                            "in",
                            QueryPredicate.keypathRepresentation(key)
                    )
            ));
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build query predicate for parameter: %s, %s", key, needle.toString())
            );
        }
        return this;
    }

    /**
     * Set negate predicate.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @return the query
     */
    public Query negate() {
        this.negation = !this.negation;

        return this;
    }

    /**
     * Add sort descending key.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key   the key
     * @return the query
     */
    public Query addDescending(String key) {
        try {
            JSONArray sortPredicate = new JSONArray();
            sortPredicate.put(QueryPredicate.keypathRepresentation(key));
            sortPredicate.put("desc");

            this.sortPredicate.put(sortPredicate);
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build sort predicate for key: %s", key)
            );
        }

        return this;
    }

    /**
     * Add sort ascending key.
     *
     * <p> This method returns the query itself, for chaining different predicate methods </p>
     *
     * @param key   the key
     * @return the query
     */
    public Query addAscending(String key) {
        try {
            JSONArray sortPredicate = new JSONArray();
            sortPredicate.put(QueryPredicate.keypathRepresentation(key));
            sortPredicate.put("asc");

            this.sortPredicate.put(sortPredicate);
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build sort predicate for key: %s", key)
            );
        }

        return this;
    }

    /**
     * Transient include record reference.
     *
     * @param key the key
     * @return the query
     */
    public Query transientInclude(String key) {
        return this.transientInclude(key, key);
    }

    /**
     * Transient include record reference.
     *
     * @param key        the key
     * @param mappingKey the mapping key
     * @return the query
     */
    public Query transientInclude(String key, String mappingKey) {
        try {
            this.transientPredicate.put(
                    mappingKey,
                    QueryPredicate.keypathRepresentation(key)
            );
        } catch (JSONException e) {
            throw new InvalidParameterException(
                    String.format("Cannot build transient predicate for key: %s => %s", mappingKey, key)
            );
        }

        return this;
    }

    /**
     * Or query.
     *
     * @param queries the queries
     * @return the query
     */
    public static Query or(Query... queries) {
        if (queries.length == 0) {
            throw new InvalidParameterException("No queries to be processed");
        }

        String type = queries[0].type;
        Query newQuery = new Query(type);

        JSONArray predicate;
        if (queries.length == 1) {
            predicate = queries[0].getPredicateJson();
        } else {
            predicate = new JSONArray();
            predicate.put("or");

            for (Query perQuery : queries) {
                if (!perQuery.getType().equals(type)) {
                    throw new InvalidParameterException("All queries must be in the same type.");
                }

                predicate.put(perQuery.getPredicateJson());
            }
        }

        newQuery.predicates.add(predicate);

        return newQuery;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets predicate json.
     *
     * @return the predicate json
     */
    public JSONArray getPredicateJson() {
        JSONArray predicateJson = new JSONArray();

        if (this.predicates.size() == 1) {
            predicateJson = this.predicates.get(0);
        } else if (this.predicates.size() > 1) {
            predicateJson.put("and");
            for (JSONArray perPredicate : this.predicates) {
                predicateJson.put(perPredicate);
            }
        }

        if (!this.negation || predicateJson.length() == 0) {
            return predicateJson;
        } else {
            JSONArray finalPredicate = new JSONArray();
            finalPredicate.put("not");
            finalPredicate.put(predicateJson);

            return finalPredicate;
        }
    }

    /**
     * Gets sort predicate json.
     *
     * @return the sort predicate json
     */
    public JSONArray getSortPredicateJson() {
        return this.sortPredicate;
    }

    /**
     * Gets transient predicate json.
     *
     * @return the transient predicate json
     */
    public JSONObject getTransientPredicateJson() {
        return this.transientPredicate;
    }
}
