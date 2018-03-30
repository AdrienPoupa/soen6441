(function() {
    var parseTweets;
    $(function() {
        if($("#search").length === 1) {
            var ws;
            console.log("Creating WebSocket");
            ws = new WebSocket($("body").data("ws-url"));
            ws.onmessage = function (event) {
                var message;
                message = JSON.parse(event.data);
                switch (message.type) {
                    case "status":
                        return parseTweets(message);
                    default:
                        return console.log(message);
                }
            };
            return $("#searchTweetsForm").submit(function (event) {
                event.preventDefault();
                if ($("#keyword").val() !== '') {
                    console.log("Sending WS with value " + $("#keyword").val());
                    ws.send(JSON.stringify({
                        query: $("#keyword").val()
                    }));
                    $("#placeholderText").text("Searching for tweets containing \""+$("#keyword").val()+"\"");
                    return $("#keyword").val("");
                }
            });
        }
    });

    parseTweets = function(message) {
        $("#tweetsList").prepend('<li><a href="http://localhost:9000/profile/'+message.user.screen_name+'">'
            +message.user.screen_name+' wrote:</a> '+message.full_text+'</li>');
    }

}).call(this);
