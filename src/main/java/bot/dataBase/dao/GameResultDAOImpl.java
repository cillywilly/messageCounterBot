package bot.dataBase.dao;

import bot.dataBase.entity.TableTennisGameResult;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GameResultDAOImpl implements GameResultDAO {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public boolean hasGame(String playerName, String opponentName) {
        Session session = entityManager.unwrap(Session.class);
        Long count = session.createQuery(
                        "SELECT COUNT(g) FROM TableTennisGameResult g WHERE g.player = :playerName AND g.opponent = :opponentName",
                        Long.class)
                .setParameter("playerName", playerName)
                .setParameter("opponentName", opponentName)
                .uniqueResult();
        return count > 0;
    }

    @Override
    @Transactional
    public void addResult(TableTennisGameResult result) {
        Session session = entityManager.unwrap(Session.class);
        session.persist(result);
    }

    @Override
    @Transactional
    public List<TableTennisGameResult> getTournament(String tournamentName) {
        Session session = entityManager.unwrap(Session.class);
        return session.createQuery(
                        "FROM TableTennisGameResult g WHERE g.tournamentName = :tournamentName",
                        TableTennisGameResult.class)
                .setParameter("tournamentName", tournamentName)
                .getResultList();
    }
}