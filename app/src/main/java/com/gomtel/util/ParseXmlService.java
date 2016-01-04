package com.gomtel.util;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *@author lqn
 *@date 2014-10-20
 */
public class ParseXmlService
{
	public HashMap<String, String> parseXml(InputStream inStream) throws Exception
	{
		HashMap<String, String> hashMap = new HashMap<String, String>();
		
		// 实锟斤拷锟斤拷一锟斤拷锟侥碉拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// 通锟斤拷锟侥碉拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷取一锟斤拷锟侥碉拷锟斤拷锟斤拷锟斤拷
		DocumentBuilder builder = factory.newDocumentBuilder();
		// 通锟斤拷锟侥碉拷通锟斤拷锟侥碉拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷一锟斤拷锟侥碉拷实锟斤拷
		Document document = builder.parse(inStream);
		//锟斤拷取XML锟侥硷拷锟斤拷锟节碉拷
		Element root = document.getDocumentElement();
		//锟斤拷锟斤拷锟斤拷锟斤拷咏诘锟�
		NodeList childNodes = root.getChildNodes();
		for (int j = 0; j < childNodes.getLength(); j++)
		{
			//锟斤拷锟斤拷锟接节碉拷
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element childElement = (Element) childNode;
				//锟芥本锟斤拷
				if ("version".equals(childElement.getNodeName()))
				{
					hashMap.put("version",childElement.getFirstChild().getNodeValue());
				}
				//强锟狡革拷锟铰憋拷志
				if ("compel".equals(childElement.getNodeName()))
				{
					hashMap.put("compel",childElement.getFirstChild().getNodeValue());
				}
				//锟斤拷锟斤拷锟斤拷锟�
				else if (("name".equals(childElement.getNodeName())))
				{
					hashMap.put("name",childElement.getFirstChild().getNodeValue());
				}
				//锟斤拷锟截碉拷址
				else if (("url".equals(childElement.getNodeName())))
				{
					hashMap.put("url",childElement.getFirstChild().getNodeValue());
				}
			}
		}
		return hashMap;
	}
}
