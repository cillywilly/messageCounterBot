package bot.dataBase.dao;

import bot.dataBase.entity.TableTennisGameResult;
import jakarta.transaction.Transactional;

import java.util.List;

public interface GameResultDAO {

    @Transactional
    boolean hasGame(String playerName, String opponentName);

    @Transactional
    void addResult(TableTennisGameResult result);

    @Transactional
    List<TableTennisGameResult> getTournament(String tournamentName);
}
