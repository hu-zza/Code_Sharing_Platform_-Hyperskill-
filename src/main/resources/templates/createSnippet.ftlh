<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Create</title>
</head>
<body>
<form onsubmit="send(); return false;">
  <label for="code_snippet">Code:</label>
  <br>
  <textarea id="code_snippet"></textarea>
  <br><br>
  <label for="time_restriction">Time limit:</label>
  <input id="time_restriction" type="number">
  <br>
  <label for="views_restriction">View limit:</label>
  <input id="views_restriction" type="number">
  <br><br>
  <button id="send_snippet" type="submit">Submit</button>
</form>
<script>
            function send() {
                let object = {
                    "code": document.getElementById("code_snippet").value,
                    "time": document.getElementById("time_restriction").value,
                    "views": document.getElementById("views_restriction").value
                };

                let json = JSON.stringify(object);

                let xhr = new XMLHttpRequest();
                xhr.open("POST", '/api/code/new', false);
                xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
                xhr.send(json);

                if (xhr.status === 200) {
                    alert("Success!");
                } else {
                    alert("Failure")
                }
            }



</script>
</body>
</html>