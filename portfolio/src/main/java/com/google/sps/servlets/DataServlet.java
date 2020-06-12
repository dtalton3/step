// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;
import java.io.IOException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.util.HashMap;
import java.io.BufferedReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import java.util.List;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

/** Servlet that returns some example content.*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private List<String> facts;
  private List<String> comments;

  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  private static int userNum = 0;

  @Override
  public void init() {
    facts = new ArrayList<>();
    facts.add("My birthday is June 19, 2000.");
    facts.add("I once had 10 pets dogs all at once.");
    facts.add("I have a YouTube video that is semi-viral... if I do say so myself.");
    facts.add("I am interested in videography and photography");
    facts.add("My dream job is to work with the team that developed YouTube, or create my own rivalling service.");
    facts.add("I would have been in Bellevue, Washington if not for COVID-19");

    comments = new ArrayList<>();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HashMap<String, List<String>> data = new HashMap<String, List<String>>();
    String lang = request.getParameter("lang");
    data.put("Facts", facts);
    data.put("Comments", loadCommentH(lang));
    String json = convertToJsonUsingGson(data);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BufferedReader bReader = request.getReader();
    String comment = bReader.readLine();
    System.out.println(comment);
    Entity userComment = new Entity("Comment");
    userComment.setProperty("User", "User #" + (++userNum));
    userComment.setProperty("Comment", comment);
    userComment.setProperty("timestamp", System.currentTimeMillis());
    datastore.put(userComment);
    comments.add(comment);
    String commentjson = convertToJsonUsingGson(comments);
    response.setContentType("application/json");
    response.getWriter().println(commentjson);
  }

  private String convertToJsonUsingGson(Object list) {
    Gson gson = new Gson();
    String json = gson.toJson(list);
    return json;
  }

  public List<String> loadCommentH(String lang) {
    List<String> list = new ArrayList<String>();
    Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
        Translate translate = TranslateOptions.getDefaultInstance().getService();
        Translation translation = translate.translate((String) entity.getProperty("Comment"), Translate.TranslateOption.targetLanguage(lang));
        String translatedText = translation.getTranslatedText();
        list.add(translatedText);
    }
    return list;
  }
}
