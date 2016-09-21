package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class QueryUnitTest {

    @Test
    public void testQueryCreationNormalFlow() throws Exception {
        Query noteQuery = new Query("Note")
                .equalTo("title", "Hello world")
                .addAscending("rating");

        assertEquals("Note", noteQuery.getType());

        JSONArray predicate = noteQuery.getPredicateJson();

        assertEquals("eq", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("title", predicateKeypath.getString("$val"));

        assertEquals("Hello world", predicate.getString(2));

        JSONArray sortPredicate = noteQuery.getSortPredicateJson();
        JSONArray sortPredicate1 = sortPredicate.getJSONArray(0);
        JSONObject sortPredicate1KeyPath = sortPredicate1.getJSONObject(0);

        assertEquals("keypath", sortPredicate1KeyPath.getString("$type"));
        assertEquals("rating", sortPredicate1KeyPath.getString("$val"));

        assertEquals("asc", sortPredicate1.getString(1));
    }

    @Test
    public void testLikeQuery() throws Exception {
        Query noteQuery = new Query("Note")
                .like("title", "Hello");

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("like", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("title", predicateKeypath.getString("$val"));

        assertEquals("Hello", predicate.getString(2));
    }

    @Test
    public void testNotLikeQuery() throws Exception {
        Query noteQuery = new Query("Note")
                .notLike("title", "Hello");

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("not", predicate.getString(0));

        JSONArray originalPredicate = predicate.getJSONArray(1);
        assertEquals("like", originalPredicate.getString(0));

        JSONObject predicateKeypath = originalPredicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("title", predicateKeypath.getString("$val"));

        assertEquals("Hello", originalPredicate.getString(2));
    }

    @Test
    public void testCaseInsensitiveLikeQuery() throws Exception {
        Query noteQuery = new Query("Note")
                .caseInsensitiveLike("title", "Hello");

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("ilike", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("title", predicateKeypath.getString("$val"));

        assertEquals("Hello", predicate.getString(2));
    }

    @Test
    public void testCaseInsensitiveNotLikeQuery() throws Exception {
        Query noteQuery = new Query("Note")
                .caseInsensitiveNotLike("title", "Hello");

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("not", predicate.getString(0));

        JSONArray originalPredicate = predicate.getJSONArray(1);
        assertEquals("ilike", originalPredicate.getString(0));

        JSONObject predicateKeypath = originalPredicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("title", predicateKeypath.getString("$val"));

        assertEquals("Hello", originalPredicate.getString(2));
    }

    @Test
    public void testEqualToQuery() throws Exception {
        Query noteQuery = new Query("Note")
                .equalTo("title", "Hello");

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("eq", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("title", predicateKeypath.getString("$val"));

        assertEquals("Hello", predicate.getString(2));
    }

    @Test
    public void testNotEqualToQuery() throws Exception {
        Query noteQuery = new Query("Note")
                .notEqualTo("title", "Hello");

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("neq", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("title", predicateKeypath.getString("$val"));

        assertEquals("Hello", predicate.getString(2));
    }

    @Test
    public void testGreaterThanQuery() throws Exception {
        Query noteQuery = new Query("Note")
                .greaterThan("rating", 3);

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("gt", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("rating", predicateKeypath.getString("$val"));

        assertEquals(3, predicate.getInt(2));
    }

    @Test
    public void testGreaterOrEqualToQuery() throws Exception {
        Query noteQuery = new Query("Note")
                .greaterThanOrEqualTo("rating", 3);

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("gte", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("rating", predicateKeypath.getString("$val"));

        assertEquals(3, predicate.getInt(2));
    }

    @Test
    public void testLessThanQuery() throws Exception {
        Query noteQuery = new Query("Note")
                .lessThan("rating", 3);

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("lt", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("rating", predicateKeypath.getString("$val"));

        assertEquals(3, predicate.getInt(2));
    }

    @Test
    public void testLessThanOrEqualToQuery() throws Exception {
        Query noteQuery = new Query("Note")
                .lessThanOrEqualTo("rating", 3);

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("lte", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("rating", predicateKeypath.getString("$val"));

        assertEquals(3, predicate.getInt(2));
    }

    @Test
    public void testContainsQuery() throws Exception {
        List<Integer> identifiers = new LinkedList<>();
        identifiers.add(1234);
        identifiers.add(5678);

        Query noteQuery = new Query("Note")
                .contains("identifier", identifiers);

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("in", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("identifier", predicateKeypath.getString("$val"));

        JSONArray identifierJsonArray = predicate.getJSONArray(2);
        assertEquals(2, identifierJsonArray.length());
        assertEquals(1234, identifierJsonArray.getInt(0));
        assertEquals(5678, identifierJsonArray.getInt(1));
    }

    @Test
    public void testNotContainsQuery() throws Exception {
        List<Integer> identifiers = new LinkedList<>();
        identifiers.add(1234);
        identifiers.add(5678);

        Query noteQuery = new Query("Note")
                .notContains("identifier", identifiers);

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("not", predicate.getString(0));

        JSONArray originalPredicate = predicate.getJSONArray(1);
        assertEquals("in", originalPredicate.getString(0));

        JSONObject predicateKeypath = originalPredicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("identifier", predicateKeypath.getString("$val"));

        JSONArray identifierJsonArray = originalPredicate.getJSONArray(2);
        assertEquals(2, identifierJsonArray.length());
        assertEquals(1234, identifierJsonArray.getInt(0));
        assertEquals(5678, identifierJsonArray.getInt(1));
    }

    @Test
    public void testContainsValueQuery() throws Exception {
        Query noteQuery = new Query("Note")
                .containsValue("tag", "geeky");

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("in", predicate.getString(0));
        assertEquals("geeky", predicate.getString(1));

        JSONObject predicateKeypath = predicate.getJSONObject(2);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("tag", predicateKeypath.getString("$val"));
    }

    @Test
    public void testNotContainsValueQuery() throws Exception {
        Query noteQuery = new Query("Note")
                .notContainsValue("tag", "geeky");

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("not", predicate.getString(0));

        JSONArray originalPredicate = predicate.getJSONArray(1);
        assertEquals("in", originalPredicate.getString(0));
        assertEquals("geeky", originalPredicate.getString(1));

        JSONObject predicateKeypath = originalPredicate.getJSONObject(2);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("tag", predicateKeypath.getString("$val"));
    }

    @Test
    public void testQueryNegation() throws Exception {
        Query noteQuery = new Query("Note")
                .greaterThan("rating", 3)
                .negate();

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("not", predicate.getString(0));

        JSONArray originalPredicate = predicate.getJSONArray(1);
        assertEquals("gt", originalPredicate.getString(0));

        JSONObject predicateKeypath = originalPredicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("rating", predicateKeypath.getString("$val"));

        assertEquals(3, originalPredicate.getInt(2));
    }

    @Test
    public void testSorting() throws Exception {
        Query noteQuery = new Query("Note")
                .addAscending("title")
                .addDescending("rating");

        JSONArray sortPredicate = noteQuery.getSortPredicateJson();
        assertEquals(2, sortPredicate.length());

        JSONArray sortPredicate1 = sortPredicate.getJSONArray(0);

        JSONObject sortPredicate1Keypath = sortPredicate1.getJSONObject(0);
        assertEquals("keypath", sortPredicate1Keypath.getString("$type"));
        assertEquals("title", sortPredicate1Keypath.getString("$val"));

        assertEquals("asc", sortPredicate1.getString(1));

        JSONArray sortPredicate2 = sortPredicate.getJSONArray(1);

        JSONObject sortPredicate2Keypath = sortPredicate2.getJSONObject(0);
        assertEquals("keypath", sortPredicate2Keypath.getString("$type"));
        assertEquals("rating", sortPredicate2Keypath.getString("$val"));

        assertEquals("desc", sortPredicate2.getString(1));
    }

    @Test
    public void testTransient() throws Exception {
        Query noteQuery = new Query("Note")
                .transientInclude("comment")
                .transientInclude("writer", "owner");

        JSONObject transientPredicateJson = noteQuery.getTransientPredicateJson();
        JSONObject commentTransient = transientPredicateJson.getJSONObject("comment");
        assertEquals("keypath", commentTransient.getString("$type"));
        assertEquals("comment", commentTransient.getString("$val"));

        JSONObject ownerTransient = transientPredicateJson.getJSONObject("owner");
        assertEquals("keypath", ownerTransient.getString("$type"));
        assertEquals("writer", ownerTransient.getString("$val"));
    }

    @Test
    public void testMultiplePredicates() throws Exception {
        Query noteQuery = new Query("Note")
                .caseInsensitiveLike("title", "Hello")
                .greaterThan("rating", 3);

        JSONArray predicate = noteQuery.getPredicateJson();
        assertEquals("and", predicate.getString(0));

        JSONArray predicate1 = predicate.getJSONArray(1);
        assertEquals("ilike", predicate1.getString(0));

        JSONObject predicate1Keypath = predicate1.getJSONObject(1);
        assertEquals("keypath", predicate1Keypath.getString("$type"));
        assertEquals("title", predicate1Keypath.getString("$val"));

        assertEquals("Hello", predicate1.getString(2));

        JSONArray predicate2 = predicate.getJSONArray(2);
        assertEquals("gt", predicate2.getString(0));

        JSONObject predicate2Keypath = predicate2.getJSONObject(1);
        assertEquals("keypath", predicate2Keypath.getString("$type"));
        assertEquals("rating", predicate2Keypath.getString("$val"));

        assertEquals(3, predicate2.getInt(2));
    }

    @Test
    public void testOrQueryNormalFlow() throws Exception {
        Query query1 = new Query("Note").caseInsensitiveLike("title", "Hello");
        Query query2 = new Query("Note").greaterThan("rating", 3);

        JSONArray predicate = Query.or(query1, query2).getPredicateJson();
        assertEquals("or", predicate.getString(0));

        JSONArray predicate1 = predicate.getJSONArray(1);
        assertEquals("ilike", predicate1.getString(0));

        JSONObject predicate1Keypath = predicate1.getJSONObject(1);
        assertEquals("keypath", predicate1Keypath.getString("$type"));
        assertEquals("title", predicate1Keypath.getString("$val"));

        assertEquals("Hello", predicate1.getString(2));

        JSONArray predicate2 = predicate.getJSONArray(2);
        assertEquals("gt", predicate2.getString(0));

        JSONObject predicate2Keypath = predicate2.getJSONObject(1);
        assertEquals("keypath", predicate2Keypath.getString("$type"));
        assertEquals("rating", predicate2Keypath.getString("$val"));

        assertEquals(3, predicate2.getInt(2));
    }

    @Test(expected = InvalidParameterException.class)
    public void testOrQueryNotAllowEmptyList() throws Exception {
        Query.or();
    }

    @Test
    public void testOrQueryWithOnlyOneQuery() throws Exception {
        Query query1 = new Query("Note").caseInsensitiveLike("title", "Hello");
        JSONArray predicate = Query.or(query1).getPredicateJson();

        assertEquals("ilike", predicate.getString(0));

        JSONObject predicate1Keypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicate1Keypath.getString("$type"));
        assertEquals("title", predicate1Keypath.getString("$val"));

        assertEquals("Hello", predicate.getString(2));
    }
}
