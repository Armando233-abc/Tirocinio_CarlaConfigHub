package com.tirocinio.Composer.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ComposerService {

    public String composeConfiguration(Map<String, String> fragments) {

        // 1. Normalizzazione e pulizia frammenti
        String weatherFragment  = normalize(fragments.getOrDefault("weatherFragment", ""));
        String vehicleFragment  = normalize(fragments.getOrDefault("vehicleFragment", ""));
        String scenarioFragment = normalize(fragments.getOrDefault("scenarioFragment", ""));


        String roadNetwork     = extractSection(scenarioFragment, "RoadNetwork");


        String egoObject       = extractScenarioObject(vehicleFragment);
        String trafficObjects  = extractTrafficObjects(scenarioFragment);


        String weatherAction   = wrapWeatherAsGlobalAction(fixWeather(weatherFragment));
        String egoInit         = fixControllerActions(extractInitPrivate(vehicleFragment));
        String trafficInit     = fixControllerActions(extractInitPrivate(scenarioFragment));

        // 3. Composizione XML Finale
        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<OpenScenario>\n");

        // --- FILE HEADER ---
        xml.append("  <FileHeader revMajor=\"1\" revMinor=\"0\" author=\"CarlaConfigHub\" date=\"")
                .append(LocalDateTime.now()).append("\" description=\"Generazione Automatica\"/>\n\n");

        // --- CATALOG LOCATIONS (Obbligatorio per XSD) ---
        xml.append("  <CatalogLocations>\n");
        xml.append("    <VehicleCatalog><Directory path=\"\"/></VehicleCatalog>\n");
        xml.append("    <ControllerCatalog><Directory path=\"\"/></ControllerCatalog>\n");
        xml.append("  </CatalogLocations>\n\n");


        if (!roadNetwork.isEmpty()) {
            xml.append(indent(roadNetwork, 2)).append("\n\n");
        }

        // --- ENTITIES ---
        xml.append("  <Entities>\n");
        xml.append(indent(egoObject, 4)).append("\n");
        if (!trafficObjects.isEmpty()) {
            xml.append(indent(trafficObjects, 4)).append("\n");
        }
        xml.append("  </Entities>\n\n");

        // --- STORYBOARD ---
        xml.append("  <Storyboard>\n");
        xml.append("    <Init>\n");
        xml.append("      <Actions>\n");

        // Global Actions (Meteo)
        if (!weatherAction.isEmpty()) {
            xml.append(indent(weatherAction, 8)).append("\n");
        }

        // Private Actions (Ego + Traffico)
        if (!egoInit.isEmpty()) {
            xml.append(indent(egoInit, 8)).append("\n");
        }
        if (!trafficInit.isEmpty()) {
            xml.append(indent(trafficInit, 8)).append("\n");
        }

        xml.append("      </Actions>\n");
        xml.append("    </Init>\n");


        xml.append("    <Story name=\"MyStory\">\n");
        xml.append("      <Act name=\"MyAct\">\n");

        // 1. ManeuverGroup
        xml.append("        <ManeuverGroup maximumExecutionCount=\"1\" name=\"ManeuverGroup\">\n");
        xml.append("          <Actors selectTriggeringEntities=\"false\"/>\n");
        xml.append("        </ManeuverGroup>\n");

        // 2. StartTrigger
        xml.append("        <StartTrigger>\n");
        xml.append("          <ConditionGroup>\n");
        xml.append("            <Condition name=\"StartCondition\" delay=\"0\" conditionEdge=\"rising\">\n");
        xml.append("              <ByValueCondition>\n");
        xml.append("                <SimulationTimeCondition value=\"0\" rule=\"greaterThan\"/>\n");
        xml.append("              </ByValueCondition>\n");
        xml.append("            </Condition>\n");
        xml.append("          </ConditionGroup>\n");
        xml.append("        </StartTrigger>\n");


        xml.append("      </Act>\n");
        xml.append("    </Story>\n");

        xml.append("    <StopTrigger/>\n");
        xml.append("  </Storyboard>\n");

        xml.append("</OpenScenario>");

        return clean(xml.toString());
    }

    // -------------------------------------------------------------------------
    // METODI DI SUPPORTO E PARSING
    // -------------------------------------------------------------------------

    private String normalize(String xml) {
        if (xml == null) return "";
        return xml.trim();
    }

    private String extractSection(String xml, String tag) {
        Pattern p = Pattern.compile("<" + tag + "[\\s\\S]*?</" + tag + ">");
        Matcher m = p.matcher(xml);
        return m.find() ? m.group(0) : "";
    }

    private String extractScenarioObject(String xml) {
        return extractSection(xml, "ScenarioObject");
    }

    private String extractTrafficObjects(String xml) {
        StringBuilder sb = new StringBuilder();
        Pattern p = Pattern.compile("<ScenarioObject name=\"Traffic[\\s\\S]*?</ScenarioObject>");
        Matcher m = p.matcher(xml);
        while (m.find()) sb.append(m.group(0)).append("\n");
        return sb.toString().trim();
    }

    private String fixWeather(String weatherXml) {
        if (weatherXml.isEmpty()) return "";
        return weatherXml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").trim();
    }

    private String wrapWeatherAsGlobalAction(String weatherXml) {
        if (weatherXml.isEmpty()) return "";
        return "<GlobalAction>\n" +
                "  <EnvironmentAction>\n" +
                "    <Environment name=\"Environment1\">\n" +
                "      <TimeOfDay animation=\"false\" dateTime=\"" + LocalDateTime.now() + "\"/>\n" +
                "      " + weatherXml + "\n" +
                "      <RoadCondition frictionScaleFactor=\"1.0\"/>\n" +
                "    </Environment>\n" +
                "  </EnvironmentAction>\n" +
                "</GlobalAction>";
    }

    private String extractInitPrivate(String xml) {
        StringBuilder sb = new StringBuilder();
        Pattern p = Pattern.compile("<Private[\\s\\S]*?</Private>");
        Matcher m = p.matcher(xml);
        while (m.find()) sb.append(m.group(0)).append("\n");
        return sb.toString().trim();
    }

    private String fixControllerActions(String xml) {
        if (xml.isEmpty()) return "";
        if (xml.contains("<Controller>") && !xml.contains("<Properties")) {
            return xml.replace("</Controller>", "  <Properties/>\n</Controller>");
        }
        return xml;
    }

    private String indent(String text, int spaces) {
        if (text == null || text.isBlank()) return "";
        String margin = " ".repeat(spaces);
        return text.replaceAll("(?m)^", margin);
    }

    private String clean(String xml) {
        return xml.replaceAll("(?m)^[ \t]*\r?\n", "");
    }
}