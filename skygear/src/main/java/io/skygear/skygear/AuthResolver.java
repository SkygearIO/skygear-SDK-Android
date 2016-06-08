package io.skygear.skygear;

/**
 * The interface Auth resolver.
 */
public interface AuthResolver {
    /**
     * Resolve auth token.
     *
     * @param token access token
     */
    void resolveAuthToken(String token);
}
