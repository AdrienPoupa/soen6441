(function() {
    var parseTweets;
    $(function() {
        if($("#search").length === 1) {
            var ws;
            console.log("Waiting for WebSocket");
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
                if ($("#query").val() !== '') {
                    console.log("Sending WS with value " + $("#query").val());
                    ws.send(JSON.stringify({
                        query: $("#query").val()
                    }));
                    return $("#query").val("");
                }
            });
        }
    });

    parseTweets = function(message) {
        var query = message.query.replace(/ /g,'');
        tweetsListQuery = $("#tweetsList"+query);
        if (tweetsListQuery.length === 0) {
            $("#tweets").prepend('<div class="results"><p>Tweets for '+message.query+'</p><ul id="tweetsList'+query+'"></ul></div>');
        }
        tweetsListQuery.prepend('<li><a href="http://localhost:9000/profile/'+message.user.screen_name+'">'
            +message.user.screen_name+'</a> wrote: '+message.full_text+'</li>');

    }

}).call(this);
