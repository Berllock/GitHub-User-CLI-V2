import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
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

        try {
            JSONArray activities = fetchGitHubActivity(username);
            displayActivities(activities);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void displayActivities(JSONArray activities) {

        if(activities.length() == 0) {
            System.out.println("No recent activity found for. ");
            return;
        }

        System.out.println("\nLast activities: ");

        int limit = Math.min(activities.length(), 10);
        for (int i = 0;  i < limit; i++) {
            JSONObject event  = activities.getJSONObject(i);
            String message = interpretEvent(event);
            System.out.println("- " + message);
        }
    }

    public static String interpretEvent (JSONObject event) {
        String type = event.getString("type");
        String repo = event.getJSONObject("repo").getString("name");
        JSONObject payload = event.getJSONObject("payload");

        switch (type) {
            case "PushEvent":
                System.out.println("Passando por" + type);

                int commits = payload.optInt("size", 0);

                if (commits == 0 && payload.has("commits")) {
                    commits = payload.getJSONArray("commits").length();
                }

                return "Sent " + commits + " commit(s) to " + repo;

            case "PullRequestEvent":
                String prAction = payload.getString("action");
                return prAction.substring(0, 1).toUpperCase() + prAction.substring(1) + " a pull request on " + repo;

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
    private static JSONArray fetchGitHubActivity(String username) throws Exception {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://api.github.com/users/" + username + "/events");

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "GitHubActivityCLI/1.0");

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

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
        } catch (IOException e) {
            throw new Exception("IO Error: " + e.getMessage());
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    System.err.println("Error closing reader: " + e.getMessage());
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}