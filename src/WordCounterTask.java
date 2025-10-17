import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class WordCounterTask implements Callable<Map<String, Integer>> { // Tarefa que conta palavras em uma lista de linhas
    private final List<String> lines;

    public WordCounterTask(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public Map<String, Integer> call() throws Exception {
        Map<String, Integer> localCounts = new HashMap<>();

        for (String line : lines) {
            String[] words = line.toLowerCase().split("[^a-zA-Z]+");

            for (String word : words) {
                if (!word.isEmpty()) {
                    localCounts.put(word, localCounts.getOrDefault(word, 0) + 1);
                }
            }
        }

        return localCounts;
    }
}