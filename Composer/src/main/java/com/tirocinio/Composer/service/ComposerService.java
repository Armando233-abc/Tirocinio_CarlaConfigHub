package com.tirocinio.Composer.service;


import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Map;

@Service
public class ComposerService {

    public String composeConfiguration(Map<String, String> fragments) {

        String weatherFragment = fragments.getOrDefault("weatherFragment", "");
        String vehicleFragment = fragments.getOrDefault("vehicleFragment", "");
        String scenarioFragment = fragments.getOrDefault("scenarioFragment", "");


        StringBuilder finalXml = new StringBuilder();
        finalXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        finalXml.append("<CarlaConfig>\n");


        finalXml.append(weatherFragment).append("\n");
        finalXml.append(vehicleFragment).append("\n");
        finalXml.append(scenarioFragment).append("\n");

        finalXml.append("</CarlaConfig>");

        String result = finalXml.toString();


        if (!isValidXml(result)) {
            throw new RuntimeException("L'XML generato non è valido o è corrotto.");
        }

        return result;
    }


    private boolean isValidXml(String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            return true;
        } catch (Exception e) {
            System.err.println("Validazione XML fallita: " + e.getMessage());
            return false;
        }
    }
}