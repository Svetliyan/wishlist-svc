package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final WishlistRepository wishlistRepository;

    @Autowired
    public WishlistController(WishlistService wishlistService, WishlistRepository wishlistRepository) {
        this.wishlistService = wishlistService;
        this.wishlistRepository = wishlistRepository;
    }

    @PostMapping
    public WishlistItem addToWishlist(@RequestBody WishlistItem wishlistItem) {
        Optional<WishlistItem> existingItem = wishlistRepository.findByUserIdAndGameId(wishlistItem.getUserId(), wishlistItem.getGameId());

        if (existingItem.isPresent()) {
            throw new RuntimeException("Game already exist");
        }


        return wishlistService.addWishlistItem(wishlistItem);
    }

    @GetMapping("/{userId}")
    public List<WishlistItem> getWishlist(@PathVariable UUID userId) {
        return wishlistService.getWishlistByUserId(userId);
    }
}
