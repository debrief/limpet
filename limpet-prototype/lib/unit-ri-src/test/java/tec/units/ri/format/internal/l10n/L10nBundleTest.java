package tec.units.ri.format.internal.l10n;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import tec.units.ri.format.internal.l10n.L10nPropertyResources;

public class L10nBundleTest {
	
	@Test
	@Ignore
	public void testL10() {
		L10nBundle resources = L10nPropertyResources.getBundle("de");
		assertNotNull(resources);
	}
	
	@Test
	public void testMapBundle() {
		Locale locale = Locale.getDefault();
		   
		L10nBundle resources = L10nResources.getBundle("tec.units.ri.format.internal.l10n.Resources", locale);
		assertNotNull(resources);
		String text = resources.getString("title");
		assertEquals("Localization example", text);
	}
	
	@Test
	public void testMapBundle_de() {
		Locale locale = new Locale("de");
		   
		L10nBundle resources = L10nResources.getBundle("tec.units.ri.format.internal.l10n.Resources", locale);
		assertNotNull(resources);
		String text = resources.getString("text");
		assertEquals("Da ist ein Text.", text);
	}
	
	@Test
	public void testMapBundle_fr() {
		Locale locale = new Locale("fr");
		   
		L10nBundle resources = L10nResources.getBundle("tec.units.ri.format.internal.l10n.Resources", locale);
		assertNotNull(resources);
		String text = resources.getString("text");
		assertEquals("Voici du texte.", text);
	}
}
