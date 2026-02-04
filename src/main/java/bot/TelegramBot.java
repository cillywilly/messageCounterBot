package bot;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private CountLogic countLogic;

    @Autowired
    private TableTennis tableTennis;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;


    @Override
    public void onUpdateReceived(Update update) {
        String mes = update.getMessage().hasText() ? update.getMessage().getText() : "";
        var game = mes.contains("итог игры ");
        if (game) {
            tableTennis.addGameResult(update);
        }
        var tournament = mes.contains("дай результат турнира ");
        if (tournament) {
            tableTennis.getTournament(mes.replace("дай результат турнира ", "")
                    , this
                    , update.getMessage().getChatId().toString());
        }
        var test = mes.contains("тест");
        if (test) tableTennis.test(this, update.getMessage().getChatId().toString());
    }

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
