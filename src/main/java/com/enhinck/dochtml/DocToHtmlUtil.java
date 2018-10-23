package com.enhinck.dochtml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.XWPFDocumentVisitor;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.converter.xhtml.internal.XHTMLMapper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;

import lombok.extern.slf4j.Slf4j;

/**
 * word文档转html工具类 文件名: DocToHtmlUtil.java <br/>
 * 文件编号: <br/>
 * 创 建 人: huenbin <br/>
 * 日 期: 2018年3月23日 <br/>
 * 修 改 人: huenbin <br/>
 * 日 期: 2018年3月23日 <br/>
 * 描 述: <br/>
 * 版 本 号: V1.0.0 <br/>
 */
@Slf4j
public class DocToHtmlUtil {
	
	// word 2003 格式
	public static final String WORD_2003_EXT = "doc";
	// word 2007以上 格式
	public static final String WORD_2007_EXT = "docx";
	// word 2003 图片存储路径
	public static final String WORD_2003_PATH = "/word/2003/images/";
	
	public static String getImageFilePath() {
		String imageFilePath = UUID.randomUUID().toString().replaceAll("\\-", "");
		return imageFilePath + "/";
	}
	
	public static String docToHtml(String fileName, InputStream input, String webRootPath, String date, String title) {
		// String title = StringUtils.substringBeforeLast(fileName, ".");
		String ext = StringUtils.substringAfterLast(fileName, ".").toLowerCase();
		String html = null;
		switch (ext) {
			case WORD_2003_EXT:
				html = docToHtml(input, webRootPath, title, date);
				break;
			case WORD_2007_EXT:
				html = docxToHtml(input, webRootPath, title, date);
				break;
			default:
				html = docToHtml(input, webRootPath, title, date);
				break;
		}
		
		return html;
		
	}
	
	/**
	 * 更新标题并返回html
	 * @param html
	 * @param title
	 * @return
	 * @author huenbin
	 * @see [相关类/方法]
	 * @since V1.0.0
	 */
	public static String updateTitle(String html, String title) {
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		doc.getElementById("title").html(title);
		return doc.html();
	}
	
	/**
	 * word2003 转 HTML
	 * @param input
	 * @param webRootPath 图片保存绝对路径
	 * @param title
	 * @param date
	 * @return
	 * @throws TransformerException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @author huenbin
	 * @see [相关类/方法]
	 * @since V1.0.0
	 */
	public static String docToHtml(InputStream input, String webRootPath, String title, String date) {
		String imageFilePath = getImageFilePath();
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			webRootPath = webRootPath + WORD_2003_PATH + imageFilePath;
			HWPFDocument wordDocument = new HWPFDocument(input);
			WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
			        DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
			wordToHtmlConverter.setPicturesManager(new PicturesManager() {
				
				public String savePicture(byte[] content, PictureType pictureType, String suggestedName, float widthInches,
				        float heightInches) {
					return WORD_2003_PATH + imageFilePath + suggestedName;
				}
			});
			wordToHtmlConverter.processDocument(wordDocument);
			File webRootFilePath = new File(webRootPath);
			if (!webRootFilePath.exists()) {
				webRootFilePath.mkdirs();
			}
			// save pictures
			List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();
			if (pics != null) {
				for (int i = 0; i < pics.size(); i++) {
					Picture pic = (Picture)pics.get(i);
					try {
						pic.writeImageContent(
						        new FileOutputStream(webRootFilePath.getAbsolutePath() + "/" + pic.suggestFullFileName()));
					} catch (FileNotFoundException e) {
						log.error("docToHtml FileNotFoundException {}", e);
					}
				}
			}
			Document htmlDocument = wordToHtmlConverter.getDocument();
			byteArrayOutputStream = new ByteArrayOutputStream();
			DOMSource domSource = new DOMSource(htmlDocument);
			StreamResult streamResult = new StreamResult(byteArrayOutputStream);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(OutputKeys.METHOD, "HTML");
			serializer.transform(domSource, streamResult);
			byteArrayOutputStream.close();
			String html = byteArrayOutputStream.toString();
			org.jsoup.nodes.Document doc = Jsoup.parse(html);
			String style = doc.getElementsByTag("style").html();
			doc.getElementsByTag("head").empty();
			doc.getElementsByTag("head").append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\"></meta>");
			doc.getElementsByTag("head").append(" <style type=\"text/css\"></style>");
			doc.getElementsByTag("style").append(style);
			
