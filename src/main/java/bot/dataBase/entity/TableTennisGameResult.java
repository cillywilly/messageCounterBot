package bot.dataBase.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@ToString
@NoArgsConstructor
@Table(name = "table_tennis_game_result")
public class TableTennisGameResult {

    @Id
    @Column(name = "game_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(name = "player")
    private String player;

    @Column(name = "opponent")
    private String opponent;

    @Column(name = "player_score")
    private Integer playerScore;

    @Column(name = "opponent_score")
    private Integer opponentScore;

    @Column(name = "tournament_name")
    private String tournamentName;

}
