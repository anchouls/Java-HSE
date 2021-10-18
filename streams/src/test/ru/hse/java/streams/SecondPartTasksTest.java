package ru.hse.java.streams;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;


public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() throws IOException {
        List<String> actual = SecondPartTasks.findQuotes(Arrays.asList("src/test/resources/file1",
                "src/test/resources/file2", "src/test/resources/file3"), "from");
        List<String> expected = Arrays.asList("from his lantern dancing from", "from the barrel in the",
                "years he had not risen from", "chewing betel from a lacquered box on the table, and thinking about",
                "from April to November.");
        Assertions.assertEquals(expected, actual);
        actual = SecondPartTasks.findQuotes(Arrays.asList("src/test/resources/file1",
                "src/test/resources/file2", "src/test/resources/file3"), "year");
        expected = Collections.singletonList("years he had not risen from");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testFindQuotesInvalidFile() {
        Assertions.assertThrows(IOException.class, () -> SecondPartTasks.findQuotes(Arrays.asList("src/test/resources/file1",
                "src/test/resources/file4"), "from"));
    }

    @Test
    public void testPiDividedBy4() {
        double scale = Math.pow(10, 2);
        Assertions.assertEquals(Math.ceil(Math.PI / 4 * scale) / scale,
                Math.ceil(SecondPartTasks.piDividedBy4() * scale) / scale);
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> compositions = new HashMap<>();
        compositions.put("Ray Douglas Bradbury",
                Arrays.asList("The Halloween Tree, Bradbury’s tribute to his favorite holiday",
                        "What could be a better Halloween read than the story",
                        "As the weather turns crisp on the twenty-third of October",
                        "This collection of the macabre and magical celebrates its",
                        "Disney’s production of Ray Bradbury’s book retains the poetry of the ",
                        "Ray Bradbury wrote and narrated this Emmy Award winning 1993 "));
        compositions.put("Daniel Keyes",
                Arrays.asList("FLOWERS FOR ALGERNON",
                        "Dr Strauss says I shoud rite down what",
                        "I had a test today.",
                        "I think I faled it and I think mabye now they wont use me.",
                        "He had a wite coat like a docter"));
        compositions.put("Oscar Fingal O'Flahertie Wills Wilde",
                Arrays.asList("Wilde was born of professional and literary parents.",
                        "In the early 1880s, when Aestheticism was the rage and despair of literary London",
                        "In the final decade of his life, Wilde wrote and published nearly all of his major work."));
        compositions.put("Joanne Rowling",
                Arrays.asList("Harry Potter",
                        "Mr. and Mrs. Dursley, of number four, Privet Drive",
                        "None of them noticed a large, tawny owl flutter past the window.",
                        "The Potters, that's right, that's what I heard",
                        "yes, their son, Harry"));
        Assertions.assertEquals("Ray Douglas Bradbury", SecondPartTasks.findPrinter(compositions));
    }

    @Test
    public void testCalculateGlobalOrder() {
        Map<String, Integer> order1 = new HashMap<>();
        order1.put("product1", 6);
        order1.put("product2", 3);
        order1.put("product3", 5);
        order1.put("product4", 1);
        order1.put("product5", 3);
        Map<String, Integer> order2 = new HashMap<>();
        order2.put("product1", 8);
        order2.put("product2", 9);
        order2.put("product3", 2);
        order2.put("product4", 1);
        order2.put("product5", 6);
        Map<String, Integer> order3 = new HashMap<>();
        order3.put("product1", 4);
        order3.put("product2", 5);
        order3.put("product3", 5);
        order3.put("product4", 6);
        order3.put("product5", 3);
        Map<String, Integer> order4 = new HashMap<>();
        order4.put("product1", 9);
        order4.put("product2", 8);
        order4.put("product3", 2);
        order4.put("product4", 6);
        order4.put("product5", 7);
        Map<String, Integer> order5 = new HashMap<>();
        order5.put("product1", 4);
        order5.put("product2", 1);
        order5.put("product3", 5);
        order5.put("product4", 7);
        order5.put("product5", 5);
        Map<String, Integer> expected = new HashMap<>();
        expected.put("product1", 31);
        expected.put("product2", 26);
        expected.put("product3", 19);
        expected.put("product4", 21);
        expected.put("product5", 24);
        Map<String, Integer> answer = SecondPartTasks.calculateGlobalOrder(
                Arrays.asList(order1, order2, order3, order4, order5));
        Assertions.assertEquals(expected, answer);
    }
}