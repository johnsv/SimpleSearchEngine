import java.util.HashMap;
import java.util.LinkedList;

/**
 * An inverted index represented by a HashMap
 * with key: (String) -> value: (IndexEntry) mappings.
 * 
 */
public class InvertedIndex {

    private HashMap<String, IndexEntry> index;
    public InvertedIndex() {
        index = new HashMap<>();
    }

    /**
     * Updates the inverted index with token and docId.
     * @param term the term that is about to be added
     * @param docId that the term was found in
     */
    public void add(String term, Integer docId, boolean sort) {
        term = term.toLowerCase();

        if (index.containsKey(term)) {
            updateIndex(term, docId, sort);
        } else {
            index.put(term, new IndexEntry(docId));
        }
    }

    /**
     * This methods is called when the term
     * is already in the index. It updates the
     * total term frequency in all document and also
     * updates the tf of the corresponding Posting.
     *
     * @param term the term that is about to be added
     * @param docId that the term was found in
     * @param sort if the postings list should be sorted
     *             after index is updated.
     */
    private void updateIndex(String term, Integer docId, boolean sort) {
        IndexEntry indexEntry = index.get(term);
        indexEntry.incrementTotalTermFrequency();
        indexEntry.updatePostings(docId);

        if(sort) {
            indexEntry.getPostings().sort((o1, o2) -> {
                return sortPostingOnTf(o1, o2);
            });
        }


    }

    /**
     * Sorts the postings lists of all entries in
     * this index. The sort is first on document tf
     * and then on document id.
     */
    public void sortIndex() {
        index.keySet().iterator().forEachRemaining(key -> {
            index.get(key).getPostings().sort((o1, o2) -> {
                return sortPostingOnTf(o1, o2);
            });
        });
    }

    private int sortPostingOnTf(Posting o1, Posting o2) {
        if (o1.getTermFrequency() < o2.getTermFrequency()) {
            return 1;
        } else if (o1.getTermFrequency() > o2.getTermFrequency()) {
            return -1;
        } else if (o1.getDocId() > o2.getDocId()) {
            return 1;
        } else if (o1.getDocId() < o2.getDocId()){
            return -1;
        }

        return 0;
    }

    /**
     * Does a single term frequency query.
     * @param term the query word
     * @return Returns the documents ordered by term frequency.
     */
    public LinkedList<Posting> singleTermQuery(String term) {
        if (index.containsKey(term)) {
            return index.get(term).getPostings();
        } else {
            System.out.println("Term: \'" + term + "\' was not found in any document.");
            return null;
        }
    }

    private double tfIdf(String term, int docId){
        return tf(term, docId) * idf(term);
    }

    /**
     * Calculates the term frequency in a document
     *
     * @param term the term
     * @param docId the id of the document
     * @return the term frequency
     */
    private int tf(String term, int docId) {
        Posting postingWithDocId = null;
        if(index.containsKey(term)) {
            postingWithDocId = index.get(term).getPostings().stream()
                    .filter(posting -> posting.getDocId() == docId)
                    .findFirst().orElse(null);
        }
        return (postingWithDocId != null) ? postingWithDocId.getTermFrequency() : 0;

    }

    /**
     * Calculates the idf of a term
     *
     * @param term the term
     * @return the idf of the term
     */
    private double idf(String term) {
        int N = index.keySet().size();
        if (index.containsKey(term)) {
            int numberOfDocumentsContainingTerm = index.get(term).getPostings().size();
            return Math.log((double) N / (double) numberOfDocumentsContainingTerm);
        }

        return 0;

    }

    public HashMap<String, IndexEntry> getIndex() {
        return index;
    }

    /**
     * An entry of the index that holds the
     * term frequency in all documents and
     * the postings list.
     */
    public class IndexEntry {

        private int count;
        private LinkedList<Posting> postings;

        private IndexEntry(int docId) {
            this.count = 1;
            this.postings = new LinkedList<>();
            postings.add(new Posting(docId));
        }

        private void incrementTotalTermFrequency() {
            this.count++;
        }

        public LinkedList<Posting> getPostings() {
            return this.postings;
        }

        /**
         * Adds a new Posting to the postings list if the
         * document isn't in the postings list, otherwise
         * increments term frequency in the Posting with
         * the specified document id.
         *
         * @param docId the document to update
         */
        private void updatePostings (int docId) {
            Posting post = postings.stream().filter(posting -> posting.getDocId() == docId).findAny().orElse(null);
            if (post != null) {
                post.incrementTermFrequency();
            } else {
                postings.add(new Posting(docId));
            }
        }

        public int getCount() {
            return count;
        }
    }

    /**
     * Holds the document id and the term
     * frequency in this document.
     */
    public class Posting {
        private final int docId;
        private int termFrequency;

        private Posting(int docId) {
            this.docId = docId;
            this.termFrequency = 1;
        }

        private void incrementTermFrequency() {
            this.termFrequency++;
        }

        public int getTermFrequency() {
            return termFrequency;
        }

        public int getDocId() {
            return docId;
        }
    }
}
