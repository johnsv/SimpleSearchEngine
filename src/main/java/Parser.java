import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Class that reads documents from a text file
 * and converts these to an inverted index
 */
public class Parser {

    /**
     * Reads text file and returns its lines.
     *
     * @param fileName input file name
     * @return a LinkedList of strings that are the lines of the input file
     */
    public LinkedList<String> readFile(String fileName) {
        LinkedList<String> lines = new LinkedList<String>();

        try (Stream<String> stream = Files.lines(Paths.get(fileName))){
            stream.forEach(line -> {
                lines.add(line);
            });
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * Returns a sorted list of token-document pairs
     * created from the documents.
     *
     * @param documents
     * @return a sorted list of <String, Integer> pairs.
     */
    public LinkedList<Pair<String, Integer>> createTokenDocumentPairs(LinkedList<String> documents) {
        String spaceDelimiter = " ";
        LinkedList<Pair<String, Integer>> tokenDocumentPairs = new LinkedList<>();

        AtomicInteger documentId = new AtomicInteger(0);
        documents.forEach(line -> {
            Arrays.stream(line.split(spaceDelimiter)).forEach(token -> {
                tokenDocumentPairs.add(new Pair<>(token, documentId.get()));
            });
            documentId.incrementAndGet();
        });

        tokenDocumentPairs.sort((o1, o2) -> {
            return sortTokenDocumentPair(o1, o2);
        });

        return tokenDocumentPairs;
    }


    /**
     * Sort the pair first alphabetically on token value
     * then on document id.
     *
     * @param o1 Object 1
     * @param o2 Object 2
     */
    private int sortTokenDocumentPair(Pair<String, Integer> o1, Pair<String, Integer> o2) {
        if (o1.getL().compareTo(o2.getL()) > 0) {
            return 1;
        } else if (o1.getL().compareTo(o2.getL()) < 0) {
            return -1;
        } else {
            if (o1.getR() < o2.getR()) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    /**
     * Creates an inverted index from token-document pairs.
     *
     * @param tokenDocumentPairs
     */
    public InvertedIndex
    createInvertedIndex(LinkedList<Pair<String, Integer>> tokenDocumentPairs) {
        InvertedIndex invertedIndex = new InvertedIndex();

        Iterator<Pair<String, Integer>> iterator = tokenDocumentPairs.iterator();

        Pair<String, Integer> tokenAndDocument;
        while(iterator.hasNext()) {
            tokenAndDocument = iterator.next();
            invertedIndex.add(tokenAndDocument.getL(), tokenAndDocument.getR(), false);
        }
        invertedIndex.sortIndex();

        return invertedIndex;
    }

    /**
     * A class representing a pair
     * @param <L> Left member
     * @param <R> Right member
     */
    public static class Pair<L,R> {
        private L l;
        private R r;
        public Pair(L l, R r){
            this.l = l;
            this.r = r;
        }
        public L getL(){ return l; }
        public R getR(){ return r; }
        public void setL(L l){ this.l = l; }
        public void setR(R r){ this.r = r; }
    }
}
