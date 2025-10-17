import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class WordCounterSerial {
    private final Path filePath;

    public WordCounterSerial(String filePath) {
        this.filePath = Path.of(filePath);
    }

    public Map<String, Integer> countWords() {
        Map<String, Integer> wordCounts = new HashMap<>();

        try (Stream<String> lines = Files.lines(filePath)) {
            lines.forEach(line -> { // Percorre linha a linha do arquivo
                String[] words = line.toLowerCase().split("[^a-zA-Z]+"); // Divide a linha em palavras, ignorando pontuações e números

                for (String word : words) {
                    if (!word.isEmpty()) {
                        wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1); // Atualiza a contagem da palavra no mapa
                    }
                }
            });
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }

        return wordCounts;
    }
}