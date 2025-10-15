package dogapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A BreedFetcher that caches successful results to avoid redundant API calls.
 * Failed lookups (exceptions) are NOT cached and will retry on subsequent calls.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache;
    private int callsMade;

    /**
     * Creates a new CachingBreedFetcher that wraps the given fetcher.
     * @param fetcher the underlying BreedFetcher to use for actual API calls
     */
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
        this.cache = new HashMap<>();
        this.callsMade = 0;
    }

    /**
     * Fetch the list of sub breeds for the given breed.
     * Results are cached so subsequent calls for the same breed won't hit the API.
     * Exceptions are NOT cached - failed breeds will be retried.
     *
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // Check if we have a cached result
        if (cache.containsKey(breed)) {
            return cache.get(breed);
        }

        // Not in cache, so make the actual API call
        callsMade++;
        List<String> result = fetcher.getSubBreeds(breed);

        // Cache the successful result
        cache.put(breed, result);

        return result;
    }

    /**
     * Returns the number of actual calls made to the underlying fetcher.
     * Cached results don't count as calls.
     *
     * @return the number of calls made to the underlying fetcher
     */
    public int getCallsMade() {
        return callsMade;
    }
}