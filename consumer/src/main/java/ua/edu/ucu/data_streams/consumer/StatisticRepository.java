package ua.edu.ucu.data_streams.consumer;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatisticRepository extends MongoRepository<Statistic, String>{

}
