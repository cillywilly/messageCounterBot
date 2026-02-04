package bot.util;

import bot.dataBase.entity.TableTennisGameResult;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TournamentResultUtil {

    public String getFormattedResult(List<TableTennisGameResult> results) {
        if (results == null || results.isEmpty()) {
            return "📊 Таблица результатов пуста.";
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

        StringBuilder sb = new StringBuilder();
        sb.append("```\n"); // Начало блока кода для моноширинного шрифта
        sb.append("🏆 *Круговой турнир по настольному теннису*\n\n");
        sb.append("*Участники:* ").append(String.join(", ", sortedPlayers)).append("\n\n");

        // Заголовок таблицы
        int maxPlayerLength = maxPlayerLength(sortedPlayers);
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

        // Итоговая таблица мест
        sb.append("*Итоговая таблица:*\n");
        for (int i = 0; i < sortedPlayers.size(); i++) {
            String p = sortedPlayers.get(i);
            sb.append(i + 1).append(".").append(p).append(" ")
                    .append("Побед:").append(wins.get(p))
                    .append(", Пораж.:").append(losses.get(p))
                    .append(" *").append(points.get(p)).append(" очк.*\n");
        }
        sb.append("```"); // Начало блока кода для моноширинного шрифта

        return sb.toString();
    }

    private int maxPlayerLength(List<String> players) {
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