<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Code</title>

  <link
      href="https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css"
      rel="stylesheet">
  <script
      src="https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js"></script>
  <script>hljs.initHighlightingOnLoad();</script>
</head>
<body>
<#if snippet.getDate()??>
<p><span id="load_date">${snippet.getDate()}</span></p>
</
#if>
<#if snippet.isRestrictedByViews()>
<p><span id="views_restriction">${snippet.getViews()} more views allowed</span></p>
</#if>
<#if snippet.isRestrictedByTime()>
<p><span id="time_restriction">The code will be available for ${snippet.getTime()} seconds</span>
</p>
</#if>
<#if snippet.getCode()??>
<pre id="code_snippet"><code>${snippet.getCode()}</code></pre>
</#if>
</body>
</html>