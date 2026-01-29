package bot.dataBase.dao;

import bot.dataBase.entity.Client;
import jakarta.transaction.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ClientDAO {

    @Transactional
    Client getClient(String chatId);

    @Transactional
    void addClick(Update chatId);
}
