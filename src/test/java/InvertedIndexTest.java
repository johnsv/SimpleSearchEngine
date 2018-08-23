import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class InvertedIndexTest {
    private static LinkedList<Document> documents;
    static Parser parser = new Parser();
    static InvertedIndex invertedIndex;

    @BeforeClass
    public static void testSetup() {
        LinkedList<String> documentContents = new LinkedList<>();
        documentContents.add("the brown fox jumped over the brown dog");
        documentContents.add("the lazy brown dog sat in the corner");
        documentContents.add("the red fox bit the lazy dog");
        LinkedList<Document> documents = new LinkedList<>();
        AtomicInteger docId = new AtomicInteger(0);
        documentContents.forEach(documentContent -> {
            documents.add(new Document(docId.getAndIncrement(), documentContent));
        });
        invertedIndex = parser.createInvertedIndex(parser.createTokenDocumentIdPairs(documents), documents);

    }

    @Test
    public void shouldCreateIndex() {
        assertNotNull(invertedIndex);
    }

    @Test
    public void shouldHaveAddedTerms() {
        assertTrue(invertedIndex.getIndex().containsKey("fox"));
        assertTrue(invertedIndex.getIndex().containsKey("red"));
        assertTrue(invertedIndex.getIndex().containsKey("brown"));
    }

    @Test
    public void shouldSortSingleTermQueryInTfOrder() {
        LinkedList<InvertedIndex.Posting> queryResults = invertedIndex.singleTermQuery("brown");
        assertEquals(0, queryResults.get(0).getDocId());
        assertEquals(1, queryResults.get(1).getDocId());

        queryResults = invertedIndex.singleTermQuery("fox");
        assertEquals(0, queryResults.get(0).getDocId());
        assertEquals(2, queryResults.get(1).getDocId());
    }



}