package org.nhindirect.monitor.streams;

import static org.mockito.Mockito.spy;

import org.apache.camel.ProducerTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@Import(TxEventSink.class)
public class TxTestConfiguration
{
	@Bean
	public ObjectMapper objectMapper()
	{
		return new ObjectMapper();
	}
	
	@Bean 
	public ProducerTemplate producer()
	{
		return spy(ProducerTemplate.class);
	}
	
}
