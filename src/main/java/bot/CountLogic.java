package bot;

import bot.dataBase.dao.ClientDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CountLogic {

    @Autowired
    private ClientDAO clientDAO;

    public void updateCount(Update update) {
        if (update.hasMessage()) {
            clientDAO.addClick(update);
        }
    }
}