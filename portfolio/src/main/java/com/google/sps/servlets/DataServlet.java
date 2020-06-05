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
import java.util.List;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
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
    

    List<String> commentsInJson = loadCommentH();

    HashMap<String, List<String>> data = new HashMap<String, List<String>>();

    data.put("Facts", facts);
    data.put("Comments", loadCommentH());

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

  public List<String> loadCommentH() {
    List<String> list = new ArrayList<String>();

    Query query = new Query("Comment").addSort("User", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
        list.add((String) entity.getProperty("Comment"));
    }

    return list;
  }
}
