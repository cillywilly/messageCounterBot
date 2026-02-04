package bot.dataBase.dao;

import bot.dataBase.entity.Client;
import bot.util.TimeUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.meta.api.objects.Update;

@Repository
public class ClientDAOImpl implements ClientDAO {

    @Autowired
    EntityManager entityManager;

    @Override
    @Transactional
    public Client getClient(String chatId) {
        Session session = entityManager.unwrap(Session.class);
        return session.get(Client.class, chatId);
    }

    @Override
    @Transactional
    public void addClick(Update update) {
        Session session = entityManager.unwrap(Session.class);

        var from = update.getMessage().getFrom();
        var chatId = update.getMessage().getChatId().toString();
        var client = getClient(chatId);
        if (null == client) {
            client = new Client();
            client.setFirstName(from.getFirstName());
            client.setLastName(from.getLastName());
            client.setUsername(from.getUserName());
            client.setChatId(chatId);
            client.setLastLoginDate(TimeUtil.getNow());
            client.setMessagesCount(1);

            session.persist(client);
        } else {
            if (null == client.getMessagesCount()) {
                client.setMessagesCount(0);
            }
            client.setMessagesCount(client.getMessagesCount() + 1);
            session.merge(client);
        }
    }
}
