package gov.nysenate.seta.service.base;

import com.google.common.eventbus.EventBus;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCache;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * A base implementation of CachingService that uses a single EhCacheCache
 */
public abstract class BaseCachingService<ContentId> implements CachingService<ContentId>
{
    @Autowired CacheManager cacheManager;
    @Autowired EventBus eventBus;

    protected EhCacheCache primaryCache;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        setupCaches();
    }

    /** {@inheritDoc} */
    @Override
    public void setupCaches() {
        Cache cache = new Cache(new CacheConfiguration()
                .name(getCacheType().name())
                .eternal(true)
                //todo set max size
                .sizeOfPolicy(defaultSizeOfPolicy()));
        cacheManager.addCache(cache);
        this.primaryCache = new EhCacheCache(cache);
    }

    /** {@inheritDoc} */
    @Override
    public List<Ehcache> getCaches() {
        return Arrays.asList(primaryCache.getNativeCache());
    }

    /** {@inheritDoc} */
    @Override
    public void evictContent(ContentId contentId) {
        primaryCache.evict(contentId);
    }
}
