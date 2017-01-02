/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package com.adyen.core.batch.task;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.HeaderTask;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.impl.DefaultImpexConverter;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.AbstractImpexRunnerTask;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.ImpexTransformerTask;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.enums.ProductTaxGroup;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.testframework.TestUtils;
import de.hybris.platform.util.CSVConstants;
import com.adyen.core.model.ApparelSizeVariantProductModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for {@link ImpexTransformerTask} and {@link AbstractImpexRunnerTask}
 */
@IntegrationTest
public class BatchIntegrationTest extends ServicelayerTest
{
	private static final String APPAREL = "apparelProductCatalog";
	private static final String SEPARATOR = ",";
	private static final String LANG_EN = "en";

	@Resource
	private HeaderTask batchRunnerTask;
	@Resource
	private HeaderTask batchTransformerTask;
	@Resource
	private HeaderTask batchCleanupTask;
	@Resource
	private ProductService productService;
	@Resource
	private StockService stockService;
	@Resource
	private WarehouseService warehouseService;
	@Resource
	private ImpexConverter batchMediaConverter;
	private String mediaHeader;

	private Long productId;
	private Long sequenceId;
	private Random random;

	@Before
	public void setUp() throws Exception
	{
		random = new Random();
		mediaHeader = batchMediaConverter.getHeader();
		productId = Long.valueOf(Math.abs(random.nextLong()));
		sequenceId = Long.valueOf(Math.abs(random.nextLong()));
		importCsv("/adyencore/test/testBatch.impex", "utf-8");
		// don't import binary data -> temporarily remove MediaTranslator
		((DefaultImpexConverter) batchMediaConverter).setHeader(StringUtils.substringBeforeLast(mediaHeader, ";"));
	}

	@After
	public void tearDown()
	{
		((DefaultImpexConverter) batchMediaConverter).setHeader(mediaHeader);
	}

	@Test
	public void testBasicProduct() throws Exception
	{
		final ProductModel product = processFile(productId, "base_product-", new ProductContent());
		Assert.assertEquals("name", product.getName(Locale.ENGLISH));
		Assert.assertNull("name", product.getName(Locale.GERMAN));
		Assert.assertEquals("description", product.getDescription(Locale.ENGLISH));
		Assert.assertNull("description", product.getDescription(Locale.GERMAN));
		Assert.assertEquals("ean", product.getEan());
		Assert.assertEquals("manufacturer", product.getManufacturerName());
		Assert.assertEquals("manufacturerAID", product.getManufacturerAID());
		Assert.assertEquals("pieces", product.getUnit().getName());
		Assert.assertEquals("approved", product.getApprovalStatus().getCode());
		Assert.assertEquals("eu-vat-half", product.getEurope1PriceFactory_PTG().getCode());
		Assert.assertEquals(sequenceId, product.getSequenceId());
		Assert.assertEquals("Staged", product.getCatalogVersion().getVersion());
		Assert.assertEquals("apparelProductCatalog", product.getCatalogVersion().getCatalog().getId());
	}

