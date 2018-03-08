package com.mizuho.esb.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mizuho.esb.entity.VendorPrice;

public class VendorPriceCacheTest {

	private VendorPriceCache cache = new VendorPriceCache();

	@Before
	public void setUp() throws Exception {
		List<VendorPrice> prices = new ArrayList<VendorPrice>();
		prices.add(new VendorPrice("V1", "IA", 1.1));
		prices.add(new VendorPrice("V1", "IB", 1.2));
		prices.add(new VendorPrice("V1", "IC", 1.3));
		prices.add(new VendorPrice("V2", "IB", 2.2));
		prices.forEach(price -> cache.addVendorPrice(price));
	}

	@After
	public void tearDown() throws Exception {
		cache.shutdown();
	}

	@Test
	public void testGetVendorPriceForInstruments() {
		List<VendorPrice> prices = cache.getVendorPriceForInstruments("V1", Arrays.asList("IA", "IB")); 
		assertNotNull(prices);
		assertEquals(prices.size(),2);
		
		prices = cache.getVendorPriceForInstruments("V1", Arrays.asList("IA"));
		assertNotNull(prices);
		assertEquals(prices.size(),1);
		assertEquals("V1", prices.get(0).vendor);
		assertEquals("IA", prices.get(0).instrument);
		assert(1.1==prices.get(0).price.doubleValue());
	}

	@Test
	public void testGetInstrumentPriceForVendors() {
		
		List<VendorPrice> prices = cache.getInstrumentPriceForVendors("IA", Arrays.asList("V1","V2"));
		assertNotNull(prices);
		assertEquals(prices.size(),1);
		assertEquals("V1", prices.get(0).vendor);
		assertEquals("IA", prices.get(0).instrument);
		assert(1.1==prices.get(0).price.doubleValue());
		
		prices = cache.getInstrumentPriceForVendors("IB", Arrays.asList("V1","V2"));
		assertNotNull(prices);
		assertEquals(prices.size(),2);
		
	}

	@Test
	public void testGetVendorPriceForAllInstruments() {
		
		List<VendorPrice> prices = cache.getVendorPriceForAllInstruments("V1");
		assertNotNull(prices);
		assertEquals(prices.size(),3);
		prices.forEach(price -> {
			assertEquals("V1", price.vendor);
			switch(price.instrument) {
			case "IA" : assert(1.1==price.price); break;
			case "IB" : assert(1.2==price.price); break;
			case "IC" : assert(1.3==price.price); break;
			default : fail("instrument not matched");
			}
		});
		
	}

	@Test
	public void testGetInstrumentPriceForAllVendors() {
		List<VendorPrice> prices = cache.getInstrumentPriceForAllVendors("IB");
		assertNotNull(prices);
		assertEquals(prices.size(),2);
		prices.forEach(price -> {
			assertEquals("IB", price.instrument);
			switch(price.vendor) {
			case "V1" : assertTrue(1.2==price.price); break;
			case "V2" : assertTrue(2.2==price.price); break;
			default : fail("vendor not matched");
			}
		});
	}

}
