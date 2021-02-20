package de.mert.vars;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Github {

    private JSONArray jsonArray;
    private String path;

    public Github(String path) throws IOException {
        URL url = new URL("https://api.Github.com/repos/mertdogan12/1v1-MC-Plugin/contents/" + path);
        this.path = path;

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            Object obj = new JSONParser().parse(response.toString());
            jsonArray = (JSONArray) obj;
        } catch (UnknownHostException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void downloadFiles(String path) throws IOException {
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject o = (JSONObject) jsonArray.get(i);

            if (!o.get("type").equals("file")) {
                Github gitHub = new Github(this.path + o.get("name"));

                File f = new File(path + o.get("name"));
                if (path.equals("")) f = new File((String) o.get("name"));

                f.mkdirs();
                System.out.println("Dir created: " + f.getPath());
                gitHub.downloadFiles(f.getPath());
            } else {
                URL website = new URL((String) o.get("download_url"));

                try (InputStream in = website.openStream()) {
                    Path target = Paths.get(path + o.get("name"));
                    if (path.equals("")) target = Paths.get((String) o.get("name"));

                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println(website.toString() + " --> " + path + o.get("name"));
                }
            }
        }
    }

    public boolean isDownloaded(String path) throws IOException {
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject o = (JSONObject) jsonArray.get(i);

            if (!new File(path  + o.get("name")).exists()) {
                System.out.println("File not exist: " + path  + o.get("name"));
                return false;
            }

            if (o.get("type").equals("dir")) {
                Github github = new Github(this.path  + o.get("name"));

                if (!github.isDownloaded(path  + o.get("name"))) return false;
            }

            System.out.println("File exist: " + o.get("url"));
        }

        return true;
    }
}