	@Test
	public void testPrice() throws Exception
	{
		processFile(productId, "base_product-", new ProductContent());
		TestUtils.disableFileAnalyzer("");
		try
		{
			final ProductModel product = processFile(productId, "price-", new FileContent()
			{
				@Override
				public void writeContent(final PrintWriter writer) throws IOException
				{
					writer.print("1231");
					writer.print(SEPARATOR);
					writer.println("EUR");
				}
			});
			Assert.assertEquals(1, product.getEurope1Prices().size());
			final PriceRowModel prize = product.getEurope1Prices().iterator().next();
			Assert.assertEquals("EUR", prize.getCurrency().getIsocode());
			Assert.assertEquals(Double.valueOf(1231), prize.getPrice());
			Assert.assertFalse(prize.getNet().booleanValue());
			Assert.assertEquals("pieces", prize.getUnit().getCode());
			Assert.assertEquals(Integer.valueOf(1), prize.getUnitFactor());
			Assert.assertEquals(Long.valueOf(1), prize.getMinqtd());
			Assert.assertEquals(sequenceId, prize.getSequenceId());
			Assert.assertEquals("Staged", prize.getCatalogVersion().getVersion());
			Assert.assertEquals("apparelProductCatalog", prize.getCatalogVersion().getCatalog().getId());
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test
	public void testStock() throws Exception
	{
		final ProductModel product = processFile(productId, "base_product-", new ProductContent());
		final WarehouseModel warehouse = warehouseService.getWarehouseForCode("default");

		processFile(productId, "stock-", new FileContent()
		{
			@Override
			public void writeContent(final PrintWriter writer) throws IOException
			{
				writer.println("111");
			}
		});
		final StockLevelModel curStock = stockService.getStockLevel(product, warehouse);
		Assert.assertEquals(111, curStock.getAvailable());
	}

	@Test
	public void testTax() throws Exception
	{
		processFile(productId, "base_product-", new ProductContent());
		final ProductModel product = processFile(productId, "tax-", new FileContent()
		{
			@Override
			public void writeContent(final PrintWriter writer) throws IOException
			{
				writer.println("eu-vat-full");
			}
		});
		final ProductTaxGroup taxGroup = product.getEurope1PriceFactory_PTG();
		Assert.assertEquals("eu-vat-full", taxGroup.getCode());
	}

	@Test
	public void testMerchandise() throws Exception
	{
		processFile(productId, "base_product-", new ProductContent());
		final ProductModel product = processFile(productId, "merchandise-", new FileContent()
		{
			@Override
			public void writeContent(final PrintWriter writer) throws IOException
			{
				writer.print("CROSSELLING");
				writer.print(SEPARATOR);
				writer.println(productId);
			}
		});
		Assert.assertEquals(1, product.getProductReferences().size());
		final ProductReferenceModel ref = product.getProductReferences().iterator().next();
		Assert.assertEquals("CROSSELLING", ref.getReferenceType().getCode());
		Assert.assertEquals(productId.toString(), ref.getTarget().getCode());
		Assert.assertEquals(Boolean.TRUE, ref.getActive());
		Assert.assertEquals(Boolean.FALSE, ref.getPreselected());
		Assert.assertEquals(product, ref.getSource());
		Assert.assertEquals(product, ref.getTarget());
	}

	@Test
	public void testMedia() throws Exception
	{
		processFile(productId, "base_product-", new ProductContent());
		final ProductModel product = processFile(productId, "media-", new FileContent()
		{
			@Override
			public void writeContent(final PrintWriter writer) throws IOException
			{
				writer.println("test.jpg");
			}
		});
		verifyMedia(product.getPicture(), "300Wx300H");
		verifyMedia(product.getThumbnail(), "96Wx96H");
		verifyMedia(product.getThumbnails().iterator().next(), "96Wx96H");
		verifyMedia(product.getDetail().iterator().next(), "1200Wx1200H");
		for (final MediaModel media : product.getOthers())
		{
			if ("515Wx515H".equals(media.getMediaFormat().getQualifier()))
			{
				verifyMedia(media, "515Wx515H");
			}
			else if ("96Wx96H".equals(media.getMediaFormat().getQualifier()))
			{
				verifyMedia(media, "96Wx96H");
			}
			else
			{
				verifyMedia(media, "30Wx30H");
			}
		}
		verifyMedia(product.getNormal().iterator().next(), "300Wx300H");
		final MediaContainerModel container = product.getGalleryImages().iterator().next();
		final Set<String> formats = new HashSet<String>();
		formats.add("30Wx30H");
		formats.add("65Wx65H");
		formats.add("96Wx96H");
		formats.add("300Wx300H");
		formats.add("515Wx515H");
		formats.add("1200Wx1200H");
		final Set<String> containerFormats = new HashSet<String>();
		for (final MediaModel media : container.getMedias())
		{
			containerFormats.add(media.getMediaFormat().getQualifier());
		}
		Assert.assertEquals(formats, containerFormats);
		Assert.assertEquals("Staged", container.getCatalogVersion().getVersion());
		Assert.assertEquals("apparelProductCatalog", container.getCatalogVersion().getCatalog().getId());
	}

	@Test
	public void testVariant() throws Exception
	{
		processFile(productId, "base_product-", new ProductContent()
		{
			@Override
			public void writeContent(final PrintWriter writer) throws IOException
			{
				writer.print("ApparelSizeVariantProduct");
				super.writeContent(writer);
			}
		});
		final Long variantId = Long.valueOf(Math.abs(random.nextLong()));
		final ProductModel product = processFile(productId, "variant-", new FileContent()
		{
			@Override
			public void writeContent(final PrintWriter writer) throws IOException
			{
				writer.print(variantId);
				writer.print(SEPARATOR);
				writer.print(SEPARATOR);
				writer.print("black");
				writer.print(SEPARATOR);
				writer.print("L");
			}
		});
		final ApparelSizeVariantProductModel variant = (ApparelSizeVariantProductModel) productService.getProductForCode(variantId
				.toString());
		Assert.assertEquals("ApparelSizeVariantProduct", product.getVariantType().getCode());
		Assert.assertEquals(product, variant.getBaseProduct());
		Assert.assertEquals("black", variant.getStyle(Locale.ENGLISH));
		Assert.assertNull(variant.getStyle(Locale.GERMAN));
		Assert.assertEquals("L", variant.getSize(Locale.ENGLISH));
		Assert.assertNull(variant.getSize(Locale.GERMAN));
	}

	protected void verifyMedia(final MediaModel media, final String format)
	{
		Assert.assertEquals("/" + format + "/test.jpg", media.getCode());
		Assert.assertEquals(format, media.getMediaFormat().getQualifier());
		Assert.assertEquals("image/jpeg", media.getMime());
		Assert.assertEquals("Staged", media.getCatalogVersion().getVersion());
		Assert.assertEquals("apparelProductCatalog", media.getCatalogVersion().getCatalog().getId());
		Assert.assertEquals("images", media.getFolder().getQualifier());
		Assert.assertEquals("test.jpg", media.getRealFileName());
	}

	protected ProductModel processFile(final Long productId, final String filePrefix, final FileContent content) throws Exception //NOPMD
	{
		File file = null;
		BatchHeader header = null;
		try
		{
			file = File.createTempFile(filePrefix, ".csv");
			header = createHeader(file);
			createFile(file, productId, content);
			batchTransformerTask.execute(header);
			batchRunnerTask.execute(header);
			return productService.getProductForCode(productId.toString());
		}
		finally
		{
			if (file != null)
			{
				file.delete();
			}
			if (header != null)
			{
				header.setFile(null);
				batchCleanupTask.execute(header);
			}
		}
	}

	protected BatchHeader createHeader(final File file)
	{
		final BatchHeader header = new BatchHeader();
		header.setFile(file);
		header.setSequenceId(sequenceId);
		header.setLanguage(LANG_EN);
		header.setCatalog(APPAREL);
		return header;
	}

	protected void createFile(final File file, final Long productId, final FileContent fileContent) throws IOException
	{
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
					CSVConstants.DEFAULT_ENCODING)));
			writer.print(productId);
			writer.print(SEPARATOR);
			fileContent.writeContent(writer);
		}
		finally
		{
			IOUtils.closeQuietly(writer);
		}
	}

	protected interface FileContent
	{
		void writeContent(PrintWriter writer) throws IOException;
	}

	protected static class ProductContent implements FileContent
	{
		@Override
		public void writeContent(final PrintWriter writer) throws IOException
		{
			writer.print(SEPARATOR);
			writer.print("name");
			writer.print(SEPARATOR);
			writer.print("description");
			writer.print(SEPARATOR);
			writer.print("ean");
			writer.print(SEPARATOR);
			writer.print("manufacturer");
			writer.print(SEPARATOR);
			writer.print("manufacturerAID");
			writer.print(SEPARATOR);
			writer.print("pieces");
			writer.print(SEPARATOR);
			writer.print("approved");
			writer.print(SEPARATOR);
			writer.println("eu-vat-half");
		}
	}
}
