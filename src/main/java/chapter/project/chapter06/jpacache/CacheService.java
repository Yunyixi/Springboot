package chapter.project.chapter06.jpacache;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @Name FeiLong
 * @Date 2023/9/25
 * @注释 jpa redis 缓存服务类
 */
@Service
@CacheConfig(cacheNames = "article")
public class CacheService {
    @Autowired
    private JpaCacheRepository jpaCacheRepository;

    // 根据id查询数据
    // 把数据库缓存保存到redis数据库，运行时先查询缓存，没有在运行数据库操作
    //@Cacheable(cacheNames = "article") //对数据库操作方法进行默认缓存管理，页面的查询结果都会显示同一条数据
    @Cacheable(cacheNames = "article", unless = "#result==null") //定制缓存管理
    public JpaCache findById(int article_id) {
        Optional<JpaCache> optional = jpaCacheRepository.findById(article_id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    // 根据id更新数据
    // 把数据库缓存保存到redis数据库，运行时先查询缓存，没有在运行数据库操作
    //@Cacheable(cacheNames = "article") //对数据库操作方法进行默认缓存管理
    @CachePut(cacheNames = "article", key = "#result.id") //定制缓存管理
    public JpaCache updateCache(JpaCache jpaCache) {
        jpaCacheRepository.updateCache(jpaCache.getTitle(), jpaCache.getId());
        return jpaCache;
    }

    // 根据id删除数据
    // 把数据库缓存保存到redis数据库，运行时先查询缓存，没有在运行数据库操作
    //@Cacheable(cacheNames = "article") //对数据库操作方法进行默认缓存管理
    @CacheEvict(cacheNames = "article") //定制缓存管理
    public void deleteCache(int article_id) {
        jpaCacheRepository.deleteById(article_id);

    }

}
