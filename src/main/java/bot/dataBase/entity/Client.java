package bot.dataBase.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Setter
@Getter
@Entity
@ToString
@Table(name = "client")
public class Client {

    @Id
    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "last_login_date")
    private String lastLoginDate;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String username;

    @ColumnDefault("0")
    @Column(name = "messages_count")
    private Integer messagesCount;

}
