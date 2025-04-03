package app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class WishlistUTest {
    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private WishlistRepository wishlistRepository;

    private Mock mockmvc;

    @Test
    void addWishlistItem_happyPath() {

        // Given
        UUID userId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setUserId(userId);
        wishlistItem.setGameId(gameId);
        wishlistItem.setMainImgUrl("http://example.com/image.png");

        // When
        wishlistService.addWishlistItem(wishlistItem);

        // Then
        List<WishlistItem> wishlistItems = wishlistRepository.findAll();
        assertThat(wishlistItems).hasSize(1);
        WishlistItem savedItem = wishlistItems.get(0);
        assertEquals(userId, savedItem.getUserId());
        assertEquals(gameId, savedItem.getGameId());
        assertEquals("http://example.com/image.png", savedItem.getMainImgUrl());
    }

    @BeforeEach
    void setUp() {
        wishlistRepository.deleteAll();  // Изчистваме базата преди всеки тест
    }

    @Test
    void getWishlistByUserId_ShouldReturnWishlistItems_WhenValidUserId() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID gameId1 = UUID.randomUUID();
        UUID gameId2 = UUID.randomUUID();

        // Добавяме 2 елемента в wishlist за даден потребител
        WishlistItem item1 = new WishlistItem();
        item1.setUserId(userId);
        item1.setGameId(gameId1);
        item1.setMainImgUrl("http://example.com/image1.png");

        WishlistItem item2 = new WishlistItem();
        item2.setUserId(userId);
        item2.setGameId(gameId2);
        item2.setMainImgUrl("http://example.com/image2.png");

        wishlistService.addWishlistItem(item1);
        wishlistService.addWishlistItem(item2);

        // When
        List<WishlistItem> wishlistItems = wishlistService.getWishlistByUserId(userId);

        // Then
        assertThat(wishlistItems).hasSize(2);  // Очакваме точно 2 елемента в списъка
        assertEquals(userId, wishlistItems.get(0).getUserId());
        assertEquals(gameId1, wishlistItems.get(0).getGameId());
        assertEquals("http://example.com/image1.png", wishlistItems.get(0).getMainImgUrl());

        assertEquals(userId, wishlistItems.get(1).getUserId());
        assertEquals(gameId2, wishlistItems.get(1).getGameId());
        assertEquals("http://example.com/image2.png", wishlistItems.get(1).getMainImgUrl());
    }

    @Test
    void getWishlistByUserId_ShouldReturnEmptyList_WhenNoWishlistItemsForUser() {
        // Given
        UUID userId = UUID.randomUUID();  // Потребител, който няма елементи в wishlist

        // When
        List<WishlistItem> wishlistItems = wishlistService.getWishlistByUserId(userId);

        // Then
        assertThat(wishlistItems).isEmpty();  // Очакваме празен списък, защото няма елементи
    }
}
