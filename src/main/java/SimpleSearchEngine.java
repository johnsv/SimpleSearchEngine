import java.util.LinkedList;
import java.util.Scanner;

/**
 * A simple search engine capable of doing single term queries
 * in a document set. The search results is sorted by tf-idf.
 *
 */
public class SimpleSearchEngine {

    String[] documents;
    public static void main(String[] args) {
        if (args.length > 0 ) {
            SimpleSearchEngine simpleSearchEngine = new SimpleSearchEngine(args[0]);
        } else {
            System.out.println("No file was specified, exiting...");
        }
    }

    public SimpleSearchEngine(String fileName) {
        Parser parser = new Parser();
        InvertedIndex invertedIndex;
        LinkedList<String> documents = parser.readFile(fileName);
        LinkedList<Parser.Pair<String, Integer>> tokenDocumentPairs = parser.createTokenDocumentPairs(documents);

        invertedIndex = parser.createInvertedIndex(tokenDocumentPairs);

        query(invertedIndex, false);
    }

    private void query(InvertedIndex invertedIndex, boolean caseSensitive) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Write query: ");

        while(scanner.hasNext()) {
            String query = caseSensitive ? scanner.next() : scanner.next().toLowerCase();

            if(!query.equals(".")){
                LinkedList<InvertedIndex.Posting> queryResult = invertedIndex.singleTermQuery(query);
                if (queryResult != null) {
                    System.out.println(query + " was found in document: ");
                    queryResult.forEach(posting -> System.out.println(posting.getDocId() + " count: " + posting.getTermFrequency()));
                }
                System.out.print("\nWrite new query: ");

            } else {
                System.out.println("exiting...");
                break;
            }
        }
    }

    private void printIndex(InvertedIndex invertedIndex) {
        invertedIndex.getIndex().keySet().iterator().forEachRemaining(key -> {
            System.out.println(key + ": " + invertedIndex.getIndex().get(key).getCount());
            invertedIndex.getIndex().get(key).getPostings().forEach(posting -> {
                System.out.println("\t docId: " + posting.getDocId() + " count: " + posting.getTermFrequency());
            });
        });
    }
}
