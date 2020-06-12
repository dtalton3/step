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

/**
 * Adds a random fact to the page.
 */
function funFactGenerator() {
  const facts =
      ['My birthday is June 19, 2000.',
       'I once had 10 pets dogs all at once.',
        'I have a YouTube video that is semi-viral... if I do say so myself.',
         'I am interested in videography and photography',
         'My dream job is to work with the team that developed YouTube, or create my own rivalling service.',
         'I would have been in Bellevue, Washington if not for COVID-19'];

  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factGetter = document.getElementById('factgetter');
  factGetter.innerText = fact;
}

/**
 * Displays a fun fact about Desmond on the page.
 */
function serverFunFact() { 

    // Fetch the data from the server.
    fetch("/data").then(dataHashMap => dataHashMap.json()).then(dataHashMapJson => {

        // Pick a random fact.
        const fact = 
        dataHashMapJson.Facts[Math.floor(Math.random() * dataHashMapJson.Facts.length)]

        // Add it to the page.
        document.getElementById('factgetter').innerText = fact;
    })
}

/**
 * Adds user's comment to the page.
 */
function showComments() {

    // Starts post request using user's input.            
    var request = new Request("/data", {method: "POST",
                body: document.getElementById("textfield").value});

    // Fetches server comment data and writes it to the page.
    fetch(request).then(res => {
        console.log(res);
        loadComments();
    });
}

/**
 * Accesses the comments from the datastore and displays them.
 */
function loadComments() {
    fetch("/data?lang=" + document.getElementById("language").value).then(dataHashMapJson => {
        console.log(dataHashMapJson);
        const len = dataHashMapJson.Comments.length;
        var comments = dataHashMapJson.Comments;
        let commNum = Math.min(document.getElementById("CommentNumber").options[0].selected ? 5 : 10, len);
        var commentString = "";
        var startIdx = len - commNum;
        for (let i = startIdx; i < len; i++) {
            commentString += "<p>" + comments[i] + "</p>";
        }
        document.getElementById("thecomments").innerHTML = commentString;
    })
}

function refreshComments() {
    loadComments();
}

function deleteAllComments() {
    document.getElementById("thecomments").innerHTML = "";  
    fetch("/delete-data", {method: "POST"}).then(res => loadComments());
}

function authenticateUser() {
    fetch("/user-login").then(userHashMap => userHashMap.json()).then(userHashMapJson => {
        console.log(userHashMapJson);
        if (userHashMapJson.valid === true) {
            document.getElementById("commentsection").innerHTML = 
            '<p>Type a comment:</p>' +
            '<input type="text" name="commentpost" value="" id="textfield">' +
            '<button onclick="showComments()">Post.</button><br/><br/></input>' + 
            "<p> <a href=\"" + userHashMapJson.logoutUrl + "\">Logout </a></p>";
        } else {
            document.getElementById("commentsection").innerHTML = 
            "<p> <a href=\"" + userHashMapJson.loginUrl + "\">Login </a>to post comments.</p>";
        }
    }) 
}