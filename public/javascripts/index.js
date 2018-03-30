(function() {
    var getAxisMax, getAxisMin, getChartArray, getChartOptions, getPricesFromArray, handleFlip, populateStockHistory, updateStockChart;

    $(function() {
        var ws;
        ws = new WebSocket($("body").data("ws-url"));
        ws.onmessage = function(event) {
            var message;
            message = JSON.parse(event.data);
            //console.log(message);
            switch (message.type) {
                case "stockhistory":
                    return populateStockHistory(message);
                case "stockupdate":
                    return updateStockChart(message);
                case "status":
                    return console.log(message);
                default:
                    return console.log(message);
            }
        };
        return $("#addsymbolform").submit(function(event) {
            event.preventDefault();
            if ($("#addsymboltext").val() != '') {
                console.log("Sending WS with value "+$("#addsymboltext").val());
                ws.send(JSON.stringify({
                    query: $("#addsymboltext").val()
                }));
                return $("#addsymboltext").val("");
            }
        });
    });

    getPricesFromArray = function(data) {
        var j, len, results, v;
        results = [];
        for (j = 0, len = data.length; j < len; j++) {
            v = data[j];
            results.push(v[1]);
        }
        return results;
    };

    getChartArray = function(data) {
        var i, j, len, results, v;
        results = [];
        for (i = j = 0, len = data.length; j < len; i = ++j) {
            v = data[i];
            results.push([i, v]);
        }
        return results;
    };

    getChartOptions = function(data) {
        return {
            series: {
                shadowSize: 0
            },
            yaxis: {
                min: getAxisMin(data),
                max: getAxisMax(data)
            },
            xaxis: {
                show: false
            }
        };
    };

    getAxisMin = function(data) {
        return Math.min.apply(Math, data) * 0.9;
    };

    getAxisMax = function(data) {
        return Math.max.apply(Math, data) * 1.1;
    };

    populateStockHistory = function(message) {
        var chart, chartHolder, detailsHolder, flipContainer, flipper, plot;
        chart = $("<div>").addClass("chart").prop("id", message.symbol);
        chartHolder = $("<div>").addClass("chart-holder").append(chart);
        chartHolder.append($("<p>").text("values are simulated"));
        detailsHolder = $("<div>").addClass("details-holder");
        flipper = $("<div>").addClass("flipper").append(chartHolder).append(detailsHolder).attr("data-content", message.symbol);
        flipContainer = $("<div>").addClass("flip-container").append(flipper).click(function(event) {
            return handleFlip($(this));
        });
        $("#stocks").prepend(flipContainer);
        return plot = chart.plot([getChartArray(message.history)], getChartOptions(message.history)).data("plot");
    };

    updateStockChart = function(message) {
        var data, plot, yaxes;
        if ($("#" + message.symbol).size() > 0) {
            plot = $("#" + message.symbol).data("plot");
            data = getPricesFromArray(plot.getData()[0].data);
            data.shift();
            data.push(message.price);
            plot.setData([getChartArray(data)]);
            yaxes = plot.getOptions().yaxes[0];
            if ((getAxisMin(data) < yaxes.min) || (getAxisMax(data) > yaxes.max)) {
                yaxes.min = getAxisMin(data);
                yaxes.max = getAxisMax(data);
                plot.setupGrid();
            }
            return plot.draw();
        }
    };

    handleFlip = function(container) {
        var detailsHolder;
        if (container.hasClass("flipped")) {
            container.removeClass("flipped");
            return container.find(".details-holder").empty();
        } else {
            container.addClass("flipped");
            $.ajax({
                url: "/sentiment/" + container.children(".flipper").attr("data-content"),
                dataType: "json",
                context: container,
                success: function(data) {
                    var detailsHolder;
                    detailsHolder = $(this).find(".details-holder");
                    detailsHolder.empty();
                    switch (data.label) {
                        case "pos":
                            detailsHolder.append($("<h4>").text("The tweets say BUY!"));
                            return detailsHolder.append($("<img>").attr("src", "/assets/images/buy.png"));
                        case "neg":
                            detailsHolder.append($("<h4>").text("The tweets say SELL!"));
                            return detailsHolder.append($("<img>").attr("src", "/assets/images/sell.png"));
                        default:
                            detailsHolder.append($("<h4>").text("The tweets say HOLD!"));
                            return detailsHolder.append($("<img>").attr("src", "/assets/images/hold.png"));
                    }
                },
                error: function(jqXHR, textStatus, error) {
                    var detailsHolder;
                    detailsHolder = $(this).find(".details-holder");
                    detailsHolder.empty();
                    return detailsHolder.append($("<h2>").text("Error: " + JSON.parse(jqXHR.responseText).error));
                }
            });
            detailsHolder = container.find(".details-holder");
            detailsHolder.append($("<h4>").text("Determining whether you should buy or sell based on the sentiment of recent tweets..."));
            return detailsHolder.append($("<div>").addClass("progress progress-striped active").append($("<div>").addClass("bar").css("width", "100%")));
        }
    };

}).call(this);

//# sourceMappingURL=index.js.map
