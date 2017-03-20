/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hu.deezer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "org.hu.codeanalyser.DeezerAction"
)
@ActionRegistration(
        displayName = "deezermodule"
)
@ActionReference(path = "Menu/Tools", position = 0)
@Messages("CTL_DeezerAction=DeezerPlaylists")
public final class DeezerAction implements ActionListener {
    public static String userID = "";
    
    @Override
    public void actionPerformed(ActionEvent e) {
    try {
        loadUserIdSettings();
        if(userID.equals("")){
            userID = JOptionPane.showInputDialog(null, "Please enter your Deezer ID.");
            System.setProperty("deezerID", userID);
        }
        
        HashMap<String, Long> playlists = getDeezerPlaylists("https://api.deezer.com/user/" + userID + "/playlists");
        String[] options = getArrayFromMap(playlists);
 
        String selected = (String) JOptionPane.showInputDialog(null, "Select a playlist", "Deezer playlists", JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        
        if(selected != null){
            Long playlistId = playlists.get(selected);
            URLDisplayer.getDefault().showURL(new URL("http://www.deezer.com/playlist/" + playlistId));    
        }
    } catch (Exception eee){
        eee.printStackTrace();
        return;//nothing much to do
    }
    }
    
    private static void loadUserIdSettings(){
        if(System.getProperty("deezerID") != null){ 
            userID = System.getProperty("deezerID");
        }
    }
    
    public static String[] getArrayFromMap(HashMap<String, Long> map){
        String[] options = new String[map.size()];
        int index = 0;
        for(Map.Entry<String, Long> entry : map.entrySet()){
            options[index] = entry.getKey();
            index++;
        }
        return options;
    }
    
    public static HashMap<String, Long> getDeezerPlaylists(String url) throws IOException, ParseException {
        InputStream is = new URL(url).openStream();
        try {
          BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
          String jsonText = readAll(rd);
          JSONParser parser = new JSONParser();
          Object obj = parser.parse(jsonText);
          JSONObject json = (JSONObject) obj;
          JSONArray data = (JSONArray) json.get("data");
          
          HashMap<String,Long> playlists = new HashMap<>();
          
          for(int i = 0; i < data.size(); i++){
              JSONObject playlist = (JSONObject) data.get(i);
              playlists.put((String) playlist.get("title"), (Long) playlist.get("id"));
          }
          
          return playlists;
        } finally {
          is.close();
        }
    }
    
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
        }
        return sb.toString();
    }
}
