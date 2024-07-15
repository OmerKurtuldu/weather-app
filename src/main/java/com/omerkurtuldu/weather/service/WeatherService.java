package com.omerkurtuldu.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omerkurtuldu.weather.constants.Constants;
import com.omerkurtuldu.weather.dto.WeatherDto;
import com.omerkurtuldu.weather.dto.WeatherResponse;
import com.omerkurtuldu.weather.model.WeatherEntity;
import com.omerkurtuldu.weather.repository.WeatherRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"weathers"})
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final WeatherRepository weatherRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherService(
            WeatherRepository weatherRepository,
            RestTemplate restTemplate
    ) {
        this.weatherRepository = weatherRepository;
        this.restTemplate = restTemplate;
    }


    @Cacheable(key = "#city")
    public WeatherDto getWeatherByCityName(String city) {
        Optional<WeatherEntity> weatherEntityOptional = weatherRepository.findFirstByRequestedCityNameOrderByUpdatedTimeDesc(city);
        logger.info("Requested city: " + city);
        return weatherEntityOptional.map(weather -> {
                    if (weather.getUpdatedTime().isBefore(LocalDateTime.now().minusMinutes(30))) {
                        return WeatherDto.convert(getWeatherFromWeatherStack(city));
                    }
                    return WeatherDto.convert(weather);
                })
                .orElseGet(() -> WeatherDto.convert(getWeatherFromWeatherStack(city)));
    }

    private WeatherEntity getWeatherFromWeatherStack(String city) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(getWeatherStackUrl(city), String.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK || responseEntity.getBody().contains("\"success\":false")) {
            throw new NotFoundException(city);
        }

        try {
            WeatherResponse weatherResponse = objectMapper.readValue(responseEntity.getBody(), WeatherResponse.class);
            return saveWeatherEntity(city, weatherResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @CacheEvict(allEntries = true)
    @PostConstruct
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void clearCache(){
        logger.info("cache cleared");
    }

    public WeatherEntity saveWeatherEntity(String city, WeatherResponse weatherResponse) {

        if (weatherResponse.location() == null || weatherResponse.current() == null) {
            throw new NotFoundException(city);
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        WeatherEntity weatherEntity = new WeatherEntity(city,
                weatherResponse.location().name(),
                weatherResponse.location().country(),
                weatherResponse.current().temperature(),
                LocalDateTime.now(),
                LocalDateTime.parse(weatherResponse.location().localtime(), dateTimeFormatter)
        );
        return weatherRepository.save(weatherEntity);
    }

    private String getWeatherStackUrl(String city){
        return Constants.API_URL + Constants.ACCESS_KEY_PARAM+ Constants.API_KEY + Constants.QUERY_KEY_PARAM + city;
    }
}
