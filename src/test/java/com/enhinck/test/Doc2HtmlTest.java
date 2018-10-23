package com.enhinck.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;

import com.enhinck.dochtml.DocToHtmlUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Doc2HtmlTest {
	
	@Test
	public void testDoc2Html() {
		FileInputStream input = null;
		try {
			input = new FileInputStream(new File("d:\\test.doc"));
			String html = DocToHtmlUtil.docToHtml(input, "c:\\doc图片输出路径\\", "标题", "2017-01-01");
			log.info(html);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testDocx2Html() {
		FileInputStream input = null;
		try {
			input = new FileInputStream(new File("d:\\test.docx"));
			String html = DocToHtmlUtil.docxToHtml(input, "c:\\doc图片输出路径\\", "标题", "2017-01-01");
			log.info(html);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