			StringBuilder titleBuilder = new StringBuilder();
			titleBuilder.append(
			        "<p style=\"text-align: center;\"><span style=\"font-size: 36.0px;\"><span style=\"font-size: 36.0px;\"><strong><span id=\"title\" style=\"\">")
			        .append(title)
			        .append("</span></strong></span></p><p style=\"text-align: center;\"><span style=\"color: rgb(165,165,165);\">上传日期：")
			        .append(date).append("</span></p><p /><hr /><p>");
			String body = doc.getElementsByTag("body").html();
			doc.getElementsByTag("body").empty();
			doc.getElementsByTag("body").attr("class", "");
			doc.getElementsByTag("body").append("<div></div>");
			doc.getElementsByTag("div").append(body);
			doc.getElementsByTag("div").before(titleBuilder.toString());
			
			StringBuilder divStyleBuilder = new StringBuilder();
			divStyleBuilder.append("width:98%;margin-bottom:50.0pt;margin-top:10.0pt;margin-left:10.0pt;margin-right:10.0pt;");
			doc.getElementsByTag("div").attr("style", divStyleBuilder.toString());
			doc.getElementsByTag("div").attr("class", "b1 b2");
			String content = doc.html();
			return content;
		} catch (Exception e) {
			log.error("docToHtml wrong {}", e);
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(byteArrayOutputStream);
		}
		return "";
		
	}
	
	// 切割位置
	public static final int INDENT = 4;
	
	/**
	 * word2007及以上版本 转 HTML
	 * @param input
	 * @param webRootPath
	 * @param title
	 * @param date
	 * @return
	 * @throws TransformerException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @author huenbin
	 * @see [相关类/方法]
	 * @since V1.0.0
	 */
	public static String docxToHtml(InputStream input, String webRootPath, String title, String date) {
		// word2007 版本存储路径特殊修改
		XWPFDocumentVisitor.WORD_MEDIA = "word/2007/" + getImageFilePath();
		XHTMLMapper.WORD_MEDIA = XWPFDocumentVisitor.WORD_MEDIA;
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			XWPFDocument document = new XWPFDocument(input);
			XHTMLOptions options = XHTMLOptions.create().indent(INDENT);
			// 导出图片
			File imageFolder = new File(webRootPath);
			options.setExtractor(new FileImageExtractor(imageFolder));
			// URI resolver
			// options.URIResolver(new FileURIResolver(imageFolder));
			options.URIResolver(new BasicURIResolver("/"));
			byteArrayOutputStream = new ByteArrayOutputStream();
			XHTMLConverter.getInstance().convert(document, byteArrayOutputStream, options);
			String str = byteArrayOutputStream.toString();
			org.jsoup.nodes.Document doc = Jsoup.parse(str);
			// doc.getElementsByTag("head").empty();
			doc.getElementsByTag("head").append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\"></meta>");
			StringBuilder titleBuilder = new StringBuilder();
			titleBuilder.append(
			        "<p style=\"text-align: center;\"><span style=\"font-size: 36.0px;\"><span style=\"font-size: 36.0px;\"><strong><span  id=\"title\"  style=\"\">")
			        .append(title)
			        .append("</span></strong></span></p><p style=\"text-align: center;\"><br /><span style=\"color: rgb(165,165,165);\">上传日期：")
			        .append(date).append("</span></p><br /><p /><hr /><p><br /></p><p>");
			
			doc.getElementsByTag("div").before(titleBuilder.toString());
			StringBuilder divStyleBuilder = new StringBuilder();
			divStyleBuilder
			        .append("width:width:98%;;margin-bottom:50.0pt;margin-top:10.0pt;margin-left:10.0pt;margin-right:10.0pt;");
			doc.getElementsByTag("div").attr("style", divStyleBuilder.toString());
			String content = doc.html();
			return content;
		} catch (Exception e) {
			log.error("docxToHtml wrong {}", e);
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(byteArrayOutputStream);
		}
		return "";
	}
	
}
