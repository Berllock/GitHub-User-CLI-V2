import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GitHubActivityCLI {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Use: java GitHubActivityCLI <username>");
            System.exit(1);
        }
        String username = args[0];
        System.out.println("\nSearching for" + username + "activity on GitHub...");
    }

    private void displayActivities() {

    }

    public static String interpretEvent (JSONObject event) {
        String type = event.getString("type");
        String repo = event.getJSONObject("repo").getString("name");
        JSONObject payload = event.getJSONObject("payload");

        switch (type) {
            case "PushEvent":
                int commits = payload.getJSONArray("commits").length();
                return "Send " + commits + " commit(s) to " + repo;

            case "PullRequestEvent":
                String prAction = payload.getString("action");
                return prAction.substring(0, 1).toUpperCase() + prAction.substring(1);

            case "IssuesEvent":
                String action = payload.getString("action");
                return action.substring(0, 1).toUpperCase() + action.substring(1) + " issue on " + repo;

            case "WatchEvent":
                return "Message " + repo;

            case "CreateEvent":
                return "Created" + payload.getString("ref_type") + " from " + repo;

            case "DeleteEvent":
                return "Deleted" + payload.getString("ref_type") + " from " + repo;

            case "ForkEvent":
                return "Forked " + payload.getString("ref_type") + " from " + repo;

            default:
                return "Performed " + type + " on " + repo;
        }
    }

    //Change for HttpClient when finish
    private static JSONArray fetchGitHubActivity(final String username) throws Exception {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://api.github.com/users/{username}/events");

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new Exception("Failed to fetch data. HTTP response code: " + responseCode);

            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return new JSONArray(response.toString());

        } catch (MalformedURLException e) {
            throw new Exception("Malformed URL" + e.getMessage());
        }

    }
}