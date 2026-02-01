import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;

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

    //Change for HttpClient when finish
    private static void fetchGitHubActivity(String username) throws Exception {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://api.github.com/users/{username}/events");

        } catch (MalformedURLException e) {
            throw new Exception("Malformed URL" + e.getMessage());
        }

    }
}