package io.skygear.skygear;

/**
 * The interface Auth resolver.
 */
public interface AuthResolver {
    /**
     * Resolve auth token.
     *
     * @param user authenticated user
     */
    void resolveAuthUser(User user);
}
