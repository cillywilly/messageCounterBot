package bot.util;

import bot.dataBase.entity.TableTennisGameResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TournamentResultUtil {

    @Autowired
    HtmlToImageConverter converter;

    private static StringBuilder getFinishResultTable(List<String> sortedPlayers, Map<String, Integer> wins, Map<String, Integer> losses, Map<String, Integer> points) {
        // Итоговая таблица мест
        StringBuilder sb = new StringBuilder();
        sb.append("*Итоговая таблица:*\n");
        for (int i = 0; i < sortedPlayers.size(); i++) {
            String p = sortedPlayers.get(i);
            sb.append(i + 1).append(".").append(p).append(" ")
                    .append("Побед:").append(wins.get(p))
                    .append(", Пораж.:").append(losses.get(p))
                    .append(" *").append(points.get(p)).append(" очк.*\n");
        }
        return sb;
    }

    public byte[] getFormattedResultPng(List<TableTennisGameResult> results) {
        try {
            var formattedResult = getFormattedResult(results);
            // Генерируем HTML
            String html = converter.generateTournamentTableHtml(formattedResult);
            // Конвертируем в изображение
            return converter.convertHtmlToImage(html, 7000, 2000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String[][] getFormattedResult(List<TableTennisGameResult> results) {
        if (results == null || results.isEmpty()) {
            return new String[][]{{"📊 Таблица результатов пуста."}};
        }

        Set<String> players = results.stream()
                .flatMap(r -> Arrays.asList(r.getPlayer(), r.getOpponent()).stream())
                .collect(Collectors.toSet());

        // Создаем матрицу встреч: player1 -> player2 -> результат
        Map<String, Map<String, String>> matchResults = new HashMap<>();
        Map<String, Integer> wins = new HashMap<>();
        Map<String, Integer> losses = new HashMap<>();

        for (String player : players) {
            matchResults.put(player, new HashMap<>());
            wins.put(player, 0);
            losses.put(player, 0);
        }

        // Заполняем результаты матчей
        for (TableTennisGameResult result : results) {
            String p1 = result.getPlayer();
            String p2 = result.getOpponent();
            int score1 = result.getPlayerScore();
            int score2 = result.getOpponentScore();

            String resStr = score1 + "–" + score2;
            matchResults.get(p1).put(p2, resStr);
            matchResults.get(p2).put(p1, score2 + "–" + score1); // обратная запись

            if (score1 > score2) {
                wins.put(p1, wins.get(p1) + 1);
                losses.put(p2, losses.get(p2) + 1);
            } else if (score1 < score2) {
                wins.put(p2, wins.get(p2) + 1);
                losses.put(p1, losses.get(p1) + 1);
            }
        }

        // Считаем очки:
        Map<String, Integer> points = new HashMap<>();
        for (String player : players) {
            final Integer[] totalPoints = {0};
            results.stream().filter(game -> game.getPlayer().equals(player))
                    .forEach(game -> totalPoints[0] += game.getPlayerScore());
            results.stream().filter(game -> game.getOpponent().equals(player))
                    .forEach(game -> totalPoints[0] += game.getOpponentScore());
            points.put(player, totalPoints[0]);
        }

        // Сортируем по очкам (по убыванию)
        List<String> sortedPlayers = new ArrayList<>(players);
        sortedPlayers.sort((a, b) -> {
            int cmp = points.get(b).compareTo(points.get(a));
            return cmp;
            // При равенстве очков — по разнице побед/поражений (можно усложнить)
        });

//        StringBuilder sb = getResultAsString(sortedPlayers, matchResults, wins, losses, points);//соберет таблицу в строку

        String[][] table = getMatchResultsTable(results);

        return table;
    }

    /**
     * Возвращает таблицу "каждый с каждым" в формате String[][],
     * где строки — игроки, столбцы — соперники, ячейки — результаты матчей.
     */
    public String[][] getMatchResultsTable(List<TableTennisGameResult> results) {
        if (results == null || results.isEmpty()) {
            return new String[][]{{"📊 Таблица результатов пуста."}};
        }

        // Собираем всех игроков и сортируем для стабильного порядка
        Set<String> playersSet = results.stream()
                .flatMap(r -> Arrays.asList(r.getPlayer(), r.getOpponent()).stream())
                .collect(Collectors.toSet());

        List<String> players = new ArrayList<>(playersSet);
        players.sort(String::compareTo);

        // Создаем матрицу встреч
        Map<String, Map<String, String>> matchResults = new HashMap<>();
        for (String player : players) {
            matchResults.put(player, new HashMap<>());
            matchResults.get(player).put(player, "–"); // диагональ — сам с собой
        }

        // Заполняем результаты
        for (TableTennisGameResult result : results) {
            String p1 = result.getPlayer();
            String p2 = result.getOpponent();
            int s1 = result.getPlayerScore();
            int s2 = result.getOpponentScore();

            matchResults.get(p1).put(p2, s1 + "–" + s2);
            matchResults.get(p2).put(p1, s2 + "–" + s1); // обратная запись
        }

        // Формируем двумерный массив: +1 строка на заголовок
        String[][] table = new String[players.size() + 1][players.size() + 1];

        // Заголовок
        table[0][0] = "Игрок";
        for (int i = 0; i < players.size(); i++) {
            table[0][i + 1] = players.get(i);
        }

        // Строки таблицы
        for (int i = 0; i < players.size(); i++) {
            String player = players.get(i);
            table[i + 1][0] = player;

            for (int j = 0; j < players.size(); j++) {
                String opponent = players.get(j);
                table[i + 1][j + 1] = matchResults.get(player).getOrDefault(opponent, "");
            }
        }

        return table;
    }

    private int getLosses(String player, List<TableTennisGameResult> results) {
        return (int) results.stream().filter(game ->
                (game.getPlayer().equals(player) && game.getPlayerScore() < game.getOpponentScore()) ||
                        (game.getOpponent().equals(player) && game.getPlayerScore() > game.getOpponentScore())
        ).count();
    }

    private String[][] getTableForPng1(List<String> sortedPlayers, Map<String, Integer> wins, Map<String, Integer> losses, Map<String, Integer> points) {
        StringBuilder sb = new StringBuilder();
        // Заголовок таблицы
        int maxPlayerLength = maxPlayerSurnameLength(sortedPlayers);
        sb.append(" ".repeat(maxPlayerLength));
        for (String player : sortedPlayers) {
            sb.append("|").append(padRight(player.substring(0, Math.min(3, player.length())), 3));
        }
        sb.append("\n");
        sb.append("-".repeat(44)).append("\n");

        // Строки таблицы
        for (String player : sortedPlayers) {
            sb.append(player).append(" ".repeat(maxPlayerLength - player.length()));
            for (String player2 : sortedPlayers) {
                if (player.equals(player2)) {
                    sb.append("| – ");
                } else {
//                    String res = matchResults.get(player).getOrDefault(player2, "   ");
//                    sb.append("|").append(padRight(res, 3));
                }
            }
            sb.append("\n");
        }
        return null;
    }

    @NotNull
    private StringBuilder getResultAsString(List<String> sortedPlayers, Map<String, Map<String, String>> matchResults, Map<String, Integer> wins, Map<String, Integer> losses, Map<String, Integer> points) {
        StringBuilder sb = new StringBuilder();
        sb.append("```\n"); // Начало блока кода для моноширинного шрифта
        sb.append("🏆 *Круговой турнир по настольному теннису*\n\n");
        sb.append("*Участники:* ").append(String.join(", ", sortedPlayers)).append("\n\n");

        // Заголовок таблицы
        int maxPlayerLength = maxPlayerSurnameLength(sortedPlayers);
        sb.append(" ".repeat(maxPlayerLength));
        for (String player : sortedPlayers) {
            sb.append("|").append(padRight(player.substring(0, Math.min(3, player.length())), 3));
        }
        sb.append("\n");
        sb.append("-".repeat(44)).append("\n");

        // Строки таблицы
        for (String player : sortedPlayers) {
            sb.append(player).append(" ".repeat(maxPlayerLength - player.length()));
            for (String player2 : sortedPlayers) {
                if (player.equals(player2)) {
                    sb.append("| – ");
                } else {
                    String res = matchResults.get(player).getOrDefault(player2, "   ");
                    sb.append("|").append(padRight(res, 3));
                }
            }
            sb.append("\n");
        }
        sb.append("\n");

        sb.append(getFinishResultTable(sortedPlayers, wins, losses, points));
        sb.append("```");
        return sb;
    }

    private int maxPlayerSurnameLength(List<String> players) {
        var res = 0;
        for (String player : players) {
            if (res < player.length()) {
                res = player.length();
            }
        }
        return res;
    }

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}