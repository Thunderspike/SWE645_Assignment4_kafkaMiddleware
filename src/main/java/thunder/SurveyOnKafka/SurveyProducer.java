package thunder.SurveyOnKafka;

import java.time.Instant;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.google.gson.Gson;


public class SurveyProducer {
	
	private static Map<String, String> env = System.getenv();
	
	static Gson gson = new Gson();
	
	private Producer<String, String> producer;
	private static String topicName;
	
	public SurveyProducer() {
		topicName = "survey-data-topic";

		Properties props = new Properties();
		props.put("bootstrap.servers", env.get("KAFKA_SERVER")+":9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		producer = new KafkaProducer<String, String>(props);

		Scanner scanner = new Scanner(System.in);
	}
	
	public Survey send(Survey survey) {
		try {
		producer.send(
				new ProducerRecord<String, String>(
						topicName, 
						null, 
						Instant.now().getEpochSecond(), 
						String.valueOf(survey.getId()), 
						gson.toJson(survey)),
				(metadata, exception) -> System.out.println(metadata));
		return survey;
		} catch(Exception e) {
			return null;
		}
	}
}