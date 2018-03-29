package models;

import akka.NotUsed;
import akka.japi.Pair;
import akka.japi.function.Function;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Map SearchResults to an object
 * @author Adrien Poupa
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {

    protected String query;

    protected List<Status> statuses;

    private final Source<StockQuote, NotUsed> source;

    private final StockQuoteGenerator stockQuoteGenerator;

    private static final FiniteDuration duration = Duration.create(75, TimeUnit.MILLISECONDS);

    public SearchResult() {
        this.query = null;
        this.source = null;
        this.stockQuoteGenerator = null;
    }

    public SearchResult(String query) {
        this.query = query;
        stockQuoteGenerator = new StockQuoteGenerator(query);
        source = Source.unfold(stockQuoteGenerator.seed(), (Function<StockQuote, Optional<Pair<StockQuote, StockQuote>>>) last -> {
            StockQuote next = stockQuoteGenerator.newQuote(last);
            return Optional.of(Pair.apply(next, next));
        });
    }

    /**
     * Get statuses
     * @return List<Status> statuses
     */
    public List<Status> getStatuses() {
        return statuses;
    }

    /**
     * Set the statuses
     * @param statuses List<Status> statuses
     */
    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    /**
     * Search query
     * @return String the keyword(s)
     */
    public String getQuery() {
        return query;
    }

    /**
     * Set the search query
     * @param query the keyword(s)
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Returns a source of stock history, containing a single element.
     */
    public Source<StockHistory, NotUsed> history(int n) {
        return source.grouped(n)
                .map(quotes -> new StockHistory(query, quotes.stream().map(sq -> sq.price).collect(Collectors.toList())))
                .take(1);
    }

    /**
     * Provides a source that returns a stock quote every 75 milliseconds.
     */
    public Source<StockUpdate, NotUsed> update() {
        return source.throttle(1, duration, 1, ThrottleMode.shaping())
                .map(sq -> new StockUpdate(sq.symbol, sq.price));
    }
}
