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
            allLines = Files.readAllLines(filePath); // Le todas as linhas do arquivo de uma so vez
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return new HashMap<>();
        }

        int totalLines = allLines.size();
        int linesPerThread = totalLines / numThreads; // Divide as linhas igualmente entre as threads

        List<Callable<Map<String, Integer>>> tasks = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) { // Define as tarefas para cada thread
            int start = i * linesPerThread; // Multiplica o thread index pelo numero de linhas por thread
            int end = (i == numThreads - 1) ? totalLines : (i + 1) * linesPerThread;  // A ultima thread pega o resto das linhas

            List<String> subList = allLines.subList(start, end); // Pega a sublista de linhas para a thread atual

            tasks.add(new WordCounterTask(subList)); // Adiciona a tarefa na lista de tarefas
        }

        ExecutorService executor = Executors.newFixedThreadPool(numThreads); // Cria o pool de threads
        Map<String, Integer> finalCounts = new HashMap<>();

        try {
            List<Future<Map<String, Integer>>> futures = executor.invokeAll(tasks); // Executa todas as tarefas e obtém os futuros

            for (Future<Map<String, Integer>> future : futures) {
                Map<String, Integer> localCounts = future.get();

                localCounts.forEach((word, count) ->
                        finalCounts.merge(word, count, Integer::sum) // Combina as contagens locais na contagem final
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