package org.crowdlib.inmemory.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.crowdlib.entities.CatalogueItem;

public class InMemoryCatalogueItemCollection {
	private static HashMap<Integer, CatalogueItem> catalogueItems = new HashMap<Integer, CatalogueItem>();

	public static void initializeInMemoryCatalogueItems(){
		catalogueItems.put(1, new CatalogueItem(1, "Book1", "Author1"));
		catalogueItems.put(2, new CatalogueItem(2, "Book2", "Author2"));
		catalogueItems.put(3, new CatalogueItem(3, "Book3", "Author3"));
	}
	
	public static void addCatalogueItem(CatalogueItem catalogueItem) {
		catalogueItems.put(catalogueItem.getId(), catalogueItem);
	}
	
	public static CatalogueItem getCatalogueItem(Integer id) {
		return catalogueItems.get(id);
	}
	
	public static List<CatalogueItem> getAllCatalogueItems(){
		return new ArrayList<>(catalogueItems.values());
	}

}
