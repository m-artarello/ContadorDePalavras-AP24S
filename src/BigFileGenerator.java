import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class BigFileGenerator {
    private static final String FILE_NAME = "big_text_file.txt";
    private static final long TARGET_SIZE_BYTES = 2L * 1024 * 1024 * 1024;

    private static final String[] SAMPLE_WORDS = {
            "java", "thread", "computacao", "paralela", "serial", "desempenho",
            "otimizacao", "arquivo", "palavra", "contagem", "speedup", "multithreading"
    };
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        System.out.println("Iniciando a geração do arquivo grande...");
        long startTime = System.currentTimeMillis();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            long currentSize = 0;
            int wordIndex = 0;

            while (currentSize < TARGET_SIZE_BYTES) {
                String word = SAMPLE_WORDS[RANDOM.nextInt(SAMPLE_WORDS.length)];

                writer.write(word);
                currentSize += word.length();

                writer.write(" ");
                currentSize += 1;

                if (++wordIndex % 20 == 0) {
                    writer.newLine();
                    currentSize += System.lineSeparator().length();
                }

                if (currentSize % (100 * 1024 * 1024) < word.length()) {
                    System.out.printf("Progresso: %.2f MB%n", currentSize / (1024.0 * 1024.0));
                }
            }

            System.out.printf("Arquivo '%s' gerado com sucesso! Tamanho final: %.2f MB%n",
                    FILE_NAME, currentSize / (1024.0 * 1024.0));
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.printf("Tempo total de geração: %.2f segundos%n", (endTime - startTime) / 1000.0);
    }
}