import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;

public class InvertedIndexTest {
    private static LinkedList<String> documents;
    static Parser parser = new Parser();
    static InvertedIndex invertedIndex;

    @BeforeClass
    public static void testSetup() {
        documents = new LinkedList<>();
        documents.add("the brown fox jumped over the brown dog");
        documents.add("the lazy brown dog sat in the corner");
        documents.add("the red fox bit the lazy dog");
        invertedIndex = parser.createInvertedIndex(parser.createTokenDocumentPairs(documents));
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