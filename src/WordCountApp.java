import java.util.Map;

public class WordCountApp {
    private static final String FILE_NAME = "big_text_file.txt";

    public static void main(String[] args) {
        if (!java.nio.file.Files.exists(java.nio.file.Path.of(FILE_NAME))) {
            System.err.println("ERRO: Arquivo '" + FILE_NAME + "' não encontrado.");
            System.err.println("Por favor, execute a classe BigFileGenerator primeiro para criar o arquivo.");
            return;
        }

        long startTimeSerial = System.nanoTime();
        WordCounterSerial serialCounter = new WordCounterSerial(FILE_NAME);
        Map<String, Integer> serialResult = serialCounter.countWords();
        long endTimeSerial = System.nanoTime();
        double timeSerial = (endTimeSerial - startTimeSerial) / 1_000_000_000.0;
        int totalPalavrasSerial = serialResult.values().stream().mapToInt(Integer::intValue).sum();

        System.out.println("--- RESULTADOS SERIAL ---");
        System.out.printf("Contagem Total de Palavras: %d%n", totalPalavrasSerial);
        System.out.printf("Contagem Total de Palavras Distintas: %d%n", serialResult.size());
        System.out.printf("Tempo de Execução Serial (Ts): %.3f segundos%n", timeSerial);
        printTopWords(serialResult, 3);
        System.out.println("-------------------------\n");

        int requiredThreads = 2;
        long startTimeParallel = System.nanoTime();
        WordCounterParallel parallelCounter = new WordCounterParallel(FILE_NAME);
        Map<String, Integer> parallelResult = parallelCounter.countWords(requiredThreads);
        long endTimeParallel = System.nanoTime();
        double timeParallel = (endTimeParallel - startTimeParallel) / 1_000_000_000.0;
        int totalPalavrasParallel = parallelResult.values().stream().mapToInt(Integer::intValue).sum();

        if (serialResult.equals(parallelResult)) {
            System.out.println("Os resultados Serial e Paralelo (2 threads) são IDÊNTICOS.");
        } else {
            System.out.println("ATENÇÃO! Os resultados Serial e Paralelo NÃO são idênticos.");
        }

        System.out.println("--- RESULTADOS PARALELO (2 THREADS) ---");
        System.out.printf("Contagem Total de Palavras: %d%n", totalPalavrasParallel);
        System.out.printf("Contagem Total de Palavras Distintas: %d%n", parallelResult.size());
        System.out.printf("Tempo de Execução Paralela (Tp): %.3f segundos%n", timeParallel);
        double speedUp2Threads = timeSerial / timeParallel;
        System.out.printf("Speed-up (S2): %.2fx%n", speedUp2Threads);
        System.out.println("----------------------------------------\n");

        System.out.println("--- DESAFIO: ANÁLISE DE SPEED-UP ---");

        double[] threadCounts = {1, 2, 4, 8, 16};

        double T1 = timeSerial;
        System.out.printf("Número de Threads (N) | Tempo (TN) (s) | Speed-Up (S = T1/TN) | Eficiência (E = S/N)%n");
        System.out.printf("----------------------|----------------|----------------------|-----------------------%n");
        System.out.printf("%21d | %14.3f | %20.2f | %22.2f%n", 1, T1, 1.0, 1.0);

        for (int i = 1; i < threadCounts.length; i++) {
            int N = (int) threadCounts[i];

            long start = System.nanoTime();
            parallelCounter.countWords(N);
            long end = System.nanoTime();

            double TN = (end - start) / 1_000_000_000.0;
            double SN = T1 / TN;
            double EN = SN / N;

            System.out.printf("%21d | %14.3f | %20.2f | %22.2f%n", N, TN, SN, EN);

            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }

        System.out.println("--------------------------------------------------------------------------------\n");
    }

    private static void printTopWords(Map<String, Integer> results, int limit) {
        results.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .forEach(entry -> System.out.printf("Top Palavra: '%s' = %d%n", entry.getKey(), entry.getValue()));
    }
}