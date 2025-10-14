package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        // Construct the API endpoint URL for fetching sub-breeds
        String url = "https://dog.ceo/api/breed/" + breed.toLowerCase() + "/list";

        // Build the HTTP request
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            // Execute the request and get the response
            Response response = client.newCall(request).execute();

            // Check if the response was successful
            if (!response.isSuccessful()) {
                throw new BreedNotFoundException(breed);
            }

            // Parse the JSON response body
            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);

            // Check the status field in the response
            String status = json.getString("status");
            if (!status.equals("success")) {
                throw new BreedNotFoundException(breed);
            }

            // Extract the "message" field which contains the array of sub-breeds
            JSONArray subBreedsArray = json.getJSONArray("message");

            // Convert JSONArray to List<String>
            List<String> subBreeds = new ArrayList<>();
            for (int i = 0; i < subBreedsArray.length(); i++) {
                subBreeds.add(subBreedsArray.getString(i));
            }

            return subBreeds;

        } catch (IOException e) {
            // If any IO exception occurs, wrap it as BreedNotFoundException
            throw new BreedNotFoundException(breed);
        } catch (Exception e) {
            // Catch any other exceptions (e.g., JSON parsing errors)
            throw new BreedNotFoundException(breed);
        }
    }
}