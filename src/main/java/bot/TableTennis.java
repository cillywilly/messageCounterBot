package bot;

import bot.dataBase.dao.GameResultDAO;
import bot.dataBase.entity.TableTennisGameResult;
import bot.util.TournamentResultUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class TableTennis {

    public static final String TOURNAMENT_NAME = "1";
    @Autowired
    GameResultDAO gameResultDAO;
    @Autowired
    TournamentResultUtil tournamentResultUtil;
    @Autowired
    ResourceLoader resourceLoader;
    TelegramBot telegramBot;
    private String player1;
    private String player2;
    private Integer player1Score;
    private Integer player2Score;

    public void send(String message, String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode("Markdown");
        myExecute(sendMessage);
    }

    public void trySendPhoto(byte[] imgAddress, String chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        myExecute(imgAddress, sendPhoto);
    }

    @SneakyThrows
    private void myExecute(byte[] imgAddress, SendPhoto sendPhoto) {
        try {
            InputFile photoFile = new InputFile(
                    new ByteArrayInputStream(imgAddress),
                    "table_" + System.currentTimeMillis() + ".png"
            );
            sendPhoto.setPhoto(photoFile);
            telegramBot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void myExecute(SendMessage sendMessage) {
        try {
            Thread.sleep(500);
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getTournament(String tournamentName, TelegramBot telegramBot, String chatId) {
        var tournament = gameResultDAO.getTournament(tournamentName);
//        var res = tournamentResultUtil.getFormattedResult(tournament);
        var resultPng = tournamentResultUtil.getFormattedResultPng(tournament);
        this.telegramBot = telegramBot;
        trySendPhoto(resultPng, chatId);
    }

    public void addGameResult(Update update) {
        if (update.hasMessage()) {
            var mes = update.getMessage().getText().replace("итог игры ", "");
            var resultMes = mes.split(" ");
            try {
                gameResultChecks(resultMes);
                var gameResult = new TableTennisGameResult();
                gameResult.setPlayer(player1);
                gameResult.setPlayerScore(player1Score);
                gameResult.setOpponent(player2);
                gameResult.setOpponentScore(player2Score);
                gameResult.setTournamentName(TOURNAMENT_NAME);
                gameResultDAO.addResult(gameResult);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void gameResultChecks(String[] result) {
        if (result.length != 4) {
            var e = "не верный формат";
            log.error(e);
            throw new RuntimeException(e);
        }
        player1 = result[0];
        player2 = result[1];
        player1Score = Integer.parseInt(result[2]);
        player2Score = Integer.parseInt(result[3]);
        if (player1Score + player2Score != 3
                && player1Score + player2Score != 4
                && player1Score + player2Score != 5
                && (player1Score == 2 && player2Score == 2)
        ) {
            var e = "не верная сумма очков";
            log.error(e);
            throw new RuntimeException(e);
        }
        if (player1.equals(player2)) {
            var e = "одинаковые фамилии";
            log.error(e);
            throw new RuntimeException(e);
        }
        if (gameResultDAO.hasGame(player1, player2) || gameResultDAO.hasGame(player2, player1)) {
            var e = "уже есть результат";
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public void test(TelegramBot telegramBot, String chatId) {
        loadTestData();
        getTournament(TOURNAMENT_NAME, telegramBot, chatId);
    }

    private void loadTestData() {
        List<TableTennisGameResult> testData = Arrays.asList(
                createGame("Илларионов", "Гончаров", 3, 1),
                createGame("Шорин", "Григорьев", 2, 3),
                createGame("Водяхин", "Шорин", 3, 0),
                createGame("Шорин", "Илларионов", 3, 2),
                createGame("Гончаров", "Шорин", 1, 3),
                createGame("Григорьев", "Водяхин", 3, 1),
                createGame("Сидоров", "Петров", 0, 3),
                createGame("Петров", "Сидоров", 2, 3),
                createGame("Галкин", "Петров", 3, 1),
                createGame("Иванов", "Орлов", 3, 1),
                createGame("Кузнецов", "Никитин", 3, 1),
                createGame("Попов", "Степанов", 3, 1),
                createGame("Васильев", "Козлов", 3, 1),
                createGame("Морозов", "Егоров", 3, 1),
                createGame("Николаев", "Дмитриев", 3, 1),
                createGame("Михайлов", "Борисов", 3, 1),
                createGame("Фёдоров", "Григорьев", 3, 1),
                createGame("Алексеев", "Лебедев", 3, 1),
                createGame("Лебедев", "Алексеев", 1, 3),
                createGame("Григорьев", "Фёдоров", 3, 1),
                createGame("Борисов", "Михайлов", 3, 1),
                createGame("Дмитриев", "Николаев", 3, 1),
                createGame("Егоров", "Морозов", 3, 1),
                createGame("Павлов", "Петров", 3, 1),
                createGame("Козлов", "Степанов", 3, 1),
                createGame("Степанов", "Никитин", 3, 1),
                createGame("Никитин", "Орлов", 3, 1),
                createGame("Орлов", "Титов", 3, 1),
                createGame("Титов", "Андреев", 3, 1),
                createGame("Макаров", "Лазарев", 3, 1),
                createGame("Лазарев", "Макаров", 3, 1)
        );

        for (TableTennisGameResult game : testData) {
            gameResultDAO.addResult(game);
        }

        System.out.println("✅ Тестовые данные успешно загружены в базу данных.");
    }

    private TableTennisGameResult createGame(String player, String opponent, int playerScore, int opponentScore) {
        TableTennisGameResult game = new TableTennisGameResult();
        game.setPlayer(player);
        game.setOpponent(opponent);
        game.setPlayerScore(playerScore);
        game.setOpponentScore(opponentScore);
        game.setTournamentName("1"); // можно оставить или убрать
        return game;
    }

}
