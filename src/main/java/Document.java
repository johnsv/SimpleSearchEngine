import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds the document id and a term frequency map
 * of all terms in the document.
 */
public class Document {
    private final int id;
    private String content;
    private HashMap<String, AtomicInteger> termfrequency = new HashMap<>();

    public Document(int id, String content) {
        this.id = id;
        this.content = content;
        String spaceDelimiter = " ";
        Arrays.stream(content.split(spaceDelimiter)).forEach(word -> {
           update(word);
        });

    }

    public void update(String word) {
        if(termfrequency.containsKey(word)) {
            termfrequency.get(word).incrementAndGet();
        } else {
            termfrequency.put(word, new AtomicInteger(1));
        }
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    /**
     * Returns the frequency of the specified term.
     * @param term
     * @return the frequency of the term in this document
     */
    public int getTermfrequency(String term) {
        return termfrequency.get(term).intValue();
    }
}
