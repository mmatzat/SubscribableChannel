package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

	@Autowired
	SubscribableChannel subChannel;

	/**
	 * Jeder REST-Request wird in einem eigenen Thread ausgeführt
	 * 
	 * @param text
	 */
	@PutMapping("/newQueue/{TEXT}")
	public void newQueue(@PathVariable("TEXT") String text) {

		// Neuen MessageHandler erzeugen und mit Bean verdrahten
		subChannel.subscribe(newHandler());
		subChannel.send(new GenericMessage<String>(text));
	}

	/**
	 * Wird einmal aufgerufen um per "Autowire" ein Bean vom Type
	 * SubscribableChannel zu injecten. Alles Threads bekommen den gleichen
	 * SubscribableChannel injected. Scope = Singleton.
	 * 
	 */
	@Bean
	protected SubscribableChannel subChannel() {

		ExecutorSubscribableChannel fixedSubscriberChannel = new ExecutorSubscribableChannel();

		System.out.println("New SubscribableChannel: " + fixedSubscriberChannel.hashCode());
		return fixedSubscriberChannel;
	}

	/**
	 * Mit jedem Aufruf wird lediglich eine neue Instanz eines MessageHandler
	 * erzeugt. Die @Bean-Annotation ist nicht notwendig da wir den
	 * MessageHandler selbst verdrahten und die Konfiguration nicht durch Spring
	 * erfolgt.
	 * 
	 */
	@Bean
	protected MessageHandler newHandler() {

		MessageHandler handler = new MessageHandler() {

			long startedAt = System.currentTimeMillis();
			public void handleMessage(Message<?> arg0) throws MessagingException {
				System.out.println("@" + startedAt + ": " + arg0.getPayload());
			}
		};
		return handler;
	}
}
