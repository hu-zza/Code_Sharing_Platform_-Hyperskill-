<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Latest</title>

  <link
      href="https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css"
      rel="stylesheet">
  <script
      src="https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js"></script>
  <script>hljs.initHighlightingOnLoad();</script>
</head>
<body>
<#list latestList as snippet>
<p><span id="load_date_${snippet?counter}">${snippet.getDate()}</span></p>
<pre id="code_snippet_${snippet?counter}"><code>${snippet.getCode()}</code></pre>
<br>
</
#list>
</body>
</html>