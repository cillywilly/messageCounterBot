import bot.dataBase.dao.ClientDAOImpl;
import bot.dataBase.entity.Client;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetClientTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Session session;

    @InjectMocks
    private ClientDAOImpl clientDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
    }

    @Test
    void getClient_shouldReturnClient_whenClientExistsInDB() {
        String chatId = "12345";
        Client expectedClient = new Client();
        expectedClient.setChatId(chatId);
        expectedClient.setFirstName("John");

        when(session.get(Client.class, chatId)).thenReturn(expectedClient);

        Client actualClient = clientDAO.getClient(chatId);

        assertNotNull(actualClient);
        assertEquals(chatId, actualClient.getChatId());
        assertEquals("John", actualClient.getFirstName());
        verify(session, times(1)).get(eq(Client.class), eq(chatId));
    }

    @Test
    void getClient_shouldReturnNull_whenClientDoesNotExist() {
        String chatId = "99999";

        when(session.get(Client.class, chatId)).thenReturn(null);

        Client client = clientDAO.getClient(chatId);

        assertNull(client);
        verify(session, times(1)).get(eq(Client.class), eq(chatId));
    }
}