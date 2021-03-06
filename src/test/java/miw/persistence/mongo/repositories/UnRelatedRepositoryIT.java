package miw.persistence.mongo.repositories;

import miw.TestConfig;
import miw.persistence.mongo.documents.UnRelatedDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class UnRelatedRepositoryIT {

    @Autowired
    private UnRelatedRepository unRelatedRepository;

    @BeforeEach
    void populate() {
        this.unRelatedRepository.deleteAll();
        for (int i = 0; i < 5; i++) {
            this.unRelatedRepository.save(new UnRelatedDocument("nick" + i));
        }
    }

    @Test
    void test() {
        assertTrue(unRelatedRepository.count() > 0);
    }

    @Test
    void testCreateWithNull() {
        this.unRelatedRepository.deleteAll();
        this.unRelatedRepository.save(new UnRelatedDocument("nick"));
        UnRelatedDocument unRelated = new UnRelatedDocument();
        unRelated.setNick("nick-null");
        this.unRelatedRepository.save(unRelated);
    }

    @Test
    public void uniqueNickTest() {
        UnRelatedDocument unRelatedDocument1 = new UnRelatedDocument("unique");
        UnRelatedDocument unRelatedDocument2 = new UnRelatedDocument("unique");
        this.unRelatedRepository.save(unRelatedDocument1);
        assertThrows(DuplicateKeyException.class, () -> this.unRelatedRepository.save(unRelatedDocument2));
        this.unRelatedRepository.delete(unRelatedDocument1);
    }

    @Test
    public void testFindByNickIgnoreCase() {
        assertNotNull(unRelatedRepository.findByNickIgnoreCase("NICK1"));
    }

    @Test
    public void testFindFirst3ByNickStartingWith() {
        assertEquals(0, unRelatedRepository.findFirst3ByNickStartingWith("no").size());
        assertEquals(3, unRelatedRepository.findFirst3ByNickStartingWith("ni").size());
    }

    @Test
    public void testFindByNickOrLargeOrderByIdDesc() {
        assertTrue(unRelatedRepository.findByNickOrLargeOrderByLogerDesc(
                "NoNick", "Large...").get(0).getLoger() >= unRelatedRepository
                .findByNickOrLargeOrderByLogerDesc("NoNick", "Large...").get(1).getLoger());
        assertEquals(5, unRelatedRepository.findByNickOrLargeOrderByLogerDesc("NoNick", "Large...").size());
    }

    @Test
    public void testFindByIntegerGreaterThan() {
        assertEquals(2, unRelatedRepository.findByIntegerGreaterThan(0, PageRequest.of(0, 2)).size());
        assertEquals(2, unRelatedRepository.findByIntegerGreaterThan(0, PageRequest.of(1, 3)).size());
    }

    @Test
    public void testDeleteByNick() {
        assertNotNull(unRelatedRepository.findByNick("nick0"));
        unRelatedRepository.deleteByNick("nick0");
        assertNull(unRelatedRepository.findByNick("nick0"));
    }

    @Test
    public void testFindByNick() {
        assertNotNull(unRelatedRepository.findByNick("nick0"));
    }

    @Test
    public void testFindIdAndBornDateAndLargeByNick() {
        assertNotNull(unRelatedRepository.findIdAndBornDateAndLargeByNick("nick0").getId());
        assertNotNull(unRelatedRepository.findIdAndBornDateAndLargeByNick("nick0").getBornDate());
        assertNotNull(unRelatedRepository.findIdAndBornDateAndLargeByNick("nick0").getLarge());
    }

    @Test
    public void testFindBornDateByNick() {
        assertNotNull(unRelatedRepository.findBornDateByNick("nick0").getBornDate());
    }

    @Test
    public void testFindByNickAndLarge() {
        assertNotNull(unRelatedRepository.findByNickAndLarge("nick0", "Large..."));
        assertNull(unRelatedRepository.findByNickAndLarge("nick0", "NOT"));
    }

    @Test
    public void testFindByNickIn() {
        assertEquals(2, unRelatedRepository.findByNickIn(Arrays.asList("nick1", "nick2")).size());
    }

    @Test
    public void testFindByNickLikeIgnoreCaseNullSafe() {
        assertEquals(1, unRelatedRepository.findByNickLikeIgnoreCaseNullSafe("k1").size());
        assertEquals(5, unRelatedRepository.findByNickLikeIgnoreCaseNullSafe(null).size());
    }

    @Test
    public void testFindByNickLikeLargeLikeNullSafe() {
        assertEquals(0, unRelatedRepository.findByNickLikeAndLargeLikeNullSafe("k1", "no").size());
        assertEquals(1, unRelatedRepository.findByNickLikeAndLargeLikeNullSafe("k1", null).size());
        assertEquals(5, unRelatedRepository.findByNickLikeAndLargeLikeNullSafe(null, null).size());
    }

}
