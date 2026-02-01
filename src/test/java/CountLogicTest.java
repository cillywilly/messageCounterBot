import bot.CountLogic;
import bot.dataBase.dao.ClientDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.*;

class CountLogicTest {

    @Mock
    private ClientDAO clientDAO;

    @InjectMocks
    private CountLogic countLogic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateCount_shouldCallAddClick_whenUpdateHasMessage() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);

        countLogic.updateCount(update);

        verify(clientDAO, times(1)).addClick(update);
    }

    @Test
    void updateCount_shouldNotCallAddClick_whenUpdateHasNoMessage() {
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(false);
        countLogic.updateCount(update);
        verify(clientDAO, never()).addClick(any());
    }
}