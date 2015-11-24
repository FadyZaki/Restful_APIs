package org.crowdlib.webservices.api;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.exceptions.CustomizedWebApplicationException;
import org.crowdlib.inmemory.collections.InMemoryCatalogueItemCollection;
import org.crowdlib.inmemory.collections.InMemoryCommentCollection;
import org.crowdlib.inmemory.collections.InMemoryUserCollection;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CatalogueItemResourceTests {

	@Mock
	CatalogueItem mockedItem;

	CatalogueItemResource catalogueItemResource = new CatalogueItemResource();

	@Before
	public void setup() {
		InMemoryUserCollection.initializeInMemoryUsers();
    	InMemoryCatalogueItemCollection.initializeInMemoryCatalogueItems();
    	InMemoryCommentCollection.initializeInMemoryComments();
	}

	@Test
	public void whenGetCatalogueItemsIsCalledShouldReturnTheListOfItemsInMemory() {
		//when
		List<CatalogueItem> returnedItems = this.catalogueItemResource.getCatalogueItems();
		List<CatalogueItem> itemsInMemory = InMemoryCatalogueItemCollection.getAllCatalogueItems();
		//then
		assertEquals(returnedItems, itemsInMemory);
	}

	@Test
	public void whenGetCatalogueItemIsCalledOnItemAvailableInTheMemoryShouldReturnThatItem() {
		//given
		when(mockedItem.getId()).thenReturn(111);
		//when
		InMemoryCatalogueItemCollection.addCatalogueItem(mockedItem);
		CatalogueItem item = this.catalogueItemResource.getCatalogueItem(mockedItem.getId());
		//then
		assertEquals(mockedItem, item);
	}
	
	@Test(expected=CustomizedWebApplicationException.class)
	public void whenGetCatalogueItemIsCalledOnItemNotAvailableInTheMemoryShouldThrowCustomizedWebApplicationExceptionWithResponseNotFound() {
		//given
		when(mockedItem.getId()).thenReturn(111);
		//when
		this.catalogueItemResource.getCatalogueItem(mockedItem.getId());
		//then
		//CustomizedWebApplication exception is thrown because item isn't available in memory
	}

}
