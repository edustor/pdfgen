<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edustor PDF Generator</title>
</head>
<body>
<form action="/pdf" method="get">
    <div>
        <label for="subject">Subject</label>
        <input name="subject" type="text" size="70">
    </div>
    <br>
    <div>
        <label for="author">Author</label>
        <input name="author" type="text" size="70" value="Dmitry Romanov">
    </div>
    <div>
        <label for="course">Course</label>
        <input name="course" type="text" size="70">
    </div>
    <div>
        <label for="copyright">Copyright</label>
        <input name="copyright" type="text" size="70" value="Dmitry Romanov">
    </div>
    <div>
        <label for="contacts">Contacts</label>
        <input name="contacts" type="text" size="70" value="wtrn.ru | me@wtrn.ru | @wutiarn on Telegram, Twitter, VK, FB, GitHub and Habrahabr">
    </div>
    <div>
        <label for="pagesCount">Regular pages count</label>
        <input name="pagesCount" type="int" value="2">
    </div>
    <div>
        <label for="generateTitle">Generate title page</label>
        <input name="generateTitle" type="checkbox" checked="checked">
    </div>
    <div>
        <label for="cornell">Draw markers</label>
        <input name="markers" type="checkbox">
    </div>
    <div>
        <label for="cornell">Use Cornell markup</label>
        <input name="cornell" type="checkbox">
    </div>
    <br>
    <div>
        <button type="submit">Generate</button>
    </div>
</form>
</body>
</html>