/**
 * Copyright (c) Raja Dilip Chowdary Kolli. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.poc.restfulpoc.service.impl;

import com.poc.restfulpoc.repository.CustomerRepository;
import com.poc.restfulpoc.service.JMSReceiver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * <p>
 * JMSReceiverImpl class.
 * </p>
 *
 * @author Raja Kolli
 * @version 0: 5
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JMSReceiverImpl implements JMSReceiver {

	private final CustomerRepository customerRepository;

	/** {@inheritDoc} */
	@JmsListener(destination = "jms.message.endpoint")
	@Override
	public void receiveMessage(String customerId) {
		log.info("Received CustomerID:{} for deletion", customerId);
		this.customerRepository.deleteById(Long.valueOf(customerId));
	}

}
