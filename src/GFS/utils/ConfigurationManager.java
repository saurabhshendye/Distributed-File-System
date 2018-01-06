package GFS.utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.regex.Pattern;

/**
 * @author saurabhs
 *
 * Validates configuration files for respective nodes
 */

public class ConfigurationManager {
    // To identify which class is creating object
    // for this configuration manager
    private int serverType;
    // Server IP from Configuration file
    private String serverIP;
    // Server Port from config file
    private int serverPort;
    // Local Port
    private int localPort;
    // Directory location for chunk server
    private String Directory;

    // Names of the configuration files
    private static final String CLIENT_CONFIG = "client_config.json";
    private static final String CONTROLLER_CONFIG = "controller_config.json";
    private static final String CHUNKSERVER_CONFIG = "chunkServer_config.json";

    // Path for the config.json file
    private static final String CLIENT_CONFIG_PATH = "./" + CLIENT_CONFIG;
    private static final String CONTROLLER_CONFIG_PATH = "./" + CONTROLLER_CONFIG;
    private static final String CHUNK_CONFIG_PATH = "./" + CHUNKSERVER_CONFIG;

    // Regex pattern to check if the IP is valid or not
    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public ConfigurationManager(int type){
        this.serverType = type;
    }

    /**
     * Checks validity of the corresponding configuration file
     * @return
     */
    public boolean isValid(){
        switch (serverType){
            // For Client
            case 0:
                return checkValidityClient(CLIENT_CONFIG_PATH);
            // For Chunk Server
            case 1:
                return checkValidityChunk(CHUNK_CONFIG_PATH);
            // For controller
            case 2:
                return checkValidityController(CONTROLLER_CONFIG_PATH);
            default:
                System.out.println("Invalid Type");
                return false;
        }
    }

    /**
     * Checks the validity of the Client configuration file
     * @param path path for the config file
     * @return true if the configuration is valid false otherwise
     */

    private boolean checkValidityClient(String path){
        try {
            JSONObject jsonObject = readConfig(path);
            serverIP = jsonObject.getString("controller_ip");
            serverPort = jsonObject.getInt("controller_port");
            // Check if IP is valid
            return isIPValid(serverIP) && isPortValid(serverPort);

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks the validity of the Chunk Server configuration file
     * @param path path for the config file
     * @return true if the configuration is valid false otherwise
     */
    private boolean checkValidityChunk(String path){
        try {
            JSONObject jsonObject = readConfig(path);
            serverIP = jsonObject.getString("controller_ip");
            serverPort = jsonObject.getInt("controller_port");
            localPort = jsonObject.getInt("Local_Port");
            Directory = jsonObject.getString("Directory");
            // Check if IP is valid
            return isIPValid(serverIP) && isPortValid(serverPort) && isPortValid(localPort);

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks the validity of the Controller configuration file
     * @param path path for the config file
     * @return true if the configuration is valid false otherwise
     */
    private boolean checkValidityController(String path){
        try {
            JSONObject jsonObject = readConfig(path);
            localPort = jsonObject.getInt("Local_Port");
            // Check if IP is valid
            return isPortValid(localPort);

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if the port number is valid
     * @param port port number
     * @return
     */
    private boolean isPortValid(int port){
        if (serverPort <= 1024 || serverPort > 65536){
            System.out.println("Invalid Port number. Port number should be greater than 1024 and less than 65536");
            return false;
        }

        return true;
    }

    private boolean isIPValid(String ip){
        if (ip.isEmpty()){
            System.out.println("Invalid IP address");
            return false;
        }

        if (!validate(ip)){
            System.out.println("Invalid IP address");
            return false;
        }



        return true;
    }

    /**
     * Creates a JSONObject from the configuration file
     * @param path path for the config file
     * @return JSONObject of the configuration file
     * @throws JSONException If unable to create the JSON object
     */
    private JSONObject readConfig(String path) throws JSONException {
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            return new JSONObject(builder.toString());
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static boolean validate(String ip) {
        return PATTERN.matcher(ip).matches();
    }

    /**
     * Getter Method
     * @return Respective Remote Server Port
     */
    public int getServerPort(){
        return serverPort;
    }

    /**
     * Getter Method
     * @return local port
     */
    public int getLocalPort(){
        return localPort;
    }

    /**
     * Getter Method
     * @return respective server IP
     */
    public String getServerIP() {
        return serverIP;
    }
}
