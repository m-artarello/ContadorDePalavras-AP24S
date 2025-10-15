import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WordCounterParallel {
    private final Path filePath;

    public WordCounterParallel(String filePath) {
        this.filePath = Path.of(filePath);
    }

    public Map<String, Integer> countWords(int numThreads) {
        List<String> allLines;
        try {
            allLines = Files.readAllLines(filePath);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return new HashMap<>();
        }

        int totalLines = allLines.size();
        int linesPerThread = totalLines / numThreads;

        List<Callable<Map<String, Integer>>> tasks = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            int start = i * linesPerThread;
            int end = (i == numThreads - 1) ? totalLines : (i + 1) * linesPerThread;

            List<String> subList = allLines.subList(start, end);

            tasks.add(new WordCounterTask(subList));
        }

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        Map<String, Integer> finalCounts = new HashMap<>();

        try {
            List<Future<Map<String, Integer>>> futures = executor.invokeAll(tasks);

            for (Future<Map<String, Integer>> future : futures) {
                Map<String, Integer> localCounts = future.get();

                localCounts.forEach((word, count) ->
                        finalCounts.merge(word, count, Integer::sum)
                );
            }

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Erro durante a execução paralela: " + e.getMessage());
        } finally {
            executor.shutdown();
        }

        return finalCounts;
    }
}