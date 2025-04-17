package mk.ukim.finki.wp.september2021.service.impl;

import mk.ukim.finki.wp.september2021.model.News;
import mk.ukim.finki.wp.september2021.model.NewsType;
import mk.ukim.finki.wp.september2021.model.exceptions.InvalidNewsCategoryIdException;
import mk.ukim.finki.wp.september2021.model.exceptions.InvalidNewsIdException;
import mk.ukim.finki.wp.september2021.repository.NewsCategoryRepository;
import mk.ukim.finki.wp.september2021.repository.NewsRepository;
import mk.ukim.finki.wp.september2021.service.NewsCategoryService;
import mk.ukim.finki.wp.september2021.service.NewsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final NewsCategoryService newsCategoryService;

    public NewsServiceImpl(NewsRepository newsRepository, NewsCategoryService newsCategoryService) {
        this.newsRepository = newsRepository;
        this.newsCategoryService = newsCategoryService;
    }


    @Override
    public List<News> listAllNews() {
        return newsRepository.findAll();
    }

    @Override
    public News findById(Long id) {
        return newsRepository.findById(id).orElseThrow(InvalidNewsIdException::new);
    }

    /**
     * This method is used to create a new entity, and save it in the database.
     *
     * @param name
     * @param description
     * @param price
     * @param type
     * @param category
     * @return The entity that is created. The id should be generated when the entity is created.
     * @throws InvalidNewsCategoryIdException when there is no category with the given id
     */
    @Override
    public News create(String name, String description, Double price, NewsType type, Long category) {
        return newsRepository.save(new News(
                name,
                description,
                price,
                type,
                newsCategoryService.findById(category)
        ));
    }

    /**
     * This method is used to modify an entity, and save it in the database.
     *
     * @param id The id of the entity that is being edited
     * @param name
     * @param description
     * @param price
     * @param type
     * @param category
     * @return The entity that is updated.
     * @throws InvalidNewsIdException  when there is no entity with the given id
     * @throws InvalidNewsCategoryIdException when there is no category with the given id
     */
    @Override
    public News update(Long id, String name, String description, Double price, NewsType type, Long category) {
        News news = findById(id);
        news.setName(name);
        news.setDescription(description);
        news.setPrice(price);
        news.setType(type);
        news.setCategory(newsCategoryService.findById(category));
        newsRepository.save(news);
        return news;
    }

    @Override
    public News delete(Long id) {
        News news = findById(id);
        newsRepository.delete(news);
        return news;
    }

    /**
     * Method for liking a news. If the id is invalid, it should throw InvalidNewsIdException.
     *
     * @param id
     * @return The event that is deleted.
     * @throws InvalidNewsIdException when there is no event with the given id
     */
    @Override
    public News like(Long id) {
        News news = findById(id);
        news.setLikes(news.getLikes() + 1);
        newsRepository.save(news);
        return news;
    }

    @Override
    public List<News> listNewsWithPriceLessThanAndType(Double price, NewsType type) {
        if (price == null && type == null) {
            return listAllNews();
        }
        else if (price != null && type != null){
            return newsRepository.findByPriceLessThanAndType(price, type);
        }
        else if (price == null){
            return newsRepository.findByType(type);
        }
        else return newsRepository.findByPriceLessThan(price);
    }
}
