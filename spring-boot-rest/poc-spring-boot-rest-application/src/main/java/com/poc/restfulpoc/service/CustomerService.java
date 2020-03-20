/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.poc.restfulpoc.service;

import java.util.List;

import com.poc.restfulpoc.entities.Customer;
import com.poc.restfulpoc.exception.EntityNotFoundException;

/**
 * <p>
 * CustomerService interface.
 * </p>
 *
 * @author Raja Kolli
 * @version 0: 5
 */
public interface CustomerService {

	Customer getCustomer(Long customerId) throws EntityNotFoundException;

	List<Customer> getCustomers();

	Customer createCustomer(Customer customer);

	Customer updateCustomer(Customer updateCustomerRequest, Long customerId) throws EntityNotFoundException;

	void deleteCustomerById(Long customerId) throws EntityNotFoundException;

	boolean isCustomerExist(String firstName);

	void deleteAllCustomers();

}
