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

/** Servlet that returns some example content.*/
@WebServlet("/delete-data")
public class DeletedDataServlet extends HttpServlet {


    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("Comment").addSort("User", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity : results.asIterable()) {
            datastore.delete(entity.getKey());
        }
        HashMap<String, List<String>> data = new HashMap<String, List<String>>();

        data.put("Comments", new ArrayList<String>());

        String json = convertToJsonUsingGson(data);

        response.setContentType("application/json");
        response.getWriter().println(json);
    }

    private String convertToJsonUsingGson(Object list) {
    Gson gson = new Gson();
    String json = gson.toJson(list);
    return json;
  }
}
