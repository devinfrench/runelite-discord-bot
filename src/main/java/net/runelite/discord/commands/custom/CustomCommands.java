package net.runelite.discord.commands.custom;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomCommands {

    public static Map<String, String> commands = new ConcurrentHashMap<>();

    public static void load() {
        try {
            File file = new File(System.getProperty("user.dir") + File.separator + "commands.json");
            String commandsJson = FileUtils.readFileToString(file, "UTF-8");
            commands.clear();
            JSONArray ja = (JSONArray) new JSONTokener(commandsJson).nextValue();
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                commands.put(jo.getString("command"), jo.getString("response"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        String commandsJson = "[";
        if (!commands.isEmpty()) {
            for (String command : commands.keySet()) {
                commandsJson += "{\"command\":\"" + command + "\",\"response\":\""
                    + StringEscapeUtils.escapeJson(commands.get(command)) + "\"},";
            }
            commandsJson = commandsJson.substring(0, commandsJson.lastIndexOf(","));
        }
        commandsJson += "]";
        try {
            File file = new File(System.getProperty("user.dir") + File.separator + "commands.json");
            FileUtils.write(file, commandsJson, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
